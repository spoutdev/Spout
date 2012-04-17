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
package org.spout.engine.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.material.BlockMaterial;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.api.util.set.TByteTripleHashSet;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.util.TripleInt;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;

public class SpoutRegion extends Region {

	private AtomicInteger numberActiveChunks = new AtomicInteger();

	// Can't extend AsyncManager and Region
	private final SpoutRegionManager manager;

	private ConcurrentLinkedQueue<TripleInt> saveMarked = new ConcurrentLinkedQueue<TripleInt>();

	@SuppressWarnings("unchecked")
	public AtomicReference<SpoutChunk>[][][] chunks = new AtomicReference[Region.REGION_SIZE][Region.REGION_SIZE][Region.REGION_SIZE];

	/**
	 * The maximum number of chunks that will be processed for population each
	 * tick.
	 */
	private static final int POPULATE_PER_TICK = 20;

	/**
	 * The maximum number of chunks that will be reaped by the chunk reaper each
	 * tick.
	 */
	private static final int REAP_PER_TICK = 1;

	/**
	 * The maximum number of chunks that will be processed for lighting updates
	 * each tick.
	 */
	private static final int LIGHT_PER_TICK = 20;

	/**
	 * The segment size to use for chunk storage. The actual size is
	 * 2^(SEGMENT_SIZE)
	 */
	private final int SEGMENT_SIZE = 8;

	/**
	 * The number of chunks in a region
	 */
	private final int REGION_SIZE_CUBED = REGION_SIZE * REGION_SIZE * REGION_SIZE;

	/**
	 * The timeout for the chunk storage in ms. If the store isn't accessed
	 * within that time, it can be automatically shutdown
	 */
	public static final int TIMEOUT = 30000;

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
	 * Reference to the persistent ByteArrayArray that stores chunk data
	 */
	private final BAAWrapper chunkStore;

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

	/**
	 * A queue of chunks that have columns of light that need to be recalculated
	 */
	private final Queue<SpoutChunk> lightingQueue = new ConcurrentLinkedQueue<SpoutChunk>();

	/**
	 * A queue of chunks that need to be populated
	 */
	private final Queue<Chunk> populationQueue = new ConcurrentLinkedQueue<Chunk>();

	private final Queue<Entity> spawnQueue = new ConcurrentLinkedQueue<Entity>();

