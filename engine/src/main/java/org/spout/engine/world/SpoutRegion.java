/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.world;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.component.entity.PlayerNetworkComponent;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.Cause;
import org.spout.api.event.chunk.ChunkLoadEvent;
import org.spout.api.event.chunk.ChunkPopulateEvent;
import org.spout.api.event.chunk.ChunkUnloadEvent;
import org.spout.api.event.chunk.ChunkUpdatedEvent;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Cube;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.lighting.LightingManager;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.GenericMath;
import org.spout.api.math.IntVector3;
import org.spout.api.math.ReactConverter;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.event.BlockUpdateEvent;
import org.spout.api.protocol.event.ChunkDatatableSendEvent;
import org.spout.api.protocol.event.ChunkSendEvent;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.cuboid.ChunkCuboidLightBufferWrapper;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.cuboid.ImmutableHeightMapBuffer;
import org.spout.api.util.cuboid.LocalRegionChunkCuboidBlockMaterialBufferWrapper;
import org.spout.api.util.cuboid.LocalRegionChunkCuboidLightBufferWrapper;
import org.spout.api.util.cuboid.LocalRegionChunkHeightMapBufferWrapper;
import org.spout.api.util.list.concurrent.setqueue.SetQueue;
import org.spout.api.util.list.concurrent.setqueue.SetQueueElement;
import org.spout.api.util.set.TByteTripleHashSet;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.component.entity.SpoutPhysicsComponent;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutEntitySnapshot;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.filesystem.ChunkDataForRegion;
import org.spout.engine.filesystem.versioned.ChunkFiles;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.world.collision.SpoutCollisionListener;
import org.spout.engine.world.collision.SpoutLinkedWorldInfo;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;
import org.spout.engine.world.dynamic.DynamicBlockUpdateTree;
import org.spout.physics.body.RigidBody;
import org.spout.physics.collision.shape.CollisionShape;
import org.spout.physics.engine.linked.LinkedDynamicsWorld;
import org.spout.physics.math.Quaternion;

public class SpoutRegion extends Region implements AsyncManager {
	/**
	 * The maximum number of chunks that will be processed for population each tick
	 */
	private static final int POPULATE_PER_TICK = 20;
	/**
	 * An AtomicReference array of SpoutChunks
	 */
	private final AtomicReference<SpoutChunk>[][][] chunks;
	/**
	 * An AtomicReference array of the neighbour regions
	 */
	private final AtomicReference<SpoutRegion>[][][] neighbours;
	/**
	 * Ths number of active chunks in this region
	 */
	private final AtomicInteger numberLoadedChunks;
	/**
	 * Queue of cubes which have been marked for saving
	 */
	private final SetQueueElement<Cube> saveMarkedElement;
	/**
	 * The source of this region
	 */
	private final RegionSource source;
	/**
	 * Snapshot manager for this region
	 */
	private final SnapshotManager snapshotManager;
	/**
	 * Holds all of the entities to be simulated
	 */
	private final EntityManager entityManager;
	/**
	 * Reference to the persistent ByteArrayArray that stores chunk data
	 */
	private final BAAWrapper chunkStore;
	/**
	 * Concurrent queue of future chunk snapshots
	 */
	private final Queue<SpoutChunkSnapshotFuture> snapshotQueue;
	/**
	 * The sequence number for executing inter-region physics and dynamic updates
	 */
	private final int updateSequence;
	/**
	 * The generator for this region
	 */
	private final RegionGenerator generator;
	/**
	 * The scheduler for this region
	 */
	private final SpoutScheduler scheduler;
	/**
	 * The region's physics simulator
	 */
	private final LinkedDynamicsWorld simulation;
	/**
	 * This region's task manager
	 */
	private final SpoutTaskManager taskManager;
	/**
	 * A queue of dirty Spout columns
	 */
	private final SetQueue<SpoutColumn> dirtyColumnQueue;
	/**
	 * This region's physics block update tree
	 */
	private final DynamicBlockUpdateTree dynamicBlockTree;
	/**
	 * A linked hash map of the observers in this region
	 */
	private final LinkedHashMap<SpoutPlayer, TByteTripleHashSet> observers;
	/**
	 * A queue of cubes which have been marked for saving
	 */
	protected final SetQueue<Cube> markedSaveQueue;
	/**
	 * Queue of chunks which are to be unloaded
	 */
	protected final SetQueue<SpoutChunk> unloadQueue;
	/**
	 * A queue of chunks that need to be populated
	 */
	protected final SetQueue<SpoutChunk> populationQueue = new SetQueue<>(CHUNKS.VOLUME);
	/**
	 * A queue of chunks which which have priority of being populated
	 */
	protected final SetQueue<SpoutChunk> populationPriorityQueue = new SetQueue<>(CHUNKS.VOLUME);
	/**
	 * A queue of chunks which need their local physics updated
	 */
	protected final SetQueue<SpoutChunk> localPhysicsChunkQueue = new SetQueue<>(CHUNKS.VOLUME);
	/**
	 * A queue of chunks which need their global physics updated
	 */
	protected final SetQueue<SpoutChunk> globalPhysicsChunkQueue = new SetQueue<>(CHUNKS.VOLUME);
	/**
	 * A queue of dirty chunks
	 */
	protected final SetQueue<SpoutChunk> dirtyChunkQueue = new SetQueue<>(CHUNKS.VOLUME);
	/**
	 * A queue of new chunks
	 */
	protected final SetQueue<SpoutChunk> newChunkQueue = new SetQueue<>(CHUNKS.VOLUME);
	/**
	 * This region's block material buffer
	 */
	private LocalRegionChunkCuboidBlockMaterialBufferWrapper blockMaterialBuffer = null;
	/**
	 * This region's chunk light buffers
	 */
	private ChunkCuboidLightBufferWrapper<?>[] lightBuffers = null;
	/**
	 * A list of this regions dynamic block updates
	 */
	private List<DynamicBlockUpdate> multiRegionUpdates = null;
	/**
	 * This region's heightmap buffer
	 */
	private LocalRegionChunkHeightMapBufferWrapper heightMapBuffer = null;
	/**
	 * This region's execution thread
	 */
	private Thread executionThread;
	/**
	 * The number of lighting updates which have occured in this region
	 */
	private int lightingUpdates = 0;
	//Finalize run variables
	private int reapX = 0, reapY = 0, reapZ = 0;

