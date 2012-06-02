/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import gnu.trove.iterator.TIntIterator;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.datatable.DataMap;
import org.spout.api.entity.BlockController;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.event.chunk.ChunkLoadEvent;
import org.spout.api.event.chunk.ChunkPopulateEvent;
import org.spout.api.event.chunk.ChunkUnloadEvent;
import org.spout.api.event.chunk.ChunkUpdatedEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.Vector3;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.api.util.map.TByteTripleObjectHashMap;
import org.spout.api.util.set.TByteTripleHashSet;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.RegionEntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.ChunkDataForRegion;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.TripleInt;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;
import org.spout.engine.world.dynamic.DynamicBlockUpdateTree;

public class SpoutRegion extends Region{
	private AtomicInteger numberActiveChunks = new AtomicInteger();
	// Can't extend AsyncManager and Region
	private final SpoutRegionManager manager;
	private ConcurrentLinkedQueue<TripleInt> saveMarked = new ConcurrentLinkedQueue<TripleInt>();
	@SuppressWarnings("unchecked")
	public AtomicReference<SpoutChunk>[][][] chunks = new AtomicReference[CHUNKS.SIZE][CHUNKS.SIZE][CHUNKS.SIZE];
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
	 * The segment size to use for chunk storage. The actual size is
	 * 2^(SEGMENT_SIZE)
	 */
	private final int SEGMENT_SIZE = 8;
	/**
	 * The timeout for the chunk storage in ms. If the store isn't accessed
	 * within that time, it can be automatically shutdown
	 */
	public static final int TIMEOUT = 30000;
	/**
	 * How many ticks to delay sending the entire chunk after lighting calculation has completed
	 */
	public static final int LIGHT_SEND_TICK_DELAY = 10;
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
	protected final RegionEntityManager entityManager = new RegionEntityManager(this);
	/**
	 * Reference to the persistent ByteArrayArray that stores chunk data
	 */
	private final BAAWrapper chunkStore;

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
	private final TByteTripleObjectHashMap<Source> queuedPhysicsUpdates = new TByteTripleObjectHashMap<Source>();
	/**
	 * The chunks that received a lighting change and need an update
	 */
	private final TByteTripleHashSet lightDirtyChunks = new TByteTripleHashSet();

	/**
	 * A queue of chunks that need to be populated
	 */
	final Queue<Chunk> populationQueue = new ConcurrentLinkedQueue<Chunk>();
	final Set<Chunk> populationQueueSet = Collections.newSetFromMap(new ConcurrentHashMap<Chunk, Boolean>());
	private final Map<Vector3, Entity> blockEntities = new HashMap<Vector3, Entity>();
	
	private final SpoutTaskManager taskManager;
	
	private final Thread executionThread;
	
	private final LinkedHashSet<SpoutChunk> occupiedChunks = new LinkedHashSet<SpoutChunk>();
	private final ConcurrentLinkedQueue<SpoutChunk> occupiedChunksQueue = new ConcurrentLinkedQueue<SpoutChunk>();
	
