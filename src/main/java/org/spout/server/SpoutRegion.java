/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server;

import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.event.entity.EntityDespawnEvent;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Blockm;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.api.util.set.TByteTripleHashSet;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.server.entity.EntityManager;
import org.spout.server.entity.SpoutEntity;
import org.spout.server.player.SpoutPlayer;
import org.spout.server.util.TripleInt;
import org.spout.server.util.thread.ThreadAsyncExecutor;
import org.spout.server.util.thread.snapshotable.SnapshotManager;

public class SpoutRegion extends Region {

	private AtomicInteger numberActiveChunks = new AtomicInteger();

	// Can't extend AsyncManager and Region
	private final SpoutRegionManager manager;

	private ConcurrentLinkedQueue<TripleInt> saveMarked = new ConcurrentLinkedQueue<TripleInt>();

	@SuppressWarnings("unchecked")
	public AtomicReference<SpoutChunk>[][][] chunks = new AtomicReference[Region.REGION_SIZE][Region.REGION_SIZE][Region.REGION_SIZE];

	/**
	 * Region coordinates of the lower, left start of the region. Add
	 * {@link Region#REGION_SIZE} to the coords to get the upper right end of
	 * the region.
	 */
	private final int x, y, z;

	private final int POPULATE_PER_TICK = 5;


	/**
	 * The source of this region
	 */
	private final RegionSource source;

	/**
	 * Snapshot manager for this region
	 */
	protected SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * Holds all of the entities to be simulated
	 */
	protected final EntityManager entityManager = new EntityManager();

	/**
	 * Holds all not populated chunks
	 */
	protected Set<Chunk> nonPopulatedChunks = Collections.newSetFromMap(new ConcurrentHashMap<Chunk, Boolean>());

	private boolean isPopulatingChunks = false;

	protected Queue<Chunk> unloadQueue = new ConcurrentLinkedQueue<Chunk>();

	public static final byte POPULATE_CHUNK_MARGIN = 1;

	/**
	 * A set of all blocks in this region that need a physics update in the next
	 * tick. The coordinates in this set are relative to this region, so (0, 0,
	 * 0) translates to (0 + x * 256, 0 + y * 256, 0 + z * 256)), where (x, y,
	 * z) are the region coordinates.
	 */
	//TODO thresholds?
	private final TByteTripleHashSet queuedPhysicsUpdates = new TByteTripleHashSet();

	private final int blockCoordMask;

	private final int blockShifts;

	private final Queue<Chunk> populationQueue = new ConcurrentLinkedQueue<Chunk>();

	private final Queue<Entity> spawnQueue = new ConcurrentLinkedQueue<Entity>();