	private final Queue<Entity> removeQueue = new ConcurrentLinkedQueue<Entity>();

	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source) {
		this(world, x, y, z, source, false);
	}

	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source, boolean load) {
		super(world, x * Region.EDGE, y * Region.EDGE, z * Region.EDGE);
		this.source = source;
		blockCoordMask = Region.REGION_SIZE * Chunk.CHUNK_SIZE - 1;
		blockShifts = Region.REGION_SIZE_BITS + Chunk.CHUNK_SIZE_BITS;
		manager = new SpoutRegionManager(this, 2, new ThreadAsyncExecutor(this.toString() + " Thread"), world.getServer());

		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					chunks[dx][dy][dz] = new AtomicReference<SpoutChunk>(load ? getChunk(dx, dy, dz, true) : null);
				}
			}
		}

		File worldDirectory = world.getDirectory();
		File regionDirectory = new File(worldDirectory, "region");
		regionDirectory.mkdirs();
		File regionFile = new File(regionDirectory, "reg" + getX() + "_" + getY() + "_" + getZ() + ".spr");
		this.chunkStore = new BAAWrapper(regionFile, SEGMENT_SIZE, REGION_SIZE_CUBED, TIMEOUT);
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

			SpoutChunk newChunk = loadChunk(x, y, z);
			if (newChunk == null) {
				newChunk = generateChunk(x, y, z);
			}

			while (!success) {
				success = ref.compareAndSet(null, newChunk);

				if (success) {
					newChunk.notifyColumn();
					numberActiveChunks.incrementAndGet();
					if (!newChunk.isPopulated()) {
						nonPopulatedChunks.add(newChunk);
					}
					return newChunk;
				} else {
					newChunk.deregisterFromColumn(false);
					SpoutChunk oldChunk = ref.get();
					if (oldChunk != null) {
						return oldChunk;
					}
				}
			}
		}
		throw new IndexOutOfBoundsException("Invalid coordinates (" + x + ", " + y + ", " + z + ")");
	}

	private SpoutChunk generateChunk(int x, int y, int z) {
		int cx = (getX() << Region.REGION_SIZE_BITS) + x;
		int cy = (getY() << Region.REGION_SIZE_BITS) + y;
		int cz = (getZ() << Region.REGION_SIZE_BITS) + z;

		CuboidShortBuffer buffer = new CuboidShortBuffer(getWorld(), cx << Chunk.CHUNK_SIZE_BITS, cy << Chunk.CHUNK_SIZE_BITS, cz << Chunk.CHUNK_SIZE_BITS, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);

		WorldGenerator generator = getWorld().getGenerator();
		generator.generate(buffer, cx, cy, cz);

		return new SpoutChunk(getWorld(), this, cx, cy, cz, buffer.getRawArray());
	}

	private SpoutChunk loadChunk(int x, int y, int z) {
		SpoutChunk newChunk = null;
		NBTInputStream is = null;
		DataInputStream dis = null;

		try {
			dis = getChunkInputStream(x, y, z);
			if (dis == null) {
				//The inputstream is null because no chunk data exists
				return newChunk;
			}

			is = new NBTInputStream(dis, false);
			CompoundTag chunkTag = (CompoundTag) is.readTag();
			CompoundMap map = chunkTag.getValue();
			int cx = (getX() << Region.REGION_SIZE_BITS) + x;
			int cy = (getY() << Region.REGION_SIZE_BITS) + y;
			int cz = (getZ() << Region.REGION_SIZE_BITS) + z;
			
			boolean populated = ((ByteTag)map.get("populated")).getBooleanValue();
			short[] blocks = (short[]) map.get("blocks").getValue();
			short[] data = (short[]) map.get("data").getValue();
			byte[] skyLight = (byte[]) map.get("skyLight").getValue();
			byte[] blockLight = (byte[]) map.get("blockLight").getValue();
			
			newChunk = new SpoutChunk(getWorld(), this, cx, cy, cz, populated, blocks, data, skyLight, blockLight);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}

		return newChunk;
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

	protected void queueChunkForPopulation(Chunk c) {
		if (!populationQueue.contains(c)) {
			populationQueue.add(c);
		}
	}

	protected void queueLighting(SpoutChunk c) {
		if (!lightingQueue.contains(c)) {
			lightingQueue.add(c);
		}
	}

	public void addEntity(Entity e) {
		if (spawnQueue.contains(e))
			return;
		if (removeQueue.contains(e)) {
			throw new IllegalArgumentException("Cannot add an entity marked for removal");
		}
		spawnQueue.add(e);

	}

	public void removeEntity(Entity e) {
		if (removeQueue.contains(e))
			return;
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
				if (!spawnQueue.isEmpty()) {
					SpoutEntity e;
					while ((e = (SpoutEntity) spawnQueue.poll()) != null) {
						this.allocate(e);
						EntitySpawnEvent event = new EntitySpawnEvent(e, e.getPosition());
						Spout.getEngine().getEventManager().callEvent(event);
						if (event.isCancelled()) {
							this.deallocate((SpoutEntity) e);
						}
					}
				}

				if (!removeQueue.isEmpty()) {
					SpoutEntity e;
					while ((e = (SpoutEntity) removeQueue.poll()) != null) {
						this.deallocate(e);
					}
				}

				float dt = delta / 1000.f;
				//Update all entities
				for (SpoutEntity ent : entityManager) {
					try {
						ent.onTick(dt);
					} catch (Exception e) {
						Spout.getEngine().getLogger().severe("Unhandled exception during tick for " + ent.toString());
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
							material.onUpdate(world, x + (getX() << blockShifts), y + (getY() << blockShifts), z + (getZ() << blockShifts));
						}
					}
				}

				for (int i = 0; i < LIGHT_PER_TICK; i++) {
					SpoutChunk toLight = lightingQueue.poll();
					if (toLight == null)
						break;
					if (toLight.isLoaded()) {
						toLight.processQueuedLighting();
					}
				}

				for (int i = 0; i < POPULATE_PER_TICK; i++) {
					Chunk toPopulate = populationQueue.poll();
					if (toPopulate == null)
						break;
					if (toPopulate.isLoaded()) {
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
						Spout.getEngine().getLogger().severe("Unhandled exception during tick resolution for " + ent.toString());
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

		int reaped = 0;

		long worldAge = getWorld().getAge();

		for (int dx = 0; dx < Region.REGION_SIZE && !chunkCompressed; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE && !chunkCompressed; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE && !chunkCompressed; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunkCompressed |= ((SpoutChunk) chunk).compressIfRequired();

						if (reaped < REAP_PER_TICK && ((SpoutChunk) chunk).isReapable(worldAge)) {
							((SpoutChunk) chunk).unload(true);
							reaped++;
						}
					}
				}
			}
		}
	}

	private void syncChunkToPlayers(SpoutChunk chunk, Entity entity) {
		SpoutPlayer player = (SpoutPlayer) ((PlayerController) entity.getController()).getPlayer();
		NetworkSynchronizer synchronizer = player.getNetworkSynchronizer();
		if (synchronizer != null) {
			if (!chunk.isDirtyOverflow()) {
				for (int i = 0; true; i++) {
					Block block = chunk.getDirtyBlock(i, new SpoutBlock(getWorld(), 0, 0, 0, null));
					if (block == null) {
						break;
					} else {
						try {
							synchronizer.updateBlock(chunk, block.getX(), block.getY(), block.getZ());
						} catch (Exception e) {
							Spout.getEngine().getLogger().log(Level.SEVERE, "Exception thrown by plugin when attempting to send a block update to " + player.getName());

						}
					}
				}

			} else {
				synchronizer.sendChunk(chunk);
			}
		}
	}

	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshotRun();

		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk == null)
						continue;
					SpoutChunk spoutChunk = (SpoutChunk) chunk;

					if (spoutChunk.isDirty()) {
						for (Entity entity : spoutChunk.getObserversLive()) {
							//chunk.refreshObserver(entity);
							if (!(entity.getController() instanceof PlayerController))
								continue;
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
		return "SpoutRegion{ ( " + getX() + ", " + getY() + ", " + getZ() + "), World: " + this.getWorld() + "}";
	}

	public Thread getExceutionThread() {

		return ((ThreadAsyncExecutor) manager.getExecutor());
	}

	/**
	 * This method should be called periodically in order to see if the Chunk
	 * Store ByteArrayArray has timed out.<br>
	 * <br>
	 * It will only close the array if no block OutputStreams are open and the
	 * last access occurred more than the timeout previously
	 */
	public void chunkStoreTimeoutCheck() {
		chunkStore.timeoutCheck();
	}

	/**
	 * Gets the DataOutputStream corresponding to a given Chunk.<br>
	 * <br>
	 * WARNING: This block will be locked until the stream is closed
	 * 
	 * @param c the chunk
	 * @return the DataOutputStream
	 */
	public DataOutputStream getChunkOutputStream(Chunk c) {
		int key = getChunkKey(c.getX(), c.getY(), c.getZ());
		return chunkStore.getBlockOutputStream(key);
	}

	/**
	 * Gets the DataInputStream corresponding to a given Chunk.<br>
	 * <br>
	 * The stream is based on a snapshot of the array.
	 * 
	 * @param c the chunk
	 * @return the DataInputStream
	 */
	public DataInputStream getChunkInputStream(int x, int y, int z) {
		int key = getChunkKey(x, y, z);
		return chunkStore.getBlockInputStream(key);
	}

	private int getChunkKey(int chunkX, int chunkY, int chunkZ) {
		int x = chunkX & (Region.REGION_SIZE - 1);
		int y = chunkY & (Region.REGION_SIZE - 1);
		int z = chunkZ & (Region.REGION_SIZE - 1);

		int key = 0;
		key |= x;
		key |= y << (Region.REGION_SIZE_BITS);
		key |= z << (Region.REGION_SIZE_BITS << 1);

		return key;
	}
}
