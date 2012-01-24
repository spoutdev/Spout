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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Blockm;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.server.entity.EntityManager;
import org.spout.server.entity.SpoutEntity;
import org.spout.server.player.SpoutPlayer;
import org.spout.server.util.TripleInt;
import org.spout.server.util.thread.ThreadAsyncExecutor;
import org.spout.server.util.thread.snapshotable.SnapshotManager;

public class SpoutRegion extends Region{
	
	private AtomicInteger numberActiveChunks = new AtomicInteger();

	// Can't extend AsyncManager and Region
	private final SpoutRegionManager manager;

	private ConcurrentLinkedQueue<TripleInt> saveMarked = new ConcurrentLinkedQueue<TripleInt>();

	@SuppressWarnings("unchecked")
	public AtomicReference<Chunk>[][][] chunks = new AtomicReference[Region.REGION_SIZE][Region.REGION_SIZE][Region.REGION_SIZE];

	/**
	 * Region coordinates of the lower, left start of the region. Add {@link Region#REGION_SIZE} to the coords to get the upper right end of the region.
	 */
	private final int x, y, z;

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
	protected Set<Chunk> nonPopulatedChunks = Collections.newSetFromMap(new ConcurrentHashMap<Chunk,Boolean>());
	
	private boolean isPopulatingChunks = false;
	