	private final Queue<Entity> removeQueue = new ConcurrentLinkedQueue<Entity>();

	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source) {
		this(world, x, y, z, source, false);
	}

	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source, boolean load) {
		super(world, x, y, z);
		this.x = (int) Math.floor(x);
		this.y = (int) Math.floor(y);
		this.z = (int) Math.floor(z);
		this.source = source;
		blockCoordMask = Region.REGION_SIZE * Chunk.CHUNK_SIZE - 1;
		blockShifts = Region.REGION_SIZE_BITS + Chunk.CHUNK_SIZE_BITS;
		manager = new SpoutRegionManager(this, 2, new ThreadAsyncExecutor(), world.getServer());
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					chunks[dx][dy][dz] = new AtomicReference<SpoutChunk>(load ? getChunk(dx, dy, dz, true) : null);
				}
			}
		}
	}

	public SpoutWorld getWorld() {
		return (SpoutWorld) super.getWorld();
	}

	@Override
	@LiveRead
	public SpoutChunk getChunk(int x, int y, int z) {
		return getChunk(x, y, z, true);
	}

	@Override
	@LiveRead
	public SpoutChunk getChunk(int x, int y, int z, boolean load) {
		if (x < Region.REGION_SIZE && x >= 0 && y < Region.REGION_SIZE && y >= 0 && z < Region.REGION_SIZE && z >= 0) {
			SpoutChunk chunk = chunks[x][y][z].get();
			if (chunk != null || !load) {
				return chunk;
			}

			AtomicReference<SpoutChunk> ref = chunks[x][y][z];

			boolean success = false;

			while (!success) {
				int cx = (this.x << Region.REGION_SIZE_BITS) + x;
				int cy = (this.y << Region.REGION_SIZE_BITS) + y;
				int cz = (this.z << Region.REGION_SIZE_BITS) + z;

				CuboidShortBuffer buffer = new CuboidShortBuffer(getWorld(), cx << Chunk.CHUNK_SIZE_BITS, cy << Chunk.CHUNK_SIZE_BITS, cz << Chunk.CHUNK_SIZE_BITS, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);

				WorldGenerator generator = getWorld().getGenerator();
				generator.generate(buffer, cx, cy, cz);

				SpoutChunk newChunk = new SpoutChunk(getWorld(), this, cx, cy, cz, buffer.getRawArray());
				success = ref.compareAndSet(null, newChunk);

				if (success) {
					numberActiveChunks.incrementAndGet();
					if (!newChunk.isPopulated()) {
						nonPopulatedChunks.add(newChunk);
					}
					return newChunk;
				} else {
					SpoutChunk oldChunk = ref.get();
					if (oldChunk != null) {
						return oldChunk;
					}
				}
			}
		}
		throw new IndexOutOfBoundsException("Invalid coordinates (" + x + ", " + y + ", " + z + ")");
	}

	/**
	 * Removes a chunk from the region and indicates if the region is empty
	 *
	 * @param c the chunk to remove
	 * @return true if the region is now empty
	 */
	public boolean removeChunk(Chunk c) {
		if (c.getRegion() != this) {
			return false;
		}
		int cx = c.getX() & Region.REGION_SIZE - 1;
		int cy = c.getY() & Region.REGION_SIZE - 1;
		int cz = c.getZ() & Region.REGION_SIZE - 1;

		AtomicReference<SpoutChunk> current = chunks[cx][cy][cz];
		SpoutChunk currentChunk = current.get();
		if (currentChunk != c) {
			return false;
		}
		boolean success = current.compareAndSet(currentChunk, null);
		if (success) {
			int num = numberActiveChunks.decrementAndGet();

			((SpoutChunk) currentChunk).setUnloaded();

			if (num == 0) {
				return true;
			} else if (num < 0) {
				throw new IllegalStateException("Region has less than 0 active chunks");
			}
		}
		return false;
	}

	@Override
	public boolean hasChunk(int x, int y, int z) {
		if (x < Region.REGION_SIZE && x >= 0 && y < Region.REGION_SIZE && y >= 0 && z < Region.REGION_SIZE && z >= 0) {
			return chunks[x][y][z].get() != null;
		}
		return false;
	}

	SpoutRegionManager getManager() {
		return manager;
	}

	/**
	 * Queues a Chunk for saving
	 */
	@Override
	@DelayedWrite
	public void saveChunk(int x, int y, int z) {
		SpoutChunk c = getChunk(x, y, z, false);
		if (c != null) {
			c.save();
		}
	}

	/**
	 * Queues all chunks for saving
	 */
	@Override
	@DelayedWrite
	public void save() {
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					SpoutChunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunk.saveNoMark();
					}
				}
			}
		}
		markForSaveUnload();
	}

	@Override
	public void unload(boolean save) {
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					SpoutChunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunk.unloadNoMark(save);
					}
				}
			}
		}
		markForSaveUnload();
	}

	@Override
	public void unloadChunk(int x, int y, int z, boolean save) {
		SpoutChunk c = getChunk(x, y, z, false);
		if (c != null) {
			c.unload(save);
		}
	}

	public void markForSaveUnload(Chunk c) {
		if (c.getRegion() != this) {
			return;
		}
		int cx = c.getX() & Region.REGION_SIZE - 1;
		int cy = c.getY() & Region.REGION_SIZE - 1;
		int cz = c.getZ() & Region.REGION_SIZE - 1;

		markForSaveUnload(cx, cy, cz);
	}

	public void markForSaveUnload(int x, int y, int z) {
		saveMarked.add(new TripleInt(x, y, z));
	}

	public void markForSaveUnload() {
		saveMarked.add(TripleInt.NULL);
	}

	public void copySnapshotRun() throws InterruptedException {
		entityManager.copyAllSnapshots();

		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						((SpoutChunk) chunk).copySnapshotRun();
					}
				}
			}
		}
		snapshotManager.copyAllSnapshots();

		boolean empty = false;
		TripleInt chunkCoords;
		while ((chunkCoords = saveMarked.poll()) != null) {
			if (chunkCoords == TripleInt.NULL) {
				for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
					for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
						for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
							if (processChunkSaveUnload(dx, dy, dz)) {
								empty = true;
							}
						}
					}
				}
				// No point in checking any others, since all processed
				saveMarked.clear();
				break;
			} else {
				processChunkSaveUnload(chunkCoords.x, chunkCoords.y, chunkCoords.z);
			}
		}

		// Updates on nulled chunks
		snapshotManager.copyAllSnapshots();

		if (empty) {
			source.removeRegion(this);
		}

	}

	public boolean processChunkSaveUnload(int x, int y, int z) {
		boolean empty = false;
		SpoutChunk c = (SpoutChunk) getChunk(x, y, z, false);
		if (c != null) {
			SpoutChunk.SaveState oldState = c.getAndResetSaveState();
			if (oldState.isSave()) {
				c.syncSave();
			}
			if (oldState.isUnload()) {
				if (removeChunk(c)) {
					System.out.println("Region is now empty ... remove?");
					empty = true;
				}
			}
		}
		return empty;
	}

	public void queueChunkForPopulation(Chunk c) {
		if (populationQueue.contains(c)) return; //Ignore this chunk if we are already populating it.
		populationQueue.add(c);
	}


	public void addEntity(Entity e) {
		if (spawnQueue.contains(e)) return;
		if (removeQueue.contains(e)) {
			throw new IllegalArgumentException("Cannot add an entity marked for removal");
		}
		spawnQueue.add(e);

	}

	public void removeEntity(Entity e) {
		if (removeQueue.contains(e)) return;
		if (spawnQueue.contains(e)) {
			spawnQueue.remove(e);
			return;
		}
		removeQueue.add(e);


	}

	public void startTickRun(int stage, long delta) throws InterruptedException {
		switch (stage) {
			case 0: {
				//Add or remove entities
				if(!spawnQueue.isEmpty()) {
					SpoutEntity e;
					while ((e = (SpoutEntity)spawnQueue.poll()) != null) {
						this.allocate(e);
						EntitySpawnEvent event = new EntitySpawnEvent(e, e.getPoint());
						Spout.getGame().getEventManager().callEvent(event);
						if (event.isCancelled()) {
							this.deallocate((SpoutEntity) e);
						}
					}
				}

				if(!removeQueue.isEmpty()) {
					SpoutEntity e;
					while ((e = (SpoutEntity)removeQueue.poll()) != null) {
						this.deallocate(e);
					}
				}

				float dt = delta / 1000.f;
				//Update all entities
				for (SpoutEntity ent : entityManager) {
					try {
						ent.onTick(dt);
					} catch (Exception e) {
						Spout.getGame().getLogger().severe("Unhandled exception during tick for " + ent.toString());
						e.printStackTrace();
					}
				}

				World world = getWorld();
				int[] updates;
				synchronized (queuedPhysicsUpdates) {
					updates = queuedPhysicsUpdates.toArray();
					queuedPhysicsUpdates.clear();
				}
				for (int key : updates) {
					int x = TByteTripleHashSet.key1(key);
					int y = TByteTripleHashSet.key2(key);
					int z = TByteTripleHashSet.key3(key);
					//switch region block coords (0-255) to a chunk index
					Chunk chunk = chunks[x >> Chunk.CHUNK_SIZE_BITS][y >> Chunk.CHUNK_SIZE_BITS][z >> Chunk.CHUNK_SIZE_BITS].get();
					if (chunk != null) {
						BlockMaterial material = chunk.getBlockMaterial(x, y, z);
						if (material.hasPhysics()) {
							//switch region block coords (0-255) to world block coords
							material.onUpdate(world, x + (this.x << blockShifts), y + (this.y << blockShifts), z + (this.z << blockShifts));
						}
					}
				}

				for(int i = 0; i < POPULATE_PER_TICK; i++) {
					Chunk toPopulate = populationQueue.poll();
					if (toPopulate == null) break;
					if (toPopulate.isLoaded()){
						toPopulate.populate();
					}

				}


				Chunk toUnload = unloadQueue.poll();
				if (toUnload != null) {
					toUnload.unload(true);
				}


				break;
			}
			case 1: {
				//Resolve and collisions and prepare for a snapshot.
				for (SpoutEntity ent : entityManager) {
					try {
						ent.resolve();
					} catch (Exception e) {
						Spout.getGame().getLogger().severe("Unhandled exception during tick resolution for " + ent.toString());
						e.printStackTrace();
					}
				}
				break;
			}
			default: {
				throw new IllegalStateException("Number of states exceeded limit for SpoutRegion");
			}
		}
	}

	public void haltRun() throws InterruptedException {
	}


	public void finalizeRun() throws InterruptedException {
		entityManager.finalizeRun();

		// Compress at most 1 chunk per tick per region
		boolean chunkCompressed = false;

		for (int dx = 0; dx < Region.REGION_SIZE && !chunkCompressed; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE && !chunkCompressed; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE && !chunkCompressed; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunkCompressed |= ((SpoutChunk) chunk).compressIfRequired();
					}
				}
			}
		}
	}

	private void syncChunkToPlayers(SpoutChunk chunk, Entity entity){
		SpoutPlayer player = (SpoutPlayer)((PlayerController) entity.getController()).getPlayer();
		NetworkSynchronizer synchronizer = player.getNetworkSynchronizer();
		if (!chunk.isDirtyOverflow()) {
			for (int i = 0; true; i++) {
				Blockm block = chunk.getDirtyBlock(i, new SpoutBlockm(getWorld(), 0, 0, 0));
				if (block == null) {
					break;
				} else {
					try {
						synchronizer.updateBlock(chunk, block.getX(), block.getY(), block.getZ());
					} catch (Exception e) {
						Spout.getGame().getLogger().log(Level.SEVERE, "Exception thrown by plugin when attempting to send a block update to " + player.getName());

					}
				}
			}

		} else {
			synchronizer.sendChunk(chunk);
		}

	}

	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshotRun();

		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk == null) continue;
					SpoutChunk spoutChunk = (SpoutChunk) chunk;

					if (spoutChunk.isDirty()) {
						for (Entity entity : spoutChunk.getObserversLive()) {
							//chunk.refreshObserver(entity);
							if(!(entity.getController() instanceof PlayerController)) continue;
							syncChunkToPlayers(spoutChunk, entity);
						}
						spoutChunk.resetDirtyArrays();
					}
					spoutChunk.preSnapshot();

				}
			}
		}
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Set<Entity> getAll(Class<? extends Controller> type) {
		return (Set) entityManager.getAll(type);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Set<Entity> getAll() {
		return (Set) entityManager.getAll();
	}

	@Override
	public SpoutEntity getEntity(int id) {
		return entityManager.getEntity(id);
	}

	/**
	 * Allocates the id for an entity.
	 *
	 * @param entity The entity.
	 * @return The id.
	 */
	public int allocate(SpoutEntity entity) {
		return entityManager.allocate(entity);
	}

	/**
	 * Deallocates the id for an entity.
	 *
	 * @param entity The entity.
	 */
	public void deallocate(SpoutEntity entity) {
		entityManager.deallocate(entity);
	}

	public Iterator<SpoutEntity> iterator() {
		return entityManager.iterator();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void onChunkPopulated(SpoutChunk chunk) {
		if (!isPopulatingChunks) {
			nonPopulatedChunks.remove(chunk);
		}
	}

	/**
	 * Queues a block for a physic update at the next available tick.
	 *
	 * @param x, the block x coordinate
	 * @param y, the block y coordinate
	 * @param z, the block z coordinate
	 */
	public void queuePhysicsUpdate(int x, int y, int z) {
		synchronized (queuedPhysicsUpdates) {
			queuedPhysicsUpdates.add(x & blockCoordMask, y & blockCoordMask, z & blockCoordMask);
		}
	}

	@Override
	public int getNumLoadedChunks() {
		return numberActiveChunks.get();
	}

	@Override
	public String toString() {
		return "SpoutRegion{ ( " + x + ", " + y + ", " + z + "), World: " + this.getWorld() + "}";
	}
}