	private final DynamicBlockUpdateTree dynamicBlockTree = new DynamicBlockUpdateTree(this);
	private List<DynamicBlockUpdate> multiRegionUpdates = null;

	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source) {
		this(world, x, y, z, source, LoadOption.NO_LOAD);
	}

	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source, LoadOption loadopt) {
		super(world, x * Region.BLOCKS.SIZE, y * Region.BLOCKS.SIZE, z * Region.BLOCKS.SIZE);
		this.source = source;
		manager = new SpoutRegionManager(this, 2, new ThreadAsyncExecutor(this.toString() + " Thread"), world.getEngine());
		
		AsyncExecutor ae = manager.getExecutor();
		if (ae instanceof Thread) {
			executionThread = (Thread) ae;
		} else {
			executionThread = null;
		}

		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
					chunks[dx][dy][dz] = new AtomicReference<SpoutChunk>(loadopt.loadIfNeeded() ? getChunk(dx, dy, dz, loadopt) : null);
				}
			}
		}

		File worldDirectory = world.getDirectory();
		File regionDirectory = new File(worldDirectory, "region");
		regionDirectory.mkdirs();
		File regionFile = new File(regionDirectory, "reg" + getX() + "_" + getY() + "_" + getZ() + ".spr");
		this.chunkStore = new BAAWrapper(regionFile, SEGMENT_SIZE, CHUNKS.VOLUME, TIMEOUT);
		Thread t;
		AsyncExecutor e = manager.getExecutor();
		if (e instanceof Thread) {
			t = (Thread)e;
		} else {
			throw new IllegalStateException("AsyncExecutor should be instance of Thread");
		}
		taskManager = new SpoutTaskManager(world.getEngine().getScheduler(), false, t, world.getAge());
	}

	@Override
	public SpoutWorld getWorld() {
		return (SpoutWorld) super.getWorld();
	}

	@Override
	@LiveRead
	public SpoutChunk getChunk(int x, int y, int z) {
		return getChunk(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	@LiveRead
	public SpoutChunk getChunk(int x, int y, int z, LoadOption loadopt) {
		if (this.containsChunk(x, y, z)) {
			x &= CHUNKS.MASK;
			y &= CHUNKS.MASK;
			z &= CHUNKS.MASK;

			SpoutChunk chunk = chunks[x][y][z].get();
			if (chunk != null || (!loadopt.loadIfNeeded())) {
				return chunk;
			}

			AtomicReference<SpoutChunk> ref = chunks[x][y][z];

			boolean success = false;

			ChunkDataForRegion dataForRegion = new ChunkDataForRegion();

			SpoutChunk newChunk = WorldFiles.loadChunk(this, x, y, z, this.getChunkInputStream(x, y, z), dataForRegion);

			boolean generated = false;

			if (newChunk == null) {
				if (!loadopt.generateIfNeeded()) {
					return null;
				}
				newChunk = generateChunk(x, y, z);
				generated = true;
			}

			while (!success) {
				success = ref.compareAndSet(null, newChunk);

				if (success) {
					newChunk.notifyColumn();
					numberActiveChunks.incrementAndGet();
					for (SpoutEntity entity : dataForRegion.loadedEntities) {
						entity.setupInitialChunk(entity.getTransform());
						addEntity(entity);
					}
					dynamicBlockTree.addDynamicBlockUpdates(dataForRegion.loadedUpdates);
					occupiedChunksQueue.add(newChunk);

					Spout.getEventManager().callDelayedEvent(new ChunkLoadEvent(newChunk, generated));
					
					if (!newChunk.isPopulated()) {
						queueChunkForPopulation(newChunk);
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
		return this.getWorld().getChunk(x, y, z, loadopt);
	}

	@Override
	public Chunk getChunkFromBlock(Vector3 position) {
		return this.getChunkFromBlock(position, LoadOption.LOAD_GEN);
	}

	@Override
	public Chunk getChunkFromBlock(Vector3 position, LoadOption loadopt) {
		return this.getChunkFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ(), loadopt);
	}

	@Override
	public Chunk getChunkFromBlock(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public Chunk getChunkFromBlock(int x, int y, int z, LoadOption loadopt) {
		return this.getChunk(x >> Chunk.BLOCKS.BITS, y >> Chunk.BLOCKS.BITS, z >> Chunk.BLOCKS.BITS, loadopt);
	}

	private SpoutChunk generateChunk(int x, int y, int z) {
		x = this.getChunkX(x);
		y = this.getChunkY(y);
		z = this.getChunkZ(z);

		CuboidShortBuffer buffer = new CuboidShortBuffer(getWorld(), x << Chunk.BLOCKS.BITS, y << Chunk.BLOCKS.BITS, z << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE);

		WorldGenerator generator = getWorld().getGenerator();
		BiomeManager manager = generator.generate(buffer, x, y, z);

		return new FilteredChunk(getWorld(), this, x, y, z, buffer.getRawArray(), manager, (DataMap)buffer.getDataMap());
	}

	/**
	 * Removes a chunk from the region and indicates if the region is empty
	 * @param c the chunk to remove
	 * @return true if the region is now empty
	 */
	public boolean removeChunk(Chunk c) {
		if (c.getRegion() != this) {
			return false;
		}
		int cx = c.getX() & CHUNKS.MASK;
		int cy = c.getY() & CHUNKS.MASK;
		int cz = c.getZ() & CHUNKS.MASK;

		AtomicReference<SpoutChunk> current = chunks[cx][cy][cz];
		SpoutChunk currentChunk = current.get();
		if (currentChunk != c) {
			return false;
		}
		boolean success = current.compareAndSet(currentChunk, null);
		if (success) {
			int num = numberActiveChunks.decrementAndGet();
			
			for (Entity e : currentChunk.getLiveEntities()) {
				e.kill();
			}

			((SpoutChunk) currentChunk).setUnloaded();

			occupiedChunksQueue.remove(currentChunk);
			occupiedChunks.remove(currentChunk);
			
			removeDynamicBlockUpdates(currentChunk);
			
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
		if (this.containsChunk(x, y, z)) {
			return chunks[x & CHUNKS.MASK][y & CHUNKS.MASK][z & CHUNKS.MASK].get() != null;
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
		SpoutChunk c = getChunk(x, y, z, LoadOption.NO_LOAD);
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
		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
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
		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
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
		SpoutChunk c = getChunk(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			c.unload(save);
		}
	}

	public void markForSaveUnload(Chunk c) {
		if (c.getRegion() != this) {
			return;
		}
		int cx = c.getX() & CHUNKS.MASK;
		int cy = c.getY() & CHUNKS.MASK;
		int cz = c.getZ() & CHUNKS.MASK;

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

		Iterator<SpoutChunk> itr = occupiedChunks.iterator();
		
		while (itr.hasNext()) {
			// NOTE : This is only called for chunks with contain entities.
			SpoutChunk c = itr.next();
			if (c.copySnapshotRun()) {
				itr.remove();
			}
		}
		
		snapshotManager.copyAllSnapshots();

		boolean empty = false;
		TripleInt chunkCoords;
		while ((chunkCoords = saveMarked.poll()) != null) {
			if (chunkCoords == TripleInt.NULL) {
				for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
					for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
						for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
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
				empty |= processChunkSaveUnload(chunkCoords.x, chunkCoords.y, chunkCoords.z);
			}
		}

		// Updates on nulled chunks
		snapshotManager.copyAllSnapshots();

		if (empty) {
			source.removeRegion(this);
		}


		if (multiRegionUpdates != null) {
			final List<DynamicBlockUpdate> finalMultiRegionUpdates = multiRegionUpdates;
			final DynamicBlockUpdateTree finalTree = dynamicBlockTree;
			final long worldAge = getWorld().getAge();
			multiRegionUpdates = null;

			((SpoutScheduler)Spout.getScheduler()).scheduleCoreTask(new Runnable() {
				@Override
				public void run() {
					for (DynamicBlockUpdate dm : finalMultiRegionUpdates) {
						finalTree.updateDynamicBlock(worldAge, dm, true);
					}
				}
			});
		}
	}

	public boolean processChunkSaveUnload(int x, int y, int z) {
		boolean empty = false;
		SpoutChunk c = (SpoutChunk) getChunk(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			SpoutChunk.SaveState oldState = c.getAndResetSaveState();
			if (oldState.isSave()) {
				c.syncSave();
			}
			if (oldState.isUnload()) {
				if (removeChunk(c)) {
					empty = true;
				}
			}
		}
		return empty;
	}

	protected void queueChunkForPopulation(Chunk c) {
		if (populationQueueSet.add(c)) {
			populationQueue.add(c);
		}
	}

	public void addEntity(Entity e) {
		Controller controller = e.getController();
		if (controller instanceof BlockController) {
			Point pos = e.getPosition();
			Vector3 vpos = new Vector3(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
			Entity old = blockEntities.put(vpos, e);
			if (old != null) {
				old.kill();
			}
		}
		this.allocate((SpoutEntity) e);
	}

	public void removeEntity(Entity e) {
		Vector3 pos = e.getPosition().floor();
		Entity be = blockEntities.get(pos);
		if (be == e) {
			blockEntities.remove(pos);
		}
		this.deallocate((SpoutEntity)e);
	}

	public void startTickRun(int stage, long delta) throws InterruptedException {
		switch (stage) {
			case 0: {
				taskManager.heartbeat(delta);
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

				//for those chunks that had lighting updated - refresh
				synchronized (lightDirtyChunks) {
					if (!lightDirtyChunks.isEmpty()) {
						int key;
						int x, y, z;
						TIntIterator iter = lightDirtyChunks.iterator();
						while (iter.hasNext()) {
							key = iter.next();
							x = TByteTripleHashSet.key1(key);
							y = TByteTripleHashSet.key2(key);
							z = TByteTripleHashSet.key3(key);
							SpoutChunk chunk = this.getChunk(x, y, z);
							if (chunk == null || !chunk.isLoaded()) {
								iter.remove();
								continue;
							}
							if (chunk.lightingCounter.incrementAndGet() > LIGHT_SEND_TICK_DELAY) {
								chunk.lightingCounter.set(-1);
								if (SpoutConfiguration.LIVE_LIGHTING.getBoolean()) {
									chunk.setLightDirty(true);
								}
								iter.remove();
							}
						}
					}
				}
				
				World world = getWorld();
				int[] updates;
				Object[] sources;
				synchronized (queuedPhysicsUpdates) {
					updates = queuedPhysicsUpdates.keys();
					sources = queuedPhysicsUpdates.values();
					queuedPhysicsUpdates.clear();
				}
				for (int i = 0; i < updates.length; i++) {
					int key = updates[i];
					Source source = (Source) sources[i];
					int x = TByteTripleObjectHashMap.key1(key) & 0xFF;
					int y = TByteTripleObjectHashMap.key2(key) & 0xFF;
					int z = TByteTripleObjectHashMap.key3(key) & 0xFF;
					//switch region block coords (0-255) to a chunk index
					Chunk chunk = chunks[x >> Chunk.BLOCKS.BITS][y >> Chunk.BLOCKS.BITS][z >> Chunk.BLOCKS.BITS].get();
					if (chunk != null) {
						BlockMaterial material = chunk.getBlockMaterial(x, y, z);
						if (material.hasPhysics()) {
							//switch region block coords (0-255) to world block coords
							Block block = world.getBlock(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ(), source);
							material.onUpdate(block);
						}
					}
				}

				for (int i = 0; i < POPULATE_PER_TICK; i++) {
					Chunk toPopulate = populationQueue.poll();
					if (toPopulate == null) {
						break;
					}
					populationQueueSet.remove(toPopulate);
					if (toPopulate.isLoaded()) {
						toPopulate.populate();
					} else {
						i--;
					}
				}

				Chunk toUnload = unloadQueue.poll();
				if (toUnload != null) {
					boolean do_unload = true;
					if (ChunkUnloadEvent.getHandlerList().getRegisteredListeners().length > 0) {
						ChunkUnloadEvent event = Spout.getEngine().getEventManager().callEvent(new ChunkUnloadEvent(toUnload));
						if (event.isCancelled()) {
							do_unload = false;
						}
					}
					if (do_unload) {
						toUnload.unload(true);
					}
				}

				break;
			}
			case 1: {
				//Resolve collisions and prepare for a snapshot.
				Set<SpoutEntity> resolvers = new HashSet<SpoutEntity>();
				boolean shouldResolve;
				for (SpoutEntity ent : entityManager) {
					shouldResolve = ent.preResolve();
					if (shouldResolve) {
						resolvers.add(ent);
					}
				}

				for (SpoutEntity ent : resolvers) {
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
	
	private int compressDx = 0;
	private int compressDy = 0;

	public void finalizeRun() throws InterruptedException {
		
		long worldAge = getWorld().getAge();

		dynamicBlockTree.commitPending(worldAge);
		multiRegionUpdates = dynamicBlockTree.updateDynamicBlocks(worldAge);
		
		// Compress at most 1 chunk per tick per region
		boolean chunkCompressed = false;

		int reaped = 0;

		
		compressDy++;
		if (compressDy >= CHUNKS.SIZE) {
			compressDy = 0;
			compressDx++;
			if (compressDx >= CHUNKS.SIZE) {
				compressDx = 0;
			}
		}

		for (int dz = 0; dz < CHUNKS.SIZE && !chunkCompressed; dz++) {
			Chunk chunk = chunks[compressDx][compressDy][dz].get();
			if (chunk != null) {
				chunkCompressed |= ((SpoutChunk) chunk).compressIfRequired();

				if (reaped < REAP_PER_TICK && ((SpoutChunk) chunk).isReapable(worldAge)) {
					boolean do_unload = true;
					if (ChunkUnloadEvent.getHandlerList().getRegisteredListeners().length > 0) {
						ChunkUnloadEvent event = Spout.getEngine().getEventManager().callEvent(new ChunkUnloadEvent(chunk));
						if (event.isCancelled()) {
							do_unload = false;
						}
					}
					if (do_unload) {
						((SpoutChunk) chunk).unload(true);
						reaped++;
					}
				} else {
					if (!chunk.isPopulated()) {
						queueChunkForPopulation(chunk);
					}
				}
			}
		}
		
		//Note: This must occur after any chunks are reaped, because reaping chunks may kill entities, which need to be finalized
		entityManager.finalizeRun();
	}

	private void syncChunkToPlayers(SpoutChunk chunk, Entity entity) {
		SpoutPlayer player = (SpoutPlayer) ((PlayerController) entity.getController()).getPlayer();
		NetworkSynchronizer synchronizer = player.getNetworkSynchronizer();
		if (synchronizer != null) {
			if (!chunk.isDirtyOverflow() && !chunk.isLightDirty()) {
				for (int i = 0; true; i++) {
					Vector3 block = chunk.getDirtyBlock(i);
					if (block == null) {
						break;
					} else {
						try {
							synchronizer.updateBlock(chunk, (int) block.getX(), (int) block.getY(), (int) block.getZ());
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

	private void processChunkUpdatedEvent(SpoutChunk chunk) {
		/* If no listeners, quit */
		if (ChunkUpdatedEvent.getHandlerList().getRegisteredListeners().length == 0) {
			return;
		}
		ChunkUpdatedEvent evt;
		if (chunk.isDirtyOverflow()) {	/* If overflow, notify for whole chunk */
			evt = new ChunkUpdatedEvent(chunk, null);
		}
		else {
			ArrayList<Vector3> lst = new ArrayList<Vector3>();
			boolean done = false;
			for (int i = 0; !done; i++) {
				Vector3 v = chunk.getDirtyBlock(i);
				if (v != null) {
					lst.add(v);
				}
				else {
					done = true;
				}
			}
			evt = new ChunkUpdatedEvent(chunk, lst);
		}
		Spout.getEventManager().callDelayedEvent(evt);
	}

	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshotRun();

		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk == null) {
						continue;
					}
					SpoutChunk spoutChunk = (SpoutChunk) chunk;

					if (spoutChunk.isPopulated() && spoutChunk.isDirty()) {
						for (Entity entity : spoutChunk.getObserversLive()) {
							//chunk.refreshObserver(entity);
							if (!(entity.getController() instanceof PlayerController)) {
								continue;
							}
							syncChunkToPlayers(spoutChunk, entity);
						}
						processChunkUpdatedEvent(spoutChunk);
						
						spoutChunk.resetDirtyArrays();
						spoutChunk.setLightDirty(false);
					}
				}
			}
		}
		Iterator<SpoutChunk> itr = occupiedChunks.iterator();
		int cx, cy, cz;
		while (itr.hasNext()) {
			SpoutChunk c = itr.next();

			cx = c.getX() & CHUNKS.MASK;
			cy = c.getY() & CHUNKS.MASK;
			cz = c.getZ() & CHUNKS.MASK;

			if (c == getChunk(cx, cy, cz, LoadOption.NO_LOAD)) {
				c.syncEntities();
			} else {
				itr.remove();
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
	 * @param entity The entity.
	 * @return The id.
	 */
	public int allocate(SpoutEntity entity) {
		return entityManager.allocate(entity, this);
	}

	/**
	 * Deallocates the id for an entity.
	 * @param entity The entity.
	 */
	public void deallocate(SpoutEntity entity) {
		entityManager.deallocate(entity);
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void onChunkPopulated(SpoutChunk chunk) {
		Spout.getEventManager().callDelayedEvent(new ChunkPopulateEvent(chunk));
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
		return executionThread;
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
	 * @param c the chunk
	 * @return the DataOutputStream
	 */
	public OutputStream getChunkOutputStream(Chunk c) {
		int key = getChunkKey(c.getX(), c.getY(), c.getZ());
		return chunkStore.getBlockOutputStream(key);
	}

	/**
	 * Gets the DataInputStream corresponding to a given Chunk.<br>
	 * <br>
	 * The stream is based on a snapshot of the array.
	 * @param x the chunk
	 * @return the DataInputStream
	 */
	public InputStream getChunkInputStream(int x, int y, int z) {
		int key = getChunkKey(x, y, z);
		return chunkStore.getBlockInputStream(key);
	}

	private int getChunkKey(int chunkX, int chunkY, int chunkZ) {
		chunkX &= CHUNKS.MASK;
		chunkY &= CHUNKS.MASK;
		chunkZ &= CHUNKS.MASK;

		int key = 0;
		key |= chunkX;
		key |= chunkY << CHUNKS.BITS;
		key |= chunkZ << (CHUNKS.BITS << 1);

		return key;
	}


	@Override
	public boolean hasChunkAtBlock(int x, int y, int z) {
		return this.getWorld().hasChunkAtBlock(x, y, z);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, source);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, data, source);
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockLight(x, y, z, light, source);
	}

	@Override
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockSkyLight(x, y, z, light, source);
	}
	
	@Override
	public short setBlockDataBits(int x, int y, int z, short bits) {
		return this.getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits);
	}

	@Override
	public short clearBlockDataBits(int x, int y, int z, short bits) {
		return this.getChunkFromBlock(x, y, z).clearBlockDataBits(x, y, z, bits);
	}

	@Override
	public int getBlockDataField(int x, int y, int z, int bits) {
		return this.getChunkFromBlock(x, y, z).getBlockDataField(x, y, z, bits);
	}

	@Override
	public int setBlockDataField(int x, int y, int z, int bits, int value) {
		return this.getChunkFromBlock(x, y, z).setBlockDataField(x, y, z, bits, value);
	}

	@Override
	public void setBlockController(int x, int y, int z, BlockController controller) {
		Vector3 pos = new Vector3(x, y, z);
		Entity entity = blockEntities.get(pos);
		if (entity != null) {
			if (controller != null) {
				//hotswap
				entity.setController(controller);
			} else {
				//remove old
				entity.kill();
			}
		} else if (controller != null) {
			//spawn new entity with controller
			this.getWorld().createAndSpawnEntity(new Point(pos, getWorld()), controller);
		}
	}

	@Override
	public BlockController getBlockController(int x, int y, int z) {
		Entity entity = blockEntities.get(new Vector3(x, y, z));
		return entity == null ? null : (BlockController) entity.getController();
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z, Source source) {
		synchronized (queuedPhysicsUpdates) {
			queuedPhysicsUpdates.put((byte) (x & BLOCKS.MASK), (byte) (y & BLOCKS.MASK), (byte) (z & BLOCKS.MASK), source);
		}
	}

	protected void reportChunkLightDirty(int x, int y, int z) {
		synchronized (lightDirtyChunks) {
			lightDirtyChunks.add(x & CHUNKS.MASK, y & CHUNKS.MASK, z & CHUNKS.MASK);
		}
	}

	@Override
	public Biome getBiomeType(int x, int y, int z) {
		return this.getWorld().getBiomeType(x, y, z);
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return this.getWorld().getBlock(x, y, z);
	}

	@Override
	public Block getBlock(int x, int y, int z, Source source) {
		return this.getWorld().getBlock(x, y, z, source);
	}

	@Override
	public Block getBlock(float x, float y, float z) {
		return this.getWorld().getBlock(x, y, z);
	}

	@Override
	public Block getBlock(float x, float y, float z, Source source) {
		return this.getWorld().getBlock(x, y, z, source);
	}

	@Override
	public Block getBlock(Vector3 position) {
		return this.getWorld().getBlock(position);
	}

	@Override
	public Block getBlock(Vector3 position, Source source) {
		return this.getWorld().getBlock(position, source);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z).getBlockMaterial(x, y, z);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z).getBlockData(x, y, z);
	}

	@Override
	public byte getBlockLight(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z).getBlockLight(x, y, z);
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z).getBlockSkyLight(x, y, z);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, BlockFullState expect, short data) {
		return this.getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data);
	}

	@Override
	public Set<Player> getPlayers() {
		HashSet<Player> players = new HashSet<Player>();
		for (PlayerController player : this.entityManager.getPlayers()) {
			if (player.getPlayer() != null) {
				players.add(player.getPlayer());
			}
		}
		return players;
	}
	
	/**
	 * Test if region file exists
	 * 
	 * @param world world
	 * @param x region x coordinate
	 * @param y region y coordinate
	 * @param z region z coordinate
	 * 
	 * @return true if exists, false if doesn't exist
	 */
	
	public static boolean regionFileExists(SpoutWorld world, int x, int y, int z) {
		File worldDirectory = world.getDirectory();
		File regionDirectory = new File(worldDirectory, "region");
		File regionFile = new File(regionDirectory, "reg" + x + "_" + y + "_" + z + ".spr");
		return regionFile.exists();
	}

	@Override
	public TaskManager getTaskManager() {
		return taskManager;
	}
	
	public void skipChunk(SpoutChunk chunk) {
		occupiedChunks.remove(chunk);
	}
	
	public void unSkipChunk(SpoutChunk chunk) {
		occupiedChunks.add(chunk);
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		dynamicBlockTree.resetBlockUpdates(x, y, z);
	}
	
	@Override
	public void queueDynamicUpdate(int x, int y, int z, long nextUpdate, Object hint) {
		dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate, hint);
	}

	@Override
	public void queueDynamicUpdate(int x, int y, int z, long nextUpdate) {
		dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate);
	}

	@Override
	public void queueDynamicUpdate(int x, int y, int z) {
		dynamicBlockTree.queueBlockUpdates(x, y, z);
	}

	
	// TODO - save needs to call this method
	public List<DynamicBlockUpdate> getDynamicBlockUpdates(Chunk c) {
		Set<DynamicBlockUpdate> updates = dynamicBlockTree.getDynamicBlockUpdates(c);
		int size = updates == null ? 0 : updates.size();
		if (multiRegionUpdates != null) {
			size += multiRegionUpdates.size();
		}
		List<DynamicBlockUpdate> list = new ArrayList<DynamicBlockUpdate>(size);
		if (updates != null) {
			list.addAll(updates);
		}
		if (multiRegionUpdates != null) {
			for (DynamicBlockUpdate dm : multiRegionUpdates) {
				if (dm.isInChunk(c)) {
					list.add(dm);
				}
			}
		}
		return list;
	}
	
	public boolean removeDynamicBlockUpdates(Chunk c) {
		boolean removed = dynamicBlockTree.removeDynamicBlockUpdates(c);
		if (multiRegionUpdates != null) {
			Iterator<DynamicBlockUpdate> itr = multiRegionUpdates.iterator();
			while (itr.hasNext()) {
				DynamicBlockUpdate dm = itr.next();
				if (dm.isInChunk(c)) {
					removed = true;
					itr.remove();
				}
			}
		}
		return removed;
	}
	
}