	/**
	 * {@link SpoutRegion} is the implementation of {@link Region} in Spout.
	 * SpoutRegion holds 16x16x16 (4096) chunks in its buffers.
	 *
	 * @param world The world which this region is part of
	 * @param x The region's x-coordinate
	 * @param y The region's y-coordinate
	 * @param z The region's z-coordinate
	 * @param source This region's source
	 */
	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source) {
		super(world, x * Region.BLOCKS.SIZE, y * Region.BLOCKS.SIZE, z * Region.BLOCKS.SIZE);

		this.chunks = new AtomicReference[CHUNKS.SIZE][CHUNKS.SIZE][CHUNKS.SIZE];
		for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
			for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
				for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
					chunks[dx][dy][dz] = new AtomicReference<>(null);
				}
			}
		}

		this.source = source;

		this.entityManager = new EntityManager(this);
		this.snapshotManager = new SnapshotManager();

		this.markedSaveQueue = new SetQueue<>(CHUNKS.VOLUME + 1);
		this.saveMarkedElement = new SetQueueElement<>(markedSaveQueue, this);

		this.snapshotQueue = new ConcurrentLinkedQueue<>();
		this.unloadQueue = new SetQueue<>(CHUNKS.VOLUME);

		this.numberLoadedChunks = new AtomicInteger(0);

		this.dynamicBlockTree = new DynamicBlockUpdateTree(this);

		this.dirtyColumnQueue = world.getColumnDirtyQueue(getX(), getZ());

		this.observers = new LinkedHashMap<>();

		int xx = GenericMath.mod(getX(), 3);
		int yy = GenericMath.mod(getY(), 3);
		int zz = GenericMath.mod(getZ(), 3);
		this.updateSequence = (xx * 9) + (yy * 3) + zz;

		this.neighbours = new AtomicReference[3][3][3];
		final int rx = getX();
		final int ry = getY();
		final int rz = getZ();
		for (int dx = 0 ; dx < 3 ; dx++) {
			for (int dy = 0 ; dy < 3 ; dy++) {
				for (int dz = 0 ; dz < 3 ; dz++) {
					neighbours[dx][dy][dz] = new AtomicReference<>(world.getRegion(rx + dx - 1, ry + dy - 1, rz + dz - 1, LoadOption.NO_LOAD));
				}
			}
		}

		if (Spout.getPlatform() == Platform.CLIENT) {
			this.generator = null;
			this.chunkStore = null;
		} else {
			this.generator = new RegionGenerator(this, 4);
			this.chunkStore = ((SpoutServerWorld) world).getRegionFile(getX(), getY(), getZ());
		}

		this.taskManager = new SpoutTaskManager(world.getEngine().getScheduler(), null, this, world.getAge());

		this.scheduler = (SpoutScheduler) (Spout.getEngine().getScheduler());

		this.simulation = new LinkedDynamicsWorld(ReactConverter.toReactVector3(0f, -9.81f, -0f), new SpoutLinkedWorldInfo(this));
		this.simulation.addListener(new SpoutCollisionListener());
		this.simulation.start();
	}

	/**
	 * Gets the {@link EntityManager} for this region.
	 *
	 * @return This region's entity manager
	 */
	public final EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Gets the {@link LinkedDynamicsWorld} for this region.
	 *
	 * @return The region's simulation
	 */
	public final LinkedDynamicsWorld getSimulation() {
		return simulation;
	}

	/**
	 * Gets the {@link TaskManager} for this region.
	 *
	 * @return
	 */
	@Override
	public final SpoutTaskManager getTaskManager() {
		return taskManager;
	}

	/**
	 * Gets the {@link RegionGenerator} for this region.
	 *
	 * @return This region's generator
	 */
	protected final RegionGenerator getRegionGenerator() {
		return generator;
	}

	/**
	 * Gets the queue of cubes which have been marked for saving.
	 *
	 * @return Queue of cubes to save
	 */
	protected final SetQueue<Cube> getMarkedSaveQueue() {
		return markedSaveQueue;
	}

	/**
	 * Gets the queue of chunks which are to be unloaded.
	 *
	 * @return Queue of chunks to be unloaded
	 */
	protected final SetQueue<SpoutChunk> getUnloadQueue() {
		return unloadQueue;
	}

	/**
	 * Checks if this region has no chunks which are loaded
	 *
	 * @return True if region is empty
	 */
	public boolean isEmpty() {
		TickStage.checkStage(TickStage.SNAPSHOT);
		for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
			for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
				for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
					if (chunks[dx][dy][dz].get() != null) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Marks this region for save and unloading.
	 */
	public void markForSaveUnload() {
		saveMarkedElement.add();
	}

	/**
	 * Removes a chunk from the region and indicates if the region is empty.
	 *
	 * @param chunk The chunk to remove
	 * @return True if the region is now empty
	 */
	public boolean removeChunk(Chunk chunk) {
		TickStage.checkStage(TickStage.SNAPSHOT);
		if (chunk.getRegion() != this) {
			return false;
		}

		final AtomicReference<SpoutChunk> current = chunks[chunk.getX() & CHUNKS.MASK][chunk.getY() & CHUNKS.MASK][chunk.getZ() & CHUNKS.MASK];
		final SpoutChunk currentChunk = current.get();
		if (currentChunk != chunk) {
			return false;
		}

		if (current.compareAndSet(currentChunk, null)) {
			final int num = numberLoadedChunks.decrementAndGet();
			for (Entity e : currentChunk.getLiveEntities()) {
				e.remove();
			}
			currentChunk.setUnloaded();
			removeDynamicBlockUpdates(currentChunk);
			if (num == 0) {
				return true;
			} else {
				if (num < 0) {
					throw new IllegalStateException("Region has less than 0 active chunks");
				}
			}
		}
		return false;
	}

	/**
	 * Processes the provided chunk for saving and unloading.
	 *
	 * @param c The {@link SpoutChunk} to be processed
	 * @return True if the chunk was already unloaded
	 */
	public boolean processChunkSaveUnload(SpoutChunk c) {
		boolean empty = false;
		if (c != null) {
			SpoutChunk.SaveState oldState = c.getAndResetSaveState();
			if (oldState.isSave()) {
				c.syncSave();
			}
			if (oldState.isUnload() && !c.isObserved()) {
				if (removeChunk(c)) {
					empty = true;
				}
			}
		}
		return empty;
	}

	/**
	 * Runs the local physics in this region.
	 */
	private void runLocalPhysics() {
		boolean updated = true;

		while (updated) {
			updated = false;
			SpoutChunk c;
			while ((c = this.localPhysicsChunkQueue.poll()) != null) {
				updated |= c.runLocalPhysics();
			}
		}
	}

	/**
	 * Runs the global physics in this region.
	 */
	private void runGlobalPhysics() {
		SpoutChunk c;
		while ((c = this.globalPhysicsChunkQueue.poll()) != null) {
			c.runGlobalPhysics();
		}
	}

	/**
	 * Runs the local dynamic updates in this region.
	 *
	 * @param time TODO
	 */
	private void runLocalDynamicUpdates(long time) {
		long currentTime = getWorld().getAge();
		if (time > currentTime) {
			time = currentTime;
		}
		this.dynamicBlockTree.commitAsyncPending(currentTime);
		this.multiRegionUpdates = dynamicBlockTree.updateDynamicBlocks(currentTime, time);
	}

	/**
	 * Runs the global dynamic updates in this region.
	 */
	private void runGlobalDynamicUpdates() {
		long currentTime = getWorld().getAge();
		if (multiRegionUpdates != null) {
			boolean updated = false;
			for (DynamicBlockUpdate update : multiRegionUpdates) {
				updated |= dynamicBlockTree.updateDynamicBlock(currentTime, update, true).isUpdated();
			}
			if (updated) {
				this.scheduler.addUpdates(1);
			}
		}
	}

	/**
	 * Checks if the chunk store stream exists.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param y The y-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 * @return True if the stream exists
	 */
	public boolean inputStreamExists(int x, int y, int z) {
		if (chunkStore == null) {
			throw new IllegalStateException("Client does not have chunk store");
		}
		return chunkStore.inputStreamExists(getChunkKey(x, y, z));
	}

	/**
	 * Attempts to close the chunk store.
	 *
	 * @return
	 */
	public boolean attemptClose() {
		if (chunkStore == null) {
			return true;
		}
		return chunkStore.attemptClose();
	}

	/**
	 * Gets the DataInputStream corresponding to a given Chunk
	 * <br> </br>
	 * The stream is based on a snapshot of the array.
	 *
	 * @param x The chunk
	 * @return The {@link DataInputStream}
	 */
	public InputStream getChunkInputStream(int x, int y, int z) {
		if (chunkStore == null) {
			throw new IllegalStateException("Client does not have chunk store");
		}
		return chunkStore.getBlockInputStream(getChunkKey(x, y, z));
	}

	/**
	 * Attempts to queue the block at the given coordinates in the physics queue,
	 * provided that the block isn't null.
	 *
	 * @param x The x-coordinate of the block
	 * @param y The y-coordinate of the block
	 * @param z The z-coordinate of the block
	 * @param range TODO
	 * @param oldMaterial TODO
	 */
	public void queueBlockPhysics(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial) {
		SpoutChunk c = getChunkFromBlock(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			c.queueBlockPhysics(x, y, z, range, oldMaterial);
		}
	}

	/**
	 * Attempts to update the physics of the block at the provided coordinates.
	 *
	 * @param x The x-coordinate of the block
	 * @param y The y-coordinate of the block
	 * @param z The z-coordinate of the block
	 * @param oldMaterial TODO
	 */
	public void updateBlockPhysics(int x, int y, int z, BlockMaterial oldMaterial) {
		SpoutChunk c = getChunkFromBlock(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			c.updateBlockPhysics(x, y, z, oldMaterial);
		}
	}

	/**
	 * Calls the {@link ChunkPopulateEvent} for the provided chunk.
	 *
	 * @param chunk Chunk to be used in the event
	 */
	public void onChunkPopulated(SpoutChunk chunk) {
		Spout.getEventManager().callDelayedEvent(new ChunkPopulateEvent(chunk));
	}

	/**
	 * Set the chunk at the provided coordinates as modified.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param y The y-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 */
	public void setChunkModified(int x, int y, int z) {
		SpoutChunk c = getChunk(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			c.setModified();
		}
	}

	/**
	 * Gets a {@link List} of all the dynamic block updates in the provided chunk.
	 *
	 * @param c Chunk to get updates from
	 * @return List of the provided chunk's dynamic updates
	 */
	public List<DynamicBlockUpdate> getDynamicBlockUpdates(Chunk c) {
		Set<DynamicBlockUpdate> updates = dynamicBlockTree.getDynamicBlockUpdates(c);
		int size = updates == null ? 0 : updates.size();
		List<DynamicBlockUpdate> list = new ArrayList<>(size);
		if (updates != null) {
			list.addAll(updates);
		}
		return list;
	}

	/**
	 * Remove the dynamic updates in the chunk provided.
	 *
	 * @param c Chunk to remove dynamic updates from
	 * @return TODO
	 */
	public boolean removeDynamicBlockUpdates(Chunk c) {
		return dynamicBlockTree.removeDynamicBlockUpdates(c);
	}

	/**
	 * Add the provided {@link SpoutChunkSnapshotFuture} to the snapshot queue in this region.
	 *
	 * @param future The snapshot to add
	 */
	public void addSnapshotFuture(SpoutChunkSnapshotFuture future) {
		snapshotQueue.add(future);
	}

	/**
	 * Unlinks this region from all its neighbours.
	 * <br></br>
	 * This should only be called when at {@code  TickStage.SNAPSHOT}.
	 */
	public void unlinkNeighbours() {
		TickStage.checkStage(TickStage.SNAPSHOT);
		final int rx = getX();
		final int ry = getY();
		final int rz = getZ();
		final SpoutWorld world = getWorld();
		for (int dx = 0 ; dx < 3 ; dx++) {
			for (int dy = 0 ; dy < 3 ; dy++) {
				for (int dz = 0 ; dz < 3 ; dz++) {
					SpoutRegion region = world.getRegion(rx + dx - 1, ry + dy - 1, rz + dz - 1, LoadOption.NO_LOAD);
					if (region != null) {
						for (int dxx = 0 ; dxx < 3 ; dxx++) {
							for (int dyy = 0 ; dyy < 3 ; dyy++) {
								for (int dzz = 0 ; dzz < 3 ; dzz++) {
									neighbours[dxx][dyy][dzz].compareAndSet(this, null);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Adds a new chunk using the coordinates, block id's and block data provided.
	 * <br></br>
	 * If there was previously a chunk at the location, it will be unloaded and not saved.
	 *
	 * @param chunkX The x-coordinate of the chunk
	 * @param chunkY The y-coordinate of the chunk
	 * @param chunkZ The z-coordinate of the chunk
	 * @param blockIds The block id's for the new chunk
	 * @param blockData The block data for the new chunk
	 * @return The new chunk created
	 */
	public SpoutChunk addChunk(int chunkX, int chunkY, int chunkZ, short[] blockIds, short[] blockData) {
		final int regionChunkX = chunkX & CHUNKS.MASK;
		final int regionChunkY = chunkY & CHUNKS.MASK;
		final int regionChunkZ = chunkZ & CHUNKS.MASK;
		final AtomicReference<SpoutChunk> chunkReference = chunks[regionChunkX][regionChunkY][regionChunkZ];
		SpoutChunk chunk = chunkReference.get();
		if (chunk != null) {
			chunk.unload(false);
			// TODO is this right?
			chunkReference.set(null);
		}
		chunk = new SpoutChunk(getWorld(), this, chunkX, chunkY, chunkZ, SpoutChunk.PopulationState.POPULATED, blockIds, blockData, new ManagedHashMap(), true);
		setChunk(chunk, regionChunkX, regionChunkY, regionChunkZ, null, false);
		checkChunkLoaded(chunk, LoadOption.LOAD_GEN);
		return chunk;
	}

	/**
	 * Attempts to remove the chunk at the provided coordinates.
	 *
	 * @param chunkX The x-coordinate of the chunk
	 * @param chunkY The y-coordinate of the chunk
	 * @param chunkZ The z-coordinate of the chunk
	 */
	public void removeChunk(int chunkX, int chunkY, int chunkZ) {
		final int regionChunkX = chunkX & CHUNKS.MASK;
		final int regionChunkY = chunkY & CHUNKS.MASK;
		final int regionChunkZ = chunkZ & CHUNKS.MASK;
		final SpoutChunk chunk = chunks[regionChunkX][regionChunkY][regionChunkZ].get();
		if (chunk != null) {
			chunk.unload(false);
			// TODO is this right?
			SpoutScheduler.addToQueue(new SpoutChunkSnapshotModel(chunk.getWorld(), chunkX, chunkY, chunkZ, true, System.currentTimeMillis()));
			chunks[regionChunkX][regionChunkY][regionChunkZ].set(null);
		}
	}

	/**
	 * Adds a physics body to this region using the data provided.
	 *
	 * @param transform The body's transform
	 * @param mass The mass of the body
	 * @param shape The collision shape of the body
	 * @param isGhost If the body is a ghost
	 * @param isMobile If the body is mobile
	 * @return The new body created
	 */
	public RigidBody addBody(final Transform transform, final float mass, final CollisionShape shape, final boolean isGhost, final boolean isMobile) {
		if (isMobile) {
			if (isGhost) {
				return simulation.createGhostMobileRigidBody(new org.spout.physics.math.Transform(ReactConverter.toReactVector3(transform.getPosition()), new Quaternion(0, 0, 0, 1)), mass, shape);
			}
			return simulation.createMobileRigidBody(new org.spout.physics.math.Transform(ReactConverter.toReactVector3(transform.getPosition()), new Quaternion(0, 0, 0, 1)), mass, shape);
		} else {
			if (isGhost) {
				return simulation.createGhostImmobileRigidBody(new org.spout.physics.math.Transform(ReactConverter.toReactVector3(transform.getPosition()), new Quaternion(0, 0, 0, 1)), mass, shape);
			}
			return simulation.createImmobileRigidBody(new org.spout.physics.math.Transform(ReactConverter.toReactVector3(transform.getPosition()), new Quaternion(0, 0, 0, 1)), mass, shape);
		}
	}

	/**
	 * Destroys the physics body provided from this region.
	 *
	 * @param body The body to be destroyed
	 */
	public void destroyBody(final RigidBody body) {
		simulation.destroyRigidBody(body);
	}

	/**
	 * Returns the world which this region is part of.
	 *
	 * @return The region's world
	 */
	@Override
	public SpoutWorld getWorld() {
		return (SpoutWorld) super.getWorld();
	}

	/**
	 * Attempts to save all the chunks in this region.
	 */
	@Override
	@DelayedWrite
	public void save() {
		for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
			for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
				for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
					final SpoutChunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunk.saveNoMark();
					}
				}
			}
		}
		markForSaveUnload();
	}

	/**
	 * Attempts to unload all the chunks in this region.
	 *
	 * @param save True to also save the chunks
	 */
	@Override
	public void unload(boolean save) {
		for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
			for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
				for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
					SpoutChunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunk.unloadNoMark(save);
					}
				}
			}
		}
		markForSaveUnload();
	}

	/**
	 * Get all the entities which are present in this region.
	 *
	 * @return All this region's entities
	 */
	@Override
	public List<Entity> getAll() {
		return new ArrayList<Entity>(Collections.unmodifiableCollection(getEntityManager().getAll()));
	}

	/**
	 * Get a specific entity using its id from this region.
	 *
	 * @param id The entity's id
	 * @return The entity, null if not found
	 */
	@Override
	public Entity getEntity(int id) {
		return getEntityManager().getEntity(id);
	}

	/**
	 * Gets all the players which are in this region.
	 *
	 * @return A list of all the players in this region
	 */
	@Override
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(getEntityManager().getPlayers());
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z) {
		return getChunk(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z, LoadOption loadopt) {
		switch (loadopt) {
			case LOAD_ONLY:
				TickStage.checkStage(~TickStage.SNAPSHOT);
				break;
			case LOAD_GEN:
				TickStage.checkStage(~(TickStage.SNAPSHOT | TickStage.PRESNAPSHOT | TickStage.LIGHTING));
				break;
			default:
				break;
		}
		x &= CHUNKS.MASK;
		y &= CHUNKS.MASK;
		z &= CHUNKS.MASK;

		final SpoutChunk chunk = chunks[x][y][z].get();
		if (chunk != null) {
			checkChunkLoaded(chunk, loadopt);
			return chunk;
		}
		if (Spout.getPlatform() == Platform.CLIENT) {
			if (loadopt.loadIfNeeded() || loadopt.generateIfNeeded()) {
				final short[] blocks = new short[CHUNKS.VOLUME];
				Arrays.fill(blocks, BlockMaterial.UNGENERATED.getId());
				final SpoutChunk newChunk = new SpoutChunk(getWorld(), this, getChunkX() + x, getChunkY() + y, getChunkZ() + z, SpoutChunk.PopulationState.UNTOUCHED, blocks, null, null, true);
				chunks[x][y][z].set(newChunk);
				return newChunk;
			}
			throw new IllegalStateException("The client should never attempt to load or generate chunks!");
		}

		boolean fileExists = inputStreamExists(x, y, z);
		ChunkDataForRegion regionData = null;
		SpoutChunk newChunk = null;

		if (loadopt.loadIfNeeded() && fileExists) {
			regionData = new ChunkDataForRegion();
			newChunk = ChunkFiles.loadChunk(this, x, y, z, getChunkInputStream(x, y, z), regionData);
			if (newChunk == null) {
				Spout.getLogger().severe("Unable to load chunk at location: " + (getChunkX() + x) + ", " + (getChunkY() + y) + ", " + (getChunkZ() + z) + " in region " + this + ", regenerating chunks");
				fileExists = false;
			}
		}

		if (loadopt.generateIfNeeded() && !fileExists && newChunk == null) {
			getRegionGenerator().generateColumn(x, z);
			final SpoutChunk generatedChunk = chunks[x][y][z].get();
			if (generatedChunk != null) {
				checkChunkLoaded(generatedChunk, loadopt);
				return generatedChunk;
			}
			Spout.getLogger().severe("Chunk failed to generate!  (" + loadopt + ")");
			Spout.getLogger().info("Region " + this + ", chunk " + (getChunkX() + x) + ", " + (getChunkY() + y) + ", " + (getChunkZ() + z) + " : " + chunks[x][y][z]);
			Thread.dumpStack();
		}

		if (newChunk == null) {
			return null;
		}

		newChunk = setChunk(newChunk, x, y, z, regionData, false);
		checkChunkLoaded(newChunk, loadopt);
		return newChunk;
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z) {
		return getChunkFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z, LoadOption loadopt) {
		return getChunk(x >> Chunk.BLOCKS.BITS, y >> Chunk.BLOCKS.BITS, z >> Chunk.BLOCKS.BITS, loadopt);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position) {
		return getChunkFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ());
	}

	@Override
	public Chunk getChunkFromBlock(Vector3 position, LoadOption loadopt) {
		return getChunkFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ(), loadopt);
	}

	@Override
	public boolean hasChunk(int x, int y, int z) {
		return chunks[x & CHUNKS.MASK][y & CHUNKS.MASK][z & CHUNKS.MASK].get() != null;
	}

	@Override
	public boolean hasChunkAtBlock(int x, int y, int z) {
		return getWorld().hasChunkAtBlock(x, y, z);
	}

	@Override
	@DelayedWrite
	public void saveChunk(int x, int y, int z) {
		final SpoutChunk chunk = getChunk(x, y, z, LoadOption.NO_LOAD);
		if (chunk != null) {
			chunk.save();
		}
	}

	@Override
	public void unloadChunk(int x, int y, int z, boolean save) {
		final SpoutChunk chunk = getChunk(x, y, z, LoadOption.NO_LOAD);
		if (chunk != null) {
			chunk.unload(save);
		}
	}

	@Override
	public int getNumLoadedChunks() {
		return numberLoadedChunks.get();
	}

	@Override
	public void queueChunksForGeneration(List<Vector3> chunks) {
		for (final Vector3 vector : chunks) {
			queueChunkForGeneration(vector);
		}
	}

	@Override
	public void queueChunkForGeneration(Vector3 chunk) {
		final RegionGenerator generator = getRegionGenerator();
		if (generator != null) {
			generator.touchChunk(chunk.getFloorX(), chunk.getFloorY(), chunk.getFloorZ());
		}
	}

	@Override
	public <T extends CuboidLightBuffer> T getLightBuffer(LightingManager<T> manager, int x, int y, int z, LoadOption loadopt) {
		final SpoutChunk chunk = getChunk(x, y, z, loadopt);
		if (chunk == null) {
			return null;
		}
		return chunk.getLightBuffer(manager);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Cause<?> source) {
		return getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, source);
	}

	@Override
	public boolean addBlockData(int x, int y, int z, short data, Cause<?> source) {
		return getChunkFromBlock(x, y, z).addBlockData(x, y, z, data, source);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Cause<?> source) {
		return getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, data, source);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Cause<?> source) {
		return getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data, source);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, Cause<?> source) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, source);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, boolean set, Cause<?> source) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, set, source);
	}

	@Override
	public short clearBlockDataBits(int x, int y, int z, int bits, Cause<?> source) {
		return getChunkFromBlock(x, y, z).clearBlockDataBits(x, y, z, bits, source);
	}

	@Override
	public int getBlockDataField(int x, int y, int z, int bits) {
		return getChunkFromBlock(x, y, z).getBlockDataField(x, y, z, bits);
	}

	@Override
	public boolean isBlockDataBitSet(int x, int y, int z, int bits) {
		return getChunkFromBlock(x, y, z).isBlockDataBitSet(x, y, z, bits);
	}

	@Override
	public int setBlockDataField(int x, int y, int z, int bits, int value, Cause<?> source) {
		return getChunkFromBlock(x, y, z).setBlockDataField(x, y, z, bits, value, source);
	}

	@Override
	public int addBlockDataField(int x, int y, int z, int bits, int value, Cause<?> source) {
		return getChunkFromBlock(x, y, z).addBlockDataField(x, y, z, bits, value, source);
	}

	@Override
	public SpoutBlock getBlock(int x, int y, int z) {
		return getWorld().getBlock(x, y, z);
	}

	@Override
	public SpoutBlock getBlock(float x, float y, float z) {
		return getWorld().getBlock(x, y, z);
	}

	@Override
	public SpoutBlock getBlock(Vector3 position) {
		return getWorld().getBlock(position);
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(boolean backBuffer) {
		return getCuboid(getBlockX(), getBlockY(), getBlockZ(), Region.BLOCKS.SIZE, Region.BLOCKS.SIZE, Region.BLOCKS.SIZE, backBuffer);
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz) {
		return getCuboid(bx, by, bz, sx, sy, sz, true);
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz, boolean backBuffer) {
		final CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(bx, by, bz, sx, sy, sz, backBuffer);
		getCuboid(bx, by, bz, buffer);
		return buffer;
	}

	@Override
	public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer) {
		getWorld().getCuboid(getChunks(bx, by, bz, buffer), bx, by, bz, buffer);
	}

	@Override
	public void getCuboid(CuboidBlockMaterialBuffer buffer) {
		final Vector3 base = buffer.getBase();
		getCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer);
	}

	@Override
	public boolean commitCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		final Vector3 base = buffer.getBase();
		final int x = base.getFloorX();
		final int y = base.getFloorY();
		final int z = base.getFloorZ();
		return getWorld().commitCuboid(getChunks(x, y, z, buffer), buffer, cause);
	}

	@Override
	public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		final Vector3 base = buffer.getBase();
		setCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer, cause);
	}

	@Override
	public void setCuboid(int x, int y, int z, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		getWorld().setCuboid(getChunks(x, y, z, buffer), x, y, z, buffer, cause);
	}

	@Override
	public CuboidLightBuffer getLightBuffer(short id) {
		throw new UnsupportedOperationException("Region does not support light buffers");
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockMaterial(x, y, z);
	}

	@Override
	public int getBlockFullState(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockFullState(x, y, z);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockData(x, y, z);
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		return getWorld().getBiome(x, y, z);
	}

	@Override
	public SpoutRegion getLocalRegion(BlockFace face, LoadOption loadopt) {
		final Vector3 offset = face.getOffset();
		return getLocalRegion(offset.getFloorX(), offset.getFloorY(), offset.getFloorZ(), loadopt);
	}

	@Override
	public SpoutRegion getLocalRegion(int dx, int dy, int dz, LoadOption loadopt) {
		if (loadopt != LoadOption.NO_LOAD) {
			TickStage.checkStage(~TickStage.SNAPSHOT);
		}
		final AtomicReference<SpoutRegion> regionReference = neighbours[dx][dy][dz];
		SpoutRegion region = regionReference.get();
		if (region == null) {
			region = getWorld().getRegion(getX() + dx - 1, getY() + dy - 1, getZ() + dz - 1, loadopt);
			regionReference.compareAndSet(null, region);
		}
		return region;
	}

	@Override
	public SpoutChunk getLocalChunk(Chunk chunk, BlockFace face, LoadOption loadopt) {
		final Vector3 offset = face.getOffset();
		return getLocalChunk(chunk, offset.getFloorX(), offset.getFloorY(), offset.getFloorZ(), loadopt);
	}

	@Override
	public SpoutChunk getLocalChunk(Chunk chunk, int ox, int oy, int oz, LoadOption loadopt) {
		return getLocalChunk(chunk.getX(), chunk.getY(), chunk.getZ(), ox, oy, oz, loadopt);
	}

	@Override
	public SpoutChunk getLocalChunk(int x, int y, int z, int ox, int oy, int oz, LoadOption loadopt) {
		x &= CHUNKS.MASK;
		y &= CHUNKS.MASK;
		z &= CHUNKS.MASK;
		x += ox;
		y += oy;
		z += oz;
		return getLocalChunk(x, y, z, loadopt);
	}

	@Override
	public SpoutChunk getLocalChunk(int x, int y, int z, LoadOption loadopt) {
		final int dx = 1 + (x >> CHUNKS.BITS);
		final int dy = 1 + (y >> CHUNKS.BITS);
		final int dz = 1 + (z >> CHUNKS.BITS);
		final SpoutRegion region = getLocalRegion(dx, dy, dz, loadopt);
		if (region == null) {
			return null;
		}
		return region.getChunk(x, y, z, loadopt);
	}

	@Override
	public ImmutableHeightMapBuffer getLocalHeightMap(int x, int z, LoadOption loadopt) {
		final SpoutColumn column = getWorld().getColumn(getChunkX() + x, getChunkZ() + z, loadopt);
		if (column == null) {
			return null;
		}
		return column.getHeightMapBuffer();
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		setChunkModified(x, y, z);
		this.dynamicBlockTree.resetBlockUpdates(x, y, z);
	}

	@Override
	public void resetDynamicBlocks(Chunk c) {
		((SpoutChunk) c).setModified();
		this.dynamicBlockTree.resetBlockUpdates(c);
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		setChunkModified(x, y, z);
		this.dynamicBlockTree.syncResetBlockUpdates(x, y, z);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, boolean exclusive) {
		setChunkModified(x, y, z);
		return dynamicBlockTree.queueBlockUpdates(x, y, z, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, boolean exclusive) {
		setChunkModified(x, y, z);
		return dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, boolean exclusive) {
		setChunkModified(x, y, z);
		return dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate, data, exclusive);
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range) {
		final SpoutChunk chunk = getChunkFromBlock(x, y, z, LoadOption.NO_LOAD);
		if (chunk != null) {
			chunk.queueBlockPhysics(x, y, z, range);
		}
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z) {
		final SpoutChunk chunk = getChunkFromBlock(x, y, z, LoadOption.NO_LOAD);
		if (chunk != null) {
			chunk.updateBlockPhysics(x, y, z);
		}
	}

	@Override
	public void finalizeRun() {
		if (Spout.getPlatform() == Platform.SERVER) {
			for (int reap = 0 ; reap < SpoutConfiguration.REAP_CHUNKS_PER_TICK.getInt() ; reap++) {
				if (++reapX >= CHUNKS.SIZE) {
					reapX = 0;
					if (++reapY >= CHUNKS.SIZE) {
						reapY = 0;
						if (++reapZ >= CHUNKS.SIZE) {
							reapZ = 0;
						}
					}
				}
				final SpoutChunk chunk = chunks[reapX][reapY][reapZ].get();
				if (chunk != null) {
					chunk.compressIfRequired();
					boolean doUnload;
					if (doUnload = chunk.isReapable()) {
						if (ChunkUnloadEvent.getHandlerList().getRegisteredListeners().length > 0) {
							final ChunkUnloadEvent event = Spout.getEngine().getEventManager().callEvent(new ChunkUnloadEvent(chunk));
							if (event.isCancelled()) {
								doUnload = false;
							}
						}
					}
					if (doUnload) {
						chunk.unload(true);
					} else {
						if (!chunk.isPopulated()) {
							chunk.queueForPopulation(false);
						}
					}
				}
			}
		}
		//Note: This must occur after any chunks are reaped, because reaping chunks may kill entities, which need to be finalized
		getEntityManager().finalizeRun();
	}

	@Override
	public void preSnapshotRun() {
		getEntityManager().preSnapshotRun();

		SpoutChunk chunk;
		while ((chunk = dirtyChunkQueue.poll()) != null) {
			if (chunk.isDirty()) {
				if (Spout.getPlatform() == Platform.SERVER) {
					for (Player player : chunk.getObservingPlayers()) {
						if (player.isOnline()) {
							final PlayerNetworkComponent network = player.getNetwork();
							if (!chunk.isDirtyOverflow() && !chunk.isLightDirty()) {
								for (int i = 0 ; true ; i++) {
									Vector3 block = chunk.getDirtyBlock(i);
									if (block == null) {
										break;
									}

									try {
										network.callProtocolEvent(new BlockUpdateEvent(chunk, block.getFloorX(), block.getFloorY(), block.getFloorZ()));
									} catch (Exception e) {
										Spout.getEngine().getLogger().log(Level.SEVERE, "Exception thrown by plugin when attempting to send a block update to " + player.getName());
									}
								}
							} else {
								network.callProtocolEvent(new ChunkSendEvent(chunk));
							}
						}
					}
					if (ChunkUpdatedEvent.getHandlerList().getRegisteredListeners().length == 0) {
						return;
					}
					final ChunkUpdatedEvent evt;
					if (chunk.isDirtyOverflow()) {
						evt = new ChunkUpdatedEvent(chunk, null);
					} else {
						final ArrayList<Vector3> lst = new ArrayList<>();
						boolean done = false;
						for (int i = 0 ; !done ; i++) {
							Vector3 v = chunk.getDirtyBlock(i);
							if (v != null) {
								lst.add(v);
							} else {
								done = true;
							}
						}
						evt = new ChunkUpdatedEvent(chunk, lst);
					}
					Spout.getEventManager().callDelayedEvent(evt);
					chunk.resetDirtyArrays();
					chunk.setLightDirty(false);
				} else {
					chunk.render(BlockFaces.NONE);
				}
			}
		}

		SpoutChunkSnapshotFuture snapshotFuture;
		while ((snapshotFuture = snapshotQueue.poll()) != null) {
			snapshotFuture.run();
		}


		for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
			for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
				for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
					final SpoutChunk spoutChunk = chunks[dx][dy][dz].get();
					if (spoutChunk != null) {
						spoutChunk.updateExpiredObservers();
						if (Spout.getPlatform() == Platform.SERVER) {
							if (!spoutChunk.getDataMap().getDeltaMap().isEmpty()) {
								for (Player entity : spoutChunk.getObservingPlayers()) {
									entity.getNetwork().callProtocolEvent(new ChunkDatatableSendEvent(spoutChunk));
								}
								spoutChunk.getDataMap().resetDelta();
							}
						}
					}
				}
			}
		}
		if (Spout.getPlatform() == Platform.SERVER) {
			entityManager.syncEntities();
		}
	}

	@Override
	public void copySnapshotRun() {
		getEntityManager().copyAllSnapshots();

		dynamicBlockTree.setRegionThread(Thread.currentThread());

		final SnapshotManager snapshotManager = this.snapshotManager;
		snapshotManager.copyAllSnapshots();

		final SetQueue<Cube> saveQueue = getMarkedSaveQueue();

		boolean empty = false;
		Cube cube;

		if (Spout.getPlatform() == Platform.SERVER) {
			while ((cube = saveQueue.poll()) != null) {
				if (cube == this) {
					for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
						for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
							for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
								SpoutChunk c = getChunk(dx, dy, dz, LoadOption.NO_LOAD);
								if (processChunkSaveUnload(c)) {
									empty = true;
								}
							}
						}
					}
					// No point in checking any others, since all processed
					saveQueue.clear();
					break;
				}
				empty |= processChunkSaveUnload((SpoutChunk) cube);
			}
		} else {
			saveQueue.clear(); //TODO: Is this correct?
		}
		// Updates on nulled chunks
		snapshotManager.copyAllSnapshots();
		if (empty) {
			this.source.removeRegion(this);
		}
	}

	@Override
	public void startTickRun(int stage, long delta) {
		final float dt = delta / 1000f;
		switch (stage) {
			case 0: {
				getTaskManager().heartbeat(delta);
				//Update block components
				for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
					for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
						for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
							final SpoutChunk chunk = chunks[dx][dy][dz].get();
							if (chunk != null && chunk.isLoaded()) {
								chunk.tickBlockComponents(dt);
							}
						}
					}
				}
				//Update entities
				for (SpoutEntity ent : getEntityManager().getAll()) {
					try {
						ent.tick(dt);
					} catch (Exception e) {
						Spout.getEngine().getLogger().severe("Unhandled exception during tick for " + ent.toString());
						e.printStackTrace();
					}
				}

				if (Spout.getPlatform() == Platform.SERVER) {
					//Update autosave
					for (int dx = 0 ; dx < CHUNKS.SIZE ; dx++) {
						for (int dy = 0 ; dy < CHUNKS.SIZE ; dy++) {
							for (int dz = 0 ; dz < CHUNKS.SIZE ; dz++) {
								final SpoutChunk chunk = chunks[dx][dy][dz].get();
								if (chunk != null && chunk.isLoaded()) {
									if (chunk.getAutosaveTicks() > 1) {
										chunk.setAutosaveTicks(chunk.getAutosaveTicks() - 1);
									} else {
										if (chunk.getAutosaveTicks() == 1) {
											chunk.setAutosaveTicks(0);
											chunk.save();
										}
									}
								}
							}
						}
					}
					//Update population
					final SetQueue<SpoutChunk> populationPriorityQueue = this.populationPriorityQueue;
					final SetQueue<SpoutChunk> populationQueue = this.populationQueue;

					for (int i = 0 ; i < POPULATE_PER_TICK && !this.scheduler.isServerOverloaded() ; i++) {
						SpoutChunk toPopulate = populationPriorityQueue.poll();
						if (toPopulate == null) {
							toPopulate = populationQueue.poll();
							if (toPopulate == null) {
								break;
							}
						}
						if (toPopulate.isLoaded()) {
							if (!toPopulate.isObserved()) {
								continue;
							}
							boolean surrounded = true;
							for (int nx = -1 ; nx <= 1 ; nx++) {
								final int nxx = nx + toPopulate.getX();
								for (int ny = -1 ; ny <= 1 ; ny++) {
									final int nyy = ny + toPopulate.getY();
									for (int nz = -1 ; nz <= 1 ; nz++) {
										final int nzz = nz + toPopulate.getZ();
										final Chunk chunk = getWorld().getChunk(nxx, nyy, nzz, LoadOption.LOAD_ONLY);
										if (chunk == null) {
											surrounded = false;
											getWorld().queueChunkForGeneration(new Vector3(nxx, nyy, nzz));
										}
									}
								}
							}
							if (surrounded && toPopulate.populate()) {
								if (this.scheduler.isServerOverloaded()) {
									break;
								}
								continue;
							}
						}
						i--;
					}
					//unload chunks
					SpoutChunk toUnload = unloadQueue.poll();
					int unloadAmt = SpoutConfiguration.UNLOAD_CHUNKS_PER_TICK.getInt();
					while (toUnload != null) {
						unloadAmt--;
						boolean do_unload = true;
						if (ChunkUnloadEvent.getHandlerList().getRegisteredListeners().length > 0) {
							final ChunkUnloadEvent event = Spout.getEngine().getEventManager().callEvent(new ChunkUnloadEvent(toUnload));
							if (event.isCancelled()) {
								do_unload = false;
							}
						}
						if (do_unload) {
							toUnload.unload(true);
						}
						if (unloadAmt > 0) {
							toUnload = getUnloadQueue().poll();
						} else {
							break;
						}
					}
				}
				break;
			}
			case 1: {
				for (final Entity entity : getAll()) {
					((SpoutPhysicsComponent) entity.getPhysics()).onPrePhysicsTick();
				}
				getSimulation().update();
				for (final Entity entity : getAll()) {
					((SpoutPhysicsComponent) entity.getPhysics()).onPostPhysicsTick(dt);
				}
				break;
			}
			case 2: {
				break;
			}
			default: {
				throw new IllegalStateException("Number of states exceeded limit for SpoutRegion");
			}
		}
	}

	@Override
	public void runPhysics(int sequence) {
		if (Spout.getPlatform() != Platform.SERVER) {
			return;
		}
		this.dynamicBlockTree.setRegionThread(Thread.currentThread());
		if (sequence == -1) {
			runLocalPhysics();
		} else {
			if (sequence == getSequence()) {
				runGlobalPhysics();
			} else {
				throw new IllegalArgumentException("Invalid physics sequence: " + sequence);
			}
		}
	}

	@Override
	public void runDynamicUpdates(long threshold, int sequence) {
		final DynamicBlockUpdateTree updateTree = this.dynamicBlockTree;
		scheduler.addUpdates(updateTree.getLastUpdates());
		updateTree.resetLastUpdates();
		updateTree.setRegionThread(Thread.currentThread());

		if (sequence == -1) {
			runLocalDynamicUpdates(threshold);
		} else {
			if (sequence == getSequence()) {
				runGlobalDynamicUpdates();
			} else {
				throw new IllegalArgumentException("Invalid dynamic physics sequence: " + sequence);
			}
		}
	}

	@Override
	public void runLighting(int sequence) {
		if (sequence != getSequence()) {
			throw new IllegalArgumentException("Attempted to run lighting update using the sequence: " + sequence);
		}

		final LocalRegionChunkCuboidBlockMaterialBufferWrapper blockMaterialBuffer = this.blockMaterialBuffer;

		if (blockMaterialBuffer == null) {
			this.blockMaterialBuffer = new LocalRegionChunkCuboidBlockMaterialBufferWrapper(this, LoadOption.LOAD_ONLY, BlockMaterial.UNGENERATED);
		}

		final LightingManager<?>[] managers = getWorld().getLightingManagers();
		ChunkCuboidLightBufferWrapper<?>[] lightBuffers = this.lightBuffers;

		if (lightBuffers == null || lightBuffers.length != managers.length) {
			this.lightBuffers = new ChunkCuboidLightBufferWrapper[managers.length];
			//Re-assign the local buffer
			lightBuffers = this.lightBuffers;
		}

		ImmutableHeightMapBuffer heightMapBuffer = this.heightMapBuffer;

		if (heightMapBuffer == null) {
			this.heightMapBuffer = new LocalRegionChunkHeightMapBufferWrapper(this, LoadOption.LOAD_ONLY);
			//Re-assign the local buffer
			heightMapBuffer = this.heightMapBuffer;
		}

		for (int i = 0 ; i < lightBuffers.length ; i++) {
			final short managerId = managers[i].getId();
			final ChunkCuboidLightBufferWrapper<?> lightBuffer = lightBuffers[i];
			if (lightBuffer == null || lightBuffer.getManagerId() != managerId) {
				lightBuffers[i] = new LocalRegionChunkCuboidLightBufferWrapper(this, managerId, LoadOption.LOAD_ONLY);
			}
		}

		final List<SpoutChunk> newChunksList = new LinkedList<>();

		int newChunksCount = 0;
		SpoutChunk newChunk;
		while ((newChunk = newChunkQueue.poll()) != null) {
			newChunksList.add(newChunk);
			newChunksCount++;
		}

		int columns = 0;

		for (SpoutColumn col : this.dirtyColumnQueue) {
			columns += col.getDirtyColumns();
		}

		int cuboids = 0;
		int blocks = 0;

		if (columns > 0) {
			final SetQueue<SpoutColumn> dirtyColumns = this.dirtyColumnQueue;

			final int[] colX = new int[columns];
			final int[] colZ = new int[columns];
			final int[] oldH = new int[columns];
			final int[] newH = new int[columns];

			final int minY = getBlockY();
			final int maxY = minY + BLOCKS.SIZE;

			int pos = 0;

			for (SpoutColumn col : dirtyColumns) {
				pos = col.fillDirty(pos, colX, newH, oldH, colZ, minY, maxY);
			}

			if (pos > 0) {
				for (int i = 0 ; i < managers.length ; i++) {
					managers[i].resolveColumnsUnchecked(lightBuffers[i], blockMaterialBuffer, heightMapBuffer, colX, colZ, oldH, newH, pos);
				}
			}
		}
		for (SpoutChunk c : this.dirtyChunkQueue) {
			if (c.isDirtyOverflow()) {
				cuboids++;
			} else {
				blocks += c.getDirtyBlocks();
			}
		}

		final SpoutChunk[] newChunksArray = new SpoutChunk[newChunksCount];
		final SpoutChunk[] dirtyChunks = new SpoutChunk[cuboids];
		final int[] x = new int[blocks];
		final int[] y = new int[blocks];
		final int[] z = new int[blocks];

		blocks = 0;
		cuboids = 0;

		final SetQueue<SpoutChunk> dirtyChunkQueue = this.dirtyChunkQueue;

		for (SpoutChunk c : dirtyChunkQueue) {
			if (c.isDirtyOverflow()) {
				dirtyChunks[cuboids++] = c;
			} else {
				final int dirtyBlocks = c.getDirtyBlocks();
				for (int i = 0 ; i < dirtyBlocks ; i++) {
					final Vector3 v = c.getDirtyBlock(i);
					x[blocks] = c.getBlockX() + (v.getFloorX() & Chunk.BLOCKS.MASK);
					y[blocks] = c.getBlockY() + (v.getFloorY() & Chunk.BLOCKS.MASK);
					z[blocks] = c.getBlockZ() + (v.getFloorZ() & Chunk.BLOCKS.MASK);
					blocks++;
				}
			}
		}

		int i = 0;
		for (SpoutChunk c : newChunksList) {
			newChunksArray[i++] = c;
		}

		if (newChunksArray.length > 0) {
			resolveCuboids(newChunksArray, managers, true);
		}

		if (dirtyChunks.length > 0) {
			resolveCuboids(dirtyChunks, managers, false);
		}
		if (x.length > 0) {
			for (int dx = 0 ; dx < managers.length ; dx++) {
				managers[dx].resolveUnchecked(lightBuffers[dx], blockMaterialBuffer, heightMapBuffer, x, y, z, x.length);
			}
		}

		this.scheduler.addUpdates(lightingUpdates);
		this.lightingUpdates = 0;

		blockMaterialBuffer.clear();

		for (i = 0 ; i < lightBuffers.length ; i++) {
			if (lightBuffers[i] != null) {
				lightBuffers[i].clear();
			}
		}

		if (heightMapBuffer != null) {
			((LocalRegionChunkHeightMapBufferWrapper) heightMapBuffer).clear();
		}
	}

	@Override
	public final int getSequence() {
		return updateSequence;
	}

	@Override
	public long getFirstDynamicUpdateTime() {
		return this.dynamicBlockTree.getFirstDynamicUpdateTime();
	}

	@Override
	public final Thread getExecutionThread() {
		return executionThread;
	}

	@Override
	public void setExecutionThread(Thread executionThread) {
		this.executionThread = executionThread;
	}

	@Override
	public final int getMaxStage() {
		return 2;
	}

	private SpoutChunk[][][] getChunks(int x, int y, int z, CuboidBlockMaterialBuffer buffer) {
		final Vector3 size = buffer.getSize();

		final int blockX = getBlockX();
		final int blockY = getBlockY();
		final int blockZ = getBlockZ();

		final int startX = Math.max(x, blockX);
		final int startY = Math.max(y, blockY);
		final int startZ = Math.max(z, blockZ);

		final int endX = Math.min(blockX + BLOCKS.SIZE - 1, x + size.getFloorX());
		final int endY = Math.min(blockY + BLOCKS.SIZE - 1, y + size.getFloorY());
		final int endZ = Math.min(blockZ + BLOCKS.SIZE - 1, z + size.getFloorZ());

		final Chunk start = getChunkFromBlock(startX, startY, startZ);
		final Chunk end = getChunkFromBlock(endX - 1, endY - 1, endZ - 1);

		final int chunkSizeX = end.getX() - start.getX() + 1;
		final int chunkSizeY = end.getY() - start.getY() + 1;
		final int chunkSizeZ = end.getZ() - start.getZ() + 1;

		final int chunkStartX = start.getX();
		final int chunkStartY = start.getY();
		final int chunkStartZ = start.getZ();

		final int chunkEndX = end.getX();
		final int chunkEndY = end.getY();
		final int chunkEndZ = end.getZ();

		final SpoutChunk[][][] chunks = new SpoutChunk[chunkSizeX][chunkSizeY][chunkSizeZ];
		for (int dx = chunkStartX ; dx <= chunkEndX ; dx++) {
			for (int dy = chunkStartY ; dy <= chunkEndY ; dy++) {
				for (int dz = chunkStartZ ; dz <= chunkEndZ ; dz++) {
					final SpoutChunk chunk = getChunk(dx, dy, dz, LoadOption.LOAD_GEN);
					if (chunk == null) {
						throw new IllegalStateException("Null chunk loaded with LoadOption.LOAD_GEN");
					}
					chunks[dx - chunkStartX][dy - chunkStartY][dz - chunkStartZ] = chunk;
				}
			}
		}
		return chunks;
	}

	/**
	 * TODO:
	 *
	 * @param newChunk
	 * @param x
	 * @param y
	 * @param z
	 * @param dataForRegion
	 * @param generated
	 * @return
	 */
	private SpoutChunk setChunk(SpoutChunk newChunk, int x, int y, int z, ChunkDataForRegion dataForRegion, boolean generated) {
		final AtomicReference<SpoutChunk> chunkReference = chunks[x][y][z];
		while (true) {
			if (chunkReference.compareAndSet(null, newChunk)) {
				if (generated) {
					newChunk.notifyColumn();
					newChunk.queueNew();
				}
				this.numberLoadedChunks.incrementAndGet();
				if (dataForRegion != null) {
					final List<SpoutEntitySnapshot> snapshots = dataForRegion.loadedEntities;
					for (SpoutEntitySnapshot snapshot : snapshots) {
						final SpoutEntity entity = (SpoutEntity) snapshot.getReference();
						entity.setupInitialChunk();
						getEntityManager().addEntity(entity);
					}
					this.dynamicBlockTree.addDynamicBlockUpdates(dataForRegion.loadedUpdates);
				}

				Spout.getEventManager().callDelayedEvent(new ChunkLoadEvent(newChunk, generated));
				return newChunk;
			}

			final SpoutChunk oldChunk = chunkReference.get();
			if (oldChunk != null) {
				newChunk.setUnloadedUnchecked();
				return oldChunk;
			}
		}
	}

	/**
	 * Attempts to set the provided chunk if it hasn't been generated.
	 * <br></br>
	 * This method is designed to only be called from {@link RegionGenerator}.
	 *
	 * @param newChunk New chunk the be set
	 * @param x The x-coordinate for the new chunk
	 * @param y The x-coordinate for the new chunk
	 * @param z The x-coordinate for the new chunk
	 * @return True if the new chunk was successfully set
	 */
	protected boolean setChunkIfNotGeneratedWithoutLock(SpoutChunk newChunk, int x, int y, int z) {
		int cx = newChunk.getX();
		int cy = newChunk.getY();
		int cz = newChunk.getZ();
		//Files never exist on the client
		boolean exists = Spout.getPlatform() == Platform.CLIENT ? false : this.inputStreamExists(cx, cy, cz);
		if (exists) {
			return false;
		}
		// chunk has not been generated
		return setChunk(newChunk, x, y, z, null, true) == newChunk;
	}

	/**
	 * Checks to see if the chunk provided is loaded
	 *
	 * @param chunk Chunk to check if loaded
	 * @param loadopt The load option for this chunk
	 */
	private void checkChunkLoaded(SpoutChunk chunk, LoadOption loadopt) {
		if (loadopt.loadIfNeeded()) {
			if (!chunk.cancelUnload()) {
				throw new IllegalStateException("Unloaded chunk returned by getChunk");
			}
		}
	}

	/**
	 * TODO:
	 *
	 * @param chunks
	 * @param managers
	 * @param init
	 */
	private void resolveCuboids(SpoutChunk[] chunks, LightingManager<?>[] managers, boolean init) {
		final int cuboids = chunks.length;
		final int[] bx = new int[cuboids];
		final int[] by = new int[cuboids];
		final int[] bz = new int[cuboids];
		if (init) {
			for (int i = 0 ; i < cuboids ; i++) {
				final SpoutChunk chunk = chunks[i];
				bx[i] = chunk.getBlockX();
				by[i] = chunk.getBlockY();
				bz[i] = chunk.getBlockZ();
			}
			for (int i = 0 ; i < managers.length ; i++) {
				managers[i].initChunksUnchecked(lightBuffers[i], blockMaterialBuffer, heightMapBuffer, chunks);
			}
		} else {
			final int[] tx = new int[cuboids];
			final int[] ty = new int[cuboids];
			final int[] tz = new int[cuboids];
			for (int i = 0 ; i < cuboids ; i++) {
				final SpoutChunk chunk = chunks[i];
				final IntVector3 min = chunk.getMinDirty();
				final IntVector3 max = chunk.getMaxDirty();
				bx[i] = chunk.getBlockX() + min.getX();
				by[i] = chunk.getBlockY() + min.getY();
				bz[i] = chunk.getBlockZ() + min.getZ();
				tx[i] = chunk.getBlockX() + max.getX() + 1;
				ty[i] = chunk.getBlockY() + max.getY() + 1;
				tz[i] = chunk.getBlockZ() + max.getZ() + 1;
			}
			for (int i = 0 ; i < managers.length ; i++) {
				managers[i].resolveChunksUnchecked(lightBuffers[i], blockMaterialBuffer, heightMapBuffer, bx, by, bz, tx, ty, tz, cuboids);
			}
		}
	}

	/**
	 * Gets the key used by the chunk provided in the chunk store
	 *
	 * @param chunkX The x-coordinate of the chunk
	 * @param chunkY The y-coordinate of the chunk
	 * @param chunkZ The z-coordinate of the chunk
	 * @return The key for the chunk provided
	 */
	public static int getChunkKey(int chunkX, int chunkY, int chunkZ) {
		chunkX &= CHUNKS.MASK;
		chunkY &= CHUNKS.MASK;
		chunkZ &= CHUNKS.MASK;

		int key = 0;
		key |= chunkX;
		key |= chunkY << CHUNKS.BITS;
		key |= chunkZ << (CHUNKS.BITS << 1);

		return key;
	}

	/**
	 * Check if file for this region exists
	 *
	 * @param world The region's world
	 * @param x region x coordinate
	 * @param y region y coordinate
	 * @param z region z coordinate
	 * @return true if exists, false if it does not exist
	 */
	public static boolean regionFileExists(World world, int x, int y, int z) {
		if (Spout.getPlatform() == Platform.CLIENT) {
			return false;
		}
		File worldDirectory = world.getDirectory();
		File regionDirectory = new File(worldDirectory, "region");
		File regionFile = new File(regionDirectory, "reg" + x + "_" + y + "_" + z + ".spr");
		return regionFile.exists();
	}
}