	public static final byte POPULATE_CHUNK_MARGIN = 1;
	
	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source) {
		this(world, x, y, z, source, false);
	}

	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source, boolean load) {
		super(world, x, y, z);
		this.x = (int) Math.floor(x);
		this.y = (int) Math.floor(y);
		this.z = (int) Math.floor(z);
		this.source = source;
		this.manager = new SpoutRegionManager(this, 1, new ThreadAsyncExecutor(), world.getServer());
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					chunks[dx][dy][dz] = new AtomicReference<Chunk>(load ? getChunk(dx, dy, dz, true) : null);
				}
			}
		}
	}
	
	@Override
	@LiveRead
	public Chunk getChunk(int x, int y, int z) {
		return getChunk(x, y, z, true);
	}

	@Override
	@LiveRead
	public Chunk getChunk(int x, int y, int z, boolean load) {
		if (x < Region.REGION_SIZE && x >= 0 && y < Region.REGION_SIZE && y >= 0 && z < Region.REGION_SIZE && z >= 0) {
			Chunk chunk = chunks[x][y][z].get();
			if (chunk != null || !load) {
				return chunk;
			}

			AtomicReference<Chunk> ref = chunks[x][y][z];

			boolean success = false;

			while (!success) {
				int cx = (this.x << Region.REGION_SIZE_BITS) + x;
				int cy = (this.y << Region.REGION_SIZE_BITS) + y;
				int cz = (this.z << Region.REGION_SIZE_BITS) + z;
				
				CuboidShortBuffer buffer = new CuboidShortBuffer(getWorld(), cx << Chunk.CHUNK_SIZE_BITS, cy << Chunk.CHUNK_SIZE_BITS, cz << Chunk.CHUNK_SIZE_BITS, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);

				WorldGenerator generator = getWorld().getGenerator();
				generator.generate(buffer, cx, cy, cz);
				
				SpoutChunk newChunk = new SpoutChunk(getWorld(), this, cx, cy , cz, buffer.getRawArray());
				success = ref.compareAndSet(null, newChunk);

				if (success) {
					numberActiveChunks.incrementAndGet();
					if(!newChunk.isPopulated()) {
						nonPopulatedChunks.add(newChunk);
					}
					return newChunk;
				} else {
					Chunk oldChunk = ref.get();
					if (oldChunk != null) {
						return oldChunk;
					}
				}
			}
		}
		throw new IndexOutOfBoundsException("Invalid coordinates");
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
		int cx = c.getX() & (Region.REGION_SIZE - 1);
		int cy = c.getY() & (Region.REGION_SIZE - 1);
		int cz = c.getZ() & (Region.REGION_SIZE - 1);
		
		AtomicReference<Chunk> current = chunks[cx][cy][cz];
		Chunk currentChunk = current.get();
		if (currentChunk != c) {
			return false;
		}
		boolean success = current.compareAndSet(currentChunk, null);
		if (success) {
			int num = numberActiveChunks.decrementAndGet();
			
			((SpoutChunk)currentChunk).setUnloaded();
			
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
	@DelayedWrite
	public void saveChunk(int x, int y, int z) {
		Chunk c = getChunk(x, y, z, false);
		if (c != null) {
			c.save();
		}
	}

	/**
	 * Queues all chunks for saving
	 */
	@DelayedWrite
	public void save() {
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						((SpoutChunk)chunk).saveNoMark();
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
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						((SpoutChunk)chunk).unloadNoMark(save);
					}
				}
			}
		}
		markForSaveUnload();
	}

	public void unloadChunk(int x, int y, int z, boolean save) {
		Chunk c = getChunk(x, y, z, false);
		if (c != null) {
			c.unload(save);
		}
	}
	
	public void markForSaveUnload(Chunk c) {
		if (c.getRegion() != this) {
			return;
		}
		int cx = c.getX() & (Region.REGION_SIZE - 1);
		int cy = c.getY() & (Region.REGION_SIZE - 1);
		int cz = c.getZ() & (Region.REGION_SIZE - 1);
		
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
		SpoutChunk c = (SpoutChunk)getChunk(x, y, z, false);
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

	public void startTickRun(int stage, long delta) throws InterruptedException {
		switch (stage) {
			case 0: {
				float dt = delta / 1000.f;
				//Update all entities
				for (SpoutEntity ent : entityManager) {
					try {
						ent.onTick(dt);
					}
					catch (Exception e){
						Spout.getGame().getLogger().severe("Unhandled exception during tick for " + ent.toString());
						e.printStackTrace();
					}
				}
				break;
			}
			case 1: {
				//Resolve and collisions and prepare for a snapshot.
				for (SpoutEntity ent : entityManager) {
					try {
						ent.resolve();
					}
					catch (Exception e){
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
		isPopulatingChunks = true;
		try {
			Iterator<Chunk> iter = nonPopulatedChunks.iterator();
			World world = getWorld();
			int chunkHeight = world.getHeight() >> Chunk.CHUNK_SIZE_BITS - 1;
			while(iter.hasNext()) {
				Chunk c = iter.next();
				if(c.isUnloaded()) {
					iter.remove();
				}
				if(c.isPopulated()) {
					//Cleanup
					iter.remove();
					continue;
				}
				int x = c.getX();
				int y = c.getY();
				int z = c.getZ();
				boolean success = true;
				int missing = 0;
				seek: for(int dx = x - POPULATE_CHUNK_MARGIN; dx <= x + POPULATE_CHUNK_MARGIN; dx++) {
					for(int dy = Math.max(y - POPULATE_CHUNK_MARGIN, 0); dy <= Math.min(y + POPULATE_CHUNK_MARGIN, chunkHeight); dy++) {
						for(int dz = z - POPULATE_CHUNK_MARGIN; dz <= z + POPULATE_CHUNK_MARGIN; dz++) {
							Chunk neighbor = world.getChunk(dx, dy, dz, false);
							if(neighbor == null) {
								success = false;
								missing ++;
							}
						}
					}
				}
				//System.out.println(x + " " + y + " " + z + " "+ missing + " missing");
				if(success) {
					c.populate();
					iter.remove();
				}
			}
		} finally {
			isPopulatingChunks = false;
		}

		// Compress at most 1 chunk per tick per region
		boolean chunkCompressed = false;
		
		for (int dx = 0; dx < Region.REGION_SIZE && !chunkCompressed; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE && !chunkCompressed; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE && !chunkCompressed; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunkCompressed |= ((SpoutChunk)chunk).compressIfRequired();
					}
				}
			}
		}
	}
	
	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshotRun();
		
		Blockm blockTemp = new SpoutBlockm(this.getWorld(), 0, 0, 0);
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						SpoutChunk spoutChunk = (SpoutChunk)chunk;
						if (spoutChunk.isDirty()) {
							for (Player player : spoutChunk.getObservers()) {
								NetworkSynchronizer synchronizer = ((SpoutPlayer)player).getNetworkSynchronizer();
								if (!spoutChunk.isDirtyOverflow()) {
									Blockm block = blockTemp;
									for (int i = 0; true; i++) {
										block = spoutChunk.getDirtyBlock(i, block);
										if (block == null) {
											break;
										} else {
											try {
												synchronizer.updateBlock(spoutChunk, block);
											} catch (Exception e) {
												Spout.getGame().getLogger().log(Level.SEVERE, "Exception thrown by plugin when attempting to send a block update to " + player.getName());
												e.printStackTrace();
											}
										}
									}
								} else {
									synchronizer.sendChunk(spoutChunk);
								}
							}
							spoutChunk.resetDirtyArrays();
						}
					}
				}
			}
		}
	}

	@SuppressWarnings( {"rawtypes", "unchecked"})
	public Collection<Entity> getAll(Class<? extends Controller> type) {
		return (Collection<Entity>) (Collection) entityManager.getAll(type);
	}

	@SuppressWarnings( {"rawtypes", "unchecked"})
	public Collection<Entity> getAll() {
		return (Collection<Entity>) (Collection) entityManager.getAll();
	}

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
		if(!isPopulatingChunks) {
			nonPopulatedChunks.remove(chunk);
		}
	}
}
