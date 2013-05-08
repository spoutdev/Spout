/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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

import gnu.trove.iterator.TIntIterator;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.component.Component;
import org.spout.api.component.type.BlockComponent;
import org.spout.api.component.type.EntityComponent;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.Cause;
import org.spout.api.event.chunk.ChunkLoadEvent;
import org.spout.api.event.chunk.ChunkPopulateEvent;
import org.spout.api.event.chunk.ChunkUnloadEvent;
import org.spout.api.event.chunk.ChunkUpdatedEvent;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.api.geo.cuboid.Cube;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.lighting.LightingManager;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.GenericMath;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Vector3;
import org.spout.api.math.VectorMath;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.render.RenderMaterial;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.api.util.cuboid.ChunkCuboidLightBufferWrapper;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.cuboid.ImmutableCuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.ImmutableHeightMapBuffer;
import org.spout.api.util.cuboid.LocalRegionChunkCuboidBlockMaterialBufferWrapper;
import org.spout.api.util.cuboid.LocalRegionChunkCuboidLightBufferWrapper;
import org.spout.api.util.cuboid.LocalRegionChunkHeightMapBufferWrapper;
import org.spout.api.util.list.concurrent.setqueue.SetQueue;
import org.spout.api.util.list.concurrent.setqueue.SetQueueElement;
import org.spout.api.util.map.TByteTripleObjectHashMap;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.api.util.set.TByteTripleHashSet;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.filesystem.ChunkDataForRegion;
import org.spout.engine.filesystem.versioned.ChunkFiles;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.world.collision.RegionShape;
import org.spout.engine.world.collision.SpoutPhysicsWorld;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;
import org.spout.engine.world.dynamic.DynamicBlockUpdateTree;
import org.spout.engine.world.light.ServerLightStore;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.voxel.VoxelWorldShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class SpoutRegion extends Region implements AsyncManager {
	private AtomicInteger numberActiveChunks = new AtomicInteger();

	protected final SetQueue<Cube> saveMarkedQueue = new SetQueue<Cube>(CHUNKS.VOLUME + 1);
	private final RegionSetQueueElement saveMarkedElement = new RegionSetQueueElement(saveMarkedQueue, this);
	
	private Thread executionThread;
	
	@SuppressWarnings("unchecked")
	public AtomicReference<SpoutChunk>[][][] chunks = new AtomicReference[CHUNKS.SIZE][CHUNKS.SIZE][CHUNKS.SIZE];
	/**
	 * The maximum number of chunks that will be processed for population each
	 * tick.
	 */
	private static final int POPULATE_PER_TICK = 20;
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
	protected final EntityManager entityManager = new EntityManager(this);
	/**
	 * Reference to the persistent ByteArrayArray that stores chunk data
	 */
	private final BAAWrapper chunkStore;
	private final Queue<SpoutChunkSnapshotFuture> snapshotQueue = new ConcurrentLinkedQueue<SpoutChunkSnapshotFuture>();
	
	protected SetQueue<SpoutChunk> unloadQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	public static final byte POPULATE_CHUNK_MARGIN = 1;
	/**
	 * The sequence number for executing inter-region physics and dynamic updates
	 */
	private final int updateSequence;
	/**
	 * The chunks that received a lighting change and need an update
	 */
	private final TByteTripleHashSet lightDirtyChunks = new TByteTripleHashSet();
	/**
	 * A queue of chunks that need to be populated
	 */
	protected final SetQueue<SpoutChunk> populationQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	protected final SetQueue<SpoutChunk> populationPriorityQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	private final Object generateSync = new Object();
	private final AtomicBoolean generated = new AtomicBoolean(false);
	private final AtomicBoolean[][] generatedColumns = new AtomicBoolean[CHUNKS.SIZE][CHUNKS.SIZE];
	private final SpoutTaskManager taskManager;
	private final List<Thread> meshThread;
	private final SpoutScheduler scheduler;
	private final LinkedHashMap<SpoutPlayer, TByteTripleHashSet> observers = new LinkedHashMap<SpoutPlayer, TByteTripleHashSet>();
	protected final SetQueue<SpoutChunk> chunkObserversDirtyQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	protected final SetQueue<SpoutChunk> localPhysicsChunkQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	protected final SetQueue<SpoutChunk> globalPhysicsChunkQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	protected final SetQueue<SpoutChunk> dirtyChunkQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	protected final SetQueue<SpoutChunk> newChunkQueue = new SetQueue<SpoutChunk>(CHUNKS.VOLUME);
	protected final SetQueue<SpoutColumn> dirtyColumnQueue;
	private final DynamicBlockUpdateTree dynamicBlockTree;
	private List<DynamicBlockUpdate> multiRegionUpdates = null;
	private boolean renderQueueEnabled = false;

	private final TByteTripleObjectHashMap<SpoutChunkSnapshotModel> renderChunkQueued = new TByteTripleObjectHashMap<SpoutChunkSnapshotModel>();
	private final ConcurrentSkipListSet<SpoutChunkSnapshotModel> renderChunkQueue = new ConcurrentSkipListSet<SpoutChunkSnapshotModel>();

	private final AtomicReference<SpoutRegion>[][][] neighbours;

	//Physics
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final DiscreteDynamicsWorld simulation;
	private final CollisionDispatcher dispatcher;
	private final BroadphaseInterface broadphase;
	private final CollisionConfiguration configuration;
	private final SequentialImpulseConstraintSolver solver;

	@SuppressWarnings("unchecked")
	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source) {
		super(world, x * Region.BLOCKS.SIZE, y * Region.BLOCKS.SIZE, z * Region.BLOCKS.SIZE);
		this.source = source;

		this.dirtyColumnQueue = world.getColumnDirtyQueue(getX(), getZ());
		
		int xx = GenericMath.mod(getX(), 3);
		int yy = GenericMath.mod(getY(), 3);
		int zz = GenericMath.mod(getZ(), 3);
		updateSequence = (xx * 9) + (yy * 3) + zz;

		if (Spout.getPlatform() == Platform.CLIENT) {
			meshThread = new ArrayList<Thread>();
			for(int i = 0; i < 1; i++ ){//TODO : Make a option to choice the number of thread to make mesh
				meshThread.add(new MeshGeneratorThread());
			}
		} else {
			meshThread = null;
		}

		dynamicBlockTree = new DynamicBlockUpdateTree(this);

		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
					chunks[dx][dy][dz] = new AtomicReference<SpoutChunk>(null);
				}
			}
		}

		neighbours = new AtomicReference[3][3][3];

		final int rx = getX();
		final int ry = getY();
		final int rz = getZ();
		for (int dx = 0; dx < 3; dx++) {
			for (int dy = 0; dy < 3; dy++) {
				for (int dz = 0; dz < 3; dz++) {
					neighbours[dx][dy][dz] = new AtomicReference<SpoutRegion>(world.getRegion(rx + dx - 1, ry + dy - 1, rz + dz - 1, LoadOption.NO_LOAD));
				}
			}
		}

		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
				generatedColumns[dx][dz] = new AtomicBoolean(false);
			}

		}

		this.chunkStore = world.getRegionFile(getX(), getY(), getZ());
		taskManager = new SpoutTaskManager(world.getEngine().getScheduler(), null, this, world.getAge());
		scheduler = (SpoutScheduler) (Spout.getEngine().getScheduler());
		
		scheduler.addAsyncManager(this);

		//Physics
		broadphase = new DbvtBroadphase();
		broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
		configuration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(configuration);
		solver = new SequentialImpulseConstraintSolver();
		simulation = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, configuration);
		simulation.setGravity(new Vector3f(0, -9.81F, 0));
		simulation.getSolverInfo().splitImpulse = true;
		final SpoutPhysicsWorld physicsInfo = new SpoutPhysicsWorld(this);
		final VoxelWorldShape simulationShape = new RegionShape(physicsInfo, this);
		final Matrix3f rot = new Matrix3f();
		rot.setIdentity();
		final DefaultMotionState regionMotionState = new DefaultMotionState(new Transform(new Matrix4f(rot, new Vector3f(0, 0, 0), 1.0f)));
		final RigidBodyConstructionInfo regionBodyInfo = new RigidBodyConstructionInfo(0, regionMotionState, simulationShape, new Vector3f(0f, 0f, 0f));
		final RigidBody regionBody = new RigidBody(regionBodyInfo);
		//TODO Recheck Terasology code for more correct flags
		regionBody.setCollisionFlags(CollisionFlags.STATIC_OBJECT | regionBody.getCollisionFlags());
		simulation.addRigidBody(regionBody);
	}

	public DiscreteDynamicsWorld getSimulation() {
		return simulation;
	}

	public ReentrantReadWriteLock getPhysicsLock() {
		return lock;
	}

	public void startMeshGeneratorThread() {
		if (meshThread != null) {
			for(Thread thread : meshThread){
				thread.start();
			}
		}
	}

	public void stopMeshGeneratorThread() {
		if (meshThread != null) {
			for(Thread thread : meshThread){
				thread.interrupt();
			}
		}
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
		switch (loadopt) {
			case LOAD_ONLY: TickStage.checkStage(~TickStage.SNAPSHOT); break;
			case LOAD_GEN: TickStage.checkStage(~(TickStage.SNAPSHOT | TickStage.PRESNAPSHOT | TickStage.LIGHTING));
		}

		x &= CHUNKS.MASK;
		y &= CHUNKS.MASK;
		z &= CHUNKS.MASK;

		final SpoutChunk chunk = chunks[x][y][z].get();
		if (chunk != null) {
			checkChunkLoaded(chunk, loadopt);
			return chunk;
		}

		SpoutChunk newChunk = null;
		ChunkDataForRegion dataForRegion = null;

		boolean fileExists = this.inputStreamExists(x, y, z);

		if (loadopt.loadIfNeeded() && fileExists) {
			dataForRegion = new ChunkDataForRegion();
			newChunk = ChunkFiles.loadChunk(this, x, y, z, this.getChunkInputStream(x, y, z), dataForRegion);
			if (newChunk == null) {
				Spout.getLogger().severe("Unable to load chunk at location " + (getChunkX() + x) + ", " + (getChunkY() + y) + ", " + (getChunkZ() + z) + " in region " + this + ", regenerating chunks");
				fileExists = false;
			}
		}

		if (loadopt.generateIfNeeded() && !fileExists && newChunk == null) {
			boolean oldGenerated = generated.get();
			generateColumn(x, z);
			final SpoutChunk generatedChunk = chunks[x][y][z].get();
			if (generatedChunk != null) {
				checkChunkLoaded(generatedChunk, loadopt);
				return generatedChunk;
			} else {
				Spout.getLogger().severe("Chunk failed to generate!  (" + loadopt + ") Region marked as generated: before " + oldGenerated + ", after " + generated.get());
				Spout.getLogger().info("Region " + this + ", chunk " + (getChunkX() + x) + ", " + (getChunkY() + y) + ", " + (getChunkZ() + z) + " : " + chunks[x][y][z]);
				Thread.dumpStack();
			}
		}

		if (newChunk == null) {
			return null;
		}

		SpoutChunk c = setChunk(newChunk, x, y, z, dataForRegion, false);
		checkChunkLoaded(c, loadopt);
		return c;
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position) {
		return this.getChunkFromBlock(position, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position, LoadOption loadopt) {
		return this.getChunkFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ(), loadopt);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z, LoadOption loadopt) {
		return this.getChunk(x >> Chunk.BLOCKS.BITS, y >> Chunk.BLOCKS.BITS, z >> Chunk.BLOCKS.BITS, loadopt);
	}
	
	
	private void generateColumn(int x, int z) {
		AtomicBoolean generated = generatedColumns[x][z];
		if (generated.get()) {
			return;
		}
		
		synchronized(generated) {
			if (generated.get()) {
				return;
			}
			int cx = getChunkX();
			int cy = getChunkY();
			int cz = getChunkZ();
			
			int xx = x;
			int cxx = cx + xx;
			int zz = z;
			int czz = cz + zz;

			final SpoutWorld world = getWorld();

			final CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(cxx << Chunk.BLOCKS.BITS, cy << Chunk.BLOCKS.BITS, czz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE, Region.BLOCKS.SIZE, Chunk.BLOCKS.SIZE);
			getWorld().getGenerator().generate(buffer, cxx, cy, czz, world);

			for (int yy = Region.CHUNKS.SIZE - 1; yy >= 0; yy--) {
				int cyy = cy + yy;
				final CuboidBlockMaterialBuffer chunk = new CuboidBlockMaterialBuffer(cxx << Chunk.BLOCKS.BITS, cyy << Chunk.BLOCKS.BITS, czz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE);
				chunk.write(buffer);
				SpoutChunk newChunk = new SpoutChunk(world, this, cxx, cyy, czz, chunk.getRawId(), chunk.getRawData(), null);
				SpoutChunk currentChunk = setChunkIfNotGenerated(newChunk, xx, yy, zz, null, true);
				if (currentChunk != newChunk) {
					if (currentChunk == null) {
						Spout.getLogger().info("Warning: Unable to set generated chunk, new Chunk " + newChunk + " chunk in memory " + currentChunk);
					}
				} else {
					newChunk.compressRaw();
					newChunk.setModified();
				}
			}
			if (!generated.compareAndSet(false, true)) {
				throw new IllegalStateException("Column " + x + ", " + z + " generated twice");
			}
		}

	}

	protected CuboidBlockMaterialBuffer generateChunks() {
		if (generated.get()) {
			return null;
		}
		synchronized(generated) {
			if (generated.get()) {
				return null;
			}
			int cx = getChunkX();
			int cy = getChunkY();
			int cz = getChunkZ();

			final SpoutWorld world = getWorld();

			final CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(cx << Chunk.BLOCKS.BITS, cy << Chunk.BLOCKS.BITS, cz << Chunk.BLOCKS.BITS, Region.BLOCKS.SIZE, Region.BLOCKS.SIZE, Region.BLOCKS.SIZE);
			getWorld().getGenerator().generate(buffer, cx, cy, cz, world);
			
			if (!generated.compareAndSet(false, true)) {
				throw new IllegalStateException("Region generated twice");
			}
			
			return buffer;
		}
	}
	
	protected void copyChunksFromBufferIfNotGenerated(SpoutWorld world, CuboidBlockMaterialBuffer buffer, int cx, int cy, int cz) {
		final CuboidBlockMaterialBuffer chunk = new CuboidBlockMaterialBuffer(cx << Chunk.BLOCKS.BITS, cy << Chunk.BLOCKS.BITS, cz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE);
		chunk.write(buffer);
		SpoutChunk newChunk = new SpoutChunk(world, this, cx, cy, cz, chunk.getRawId(), chunk.getRawData(), null);
		cx &= CHUNKS.MASK;
		cy &= CHUNKS.MASK;
		cz &= CHUNKS.MASK;
		SpoutChunk currentChunk = setChunkIfNotGenerated(newChunk, cx, cy, cz, null, true);
		if (currentChunk != newChunk) {
			//Spout.getLogger().info("Warning: Unable to set generated chunk, new Chunk " + newChunk + " chunk in memory " + currentChunk);
		} else {
			newChunk.compressRaw();
			newChunk.setModified();
		}
	}
	
	private SpoutChunk setChunkIfNotGenerated(SpoutChunk newChunk, int x, int y, int z, ChunkDataForRegion dataForRegion, boolean generated) {
		
		synchronized (this.generateSync) {
			int cx = newChunk.getX();
			int cy = newChunk.getY();
			int cz = newChunk.getZ();
			SpoutChunk current = getChunk(cx, cy, cz, LoadOption.LOAD_ONLY);
			if (current != null) {
				return current;
			}
			// chunk has not been generated
			return setChunk(newChunk, x, y, z, dataForRegion, generated);
		}
		
	}
	
	private SpoutChunk setChunk(SpoutChunk newChunk, int x, int y, int z, ChunkDataForRegion dataForRegion, boolean generated) {
		final AtomicReference<SpoutChunk> chunkReference = chunks[x][y][z];
		while (true) {
			if (chunkReference.compareAndSet(null, newChunk)) {
				if (generated) {
					newChunk.notifyColumn();
					newChunk.queueNew();
				}
				if (Spout.getEngine().getPlatform() == Platform.CLIENT) { 
					newChunk.setNeighbourRenderDirty(true);
				}
				numberActiveChunks.incrementAndGet();
				if (dataForRegion != null) {
					for (SpoutEntity entity : dataForRegion.loadedEntities) {
						entity.setupInitialChunk(entity.getScene().getTransform(), LoadOption.NO_LOAD);
						entityManager.addEntity(entity);
					}
					dynamicBlockTree.addDynamicBlockUpdates(dataForRegion.loadedUpdates);
				}
				if (!newChunk.wasLightStableOnLoad() && newChunk.isPopulated()) {
					newChunk.initLighting();
				}
				Spout.getEventManager().callDelayedEvent(new ChunkLoadEvent(newChunk, generated));
				return newChunk;
			}

			SpoutChunk oldChunk = chunkReference.get();
			if (oldChunk != null) {
				newChunk.setUnloadedUnchecked();
				return oldChunk;
			}
		}
	}

	private void checkChunkLoaded(SpoutChunk chunk, LoadOption loadopt) {
		if (loadopt.loadIfNeeded()) {
			if (!chunk.cancelUnload()) {
				throw new IllegalStateException("Unloaded chunk returned by getChunk");
			}
		}
	}

	private void addToRenderQueue(SpoutChunkSnapshotModel model){
		SpoutChunkSnapshotModel previous;
		synchronized (renderChunkQueued) {
			previous = renderChunkQueued.put((byte) model.getX(), (byte) model.getY(), (byte) model.getZ(), model);
			if(previous != null){
				boolean removed = renderChunkQueue.remove(previous);
				model.addDirty(previous, removed);
				if (model.isUnload() && model.isFirst()) {
					if (renderChunkQueued.remove((byte) model.getX(), (byte) model.getY(), (byte) model.getZ()) != model) {
						throw new IllegalStateException("Removed model does not match put model");
					} else {
						return;
					}
				}
			}
		}

		renderChunkQueue.add(model);

		synchronized (renderChunkQueued) {
			renderChunkQueued.notify();
		}
	}

	private SpoutChunkSnapshotModel removeFromRenderQueue() throws InterruptedException {
		SpoutChunkSnapshotModel model = renderChunkQueue.pollFirst();
		if (model == null) {
			synchronized (renderChunkQueued) {
				while (model == null) {
					model = renderChunkQueue.pollFirst();
					if (model == null) {
						renderChunkQueued.wait();
					}
				}
			}
		}
		synchronized (renderChunkQueued) {
			if (renderChunkQueued.get((byte) model.getX(), (byte) model.getY(), (byte) model.getZ()) == model) {
				renderChunkQueued.remove((byte) model.getX(), (byte) model.getY(), (byte) model.getZ());
			}
		}
		return model;
	}

	/**
	 * Removes a chunk from the region and indicates if the region is empty
	 * @param c the chunk to remove
	 * @return true if the region is now empty
	 */
	public boolean removeChunk(Chunk c) {
		TickStage.checkStage(TickStage.SNAPSHOT);
		if (c.getRegion() != this) {
			return false;
		}

		AtomicReference<SpoutChunk> current = chunks[c.getX() & CHUNKS.MASK][c.getY() & CHUNKS.MASK][c.getZ() & CHUNKS.MASK];
		SpoutChunk currentChunk = current.get();
		if (currentChunk != c) {
			return false;
		}
		boolean success = current.compareAndSet(currentChunk, null);
		if (success) {
			int num = numberActiveChunks.decrementAndGet();

			for (Entity e : currentChunk.getLiveEntities()) {
				e.remove();
			}

			currentChunk.setUnloaded();
			if (renderQueueEnabled && currentChunk.isInViewDistance()) {
				addToRenderQueue(new SpoutChunkSnapshotModel(getWorld(),currentChunk.getX(), currentChunk.getY(), currentChunk.getZ(), true, System.currentTimeMillis()));
			}

			int cx = c.getX() & CHUNKS.MASK;
			int cy = c.getY() & CHUNKS.MASK;
			int cz = c.getZ() & CHUNKS.MASK;

			Iterator<Map.Entry<SpoutPlayer, TByteTripleHashSet>> itr = observers.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<SpoutPlayer, TByteTripleHashSet> entry = itr.next();
				TByteTripleHashSet chunkSet = entry.getValue();
				if (chunkSet.remove(cx, cy, cz)) {
					if (chunkSet.isEmpty()) {
						itr.remove();
					}
				}
			}

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
		return chunks[x & CHUNKS.MASK][y & CHUNKS.MASK][z & CHUNKS.MASK].get() != null;
	}

	public boolean isEmpty() {
		TickStage.checkStage(TickStage.SNAPSHOT);
		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
					if (chunks[dx][dy][dz].get() != null) {
						return false;
					}
				}
			}
		}
		return true;
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

	public void markForSaveUnload() {
		saveMarkedElement.add();
	}

	public void copySnapshotRun() {
		entityManager.copyAllSnapshots();

		snapshotManager.copyAllSnapshots();
		
		dynamicBlockTree.setRegionThread(Thread.currentThread());

		boolean empty = false;
		Cube cube;
		while ((cube = saveMarkedQueue.poll()) != null) {
			if (cube == this) {
				for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
					for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
						for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
							SpoutChunk c = getChunk(dx, dy, dz, LoadOption.NO_LOAD);
							if (processChunkSaveUnload(c)) {
								empty = true;
							}
						}
					}
				}
				// No point in checking any others, since all processed
				saveMarkedQueue.clear();
				break;
			}

			empty |= processChunkSaveUnload((SpoutChunk) cube);
		}

		SpoutChunk c;
		while ((c = chunkObserversDirtyQueue.poll()) != null) {
			int cx = c.getX() & CHUNKS.MASK;
			int cy = c.getY() & CHUNKS.MASK;
			int cz = c.getZ() & CHUNKS.MASK;
			c = chunks[cx][cy][cz].get();
			Set<SpoutEntity> chunkObservers = c == null ? Collections.<SpoutEntity>emptySet() : c.getObservers();

			Iterator<Map.Entry<SpoutPlayer, TByteTripleHashSet>> itr = observers.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<SpoutPlayer, TByteTripleHashSet> entry = itr.next();
				TByteTripleHashSet chunkSet = entry.getValue();
				SpoutPlayer sp = entry.getKey();

				if (chunkObservers.contains(sp)) {
					chunkSet.add(cx, cy, cz);
				} else {
					if (chunkSet.remove(cx, cy, cz)) {
						if (chunkSet.isEmpty()) {
							itr.remove();
						}
					}
				}
			}
			for (SpoutEntity e : chunkObservers) {
				if (!(e instanceof Player)) {
					continue;
				}
				SpoutPlayer p = (SpoutPlayer) e;
				if (!observers.containsKey(p)) {
					TByteTripleHashSet chunkSet = new TByteTripleHashSet();
					chunkSet.add(cx, cy, cz);
					observers.put(p, chunkSet);
				}
			}
		}

		// Updates on nulled chunks
		snapshotManager.copyAllSnapshots();

		if (empty) {
			source.removeRegion(this);
		}
	}

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

	private void updateAutosave() {
		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
					SpoutChunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null && chunk.isLoaded()) {
						if (chunk.getAutosaveTicks() > 1) {
							chunk.setAutosaveTicks(chunk.getAutosaveTicks() - 1);
						} else if (chunk.getAutosaveTicks() == 1) {
							chunk.setAutosaveTicks(0);
							chunk.save();
						}
					}
				}
			}
		}
	}

	private void updateBlockComponents(float dt) {
		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
					SpoutChunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null && chunk.isLoaded()) {
						chunk.tickBlockComponents(dt);
					}
				}
			}
		}
	}

	private void updateEntities(float dt) {
		for (SpoutEntity ent : entityManager.getAll()) {
			try {
				ent.tick(dt);
			} catch (Exception e) {
				Spout.getEngine().getLogger().severe("Unhandled exception during tick for " + ent.toString());
				e.printStackTrace();
			}
		}
	}

	private void updateLighting() {
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
					SpoutChunk chunk = this.getChunk(x, y, z, LoadOption.NO_LOAD);
					if (chunk == null || !chunk.isLoaded()) {
						iter.remove();
						continue;
					}
					if (((ServerLightStore)chunk.getLightStore()).lightingCounter.incrementAndGet() > LIGHT_SEND_TICK_DELAY) {
						((ServerLightStore)chunk.getLightStore()).lightingCounter.set(-1);
						if (SpoutConfiguration.LIVE_LIGHTING.getBoolean()) {
							chunk.setLightDirty(true);
						}
						iter.remove();
					}
				}
			}
		}
	}

	private void updatePopulation() {
		for (int i = 0; i < POPULATE_PER_TICK && !scheduler.isServerOverloaded(); i++) {
			SpoutChunk toPopulate = populationPriorityQueue.poll();
			if (toPopulate == null) {
				toPopulate = populationQueue.poll();
				if (toPopulate == null) {
					break;
				}
			}
			if (toPopulate.isLoaded()) {
				if (toPopulate.populate()) {
					if (scheduler.isServerOverloaded()) {
						break;
					}
					continue;
				}
			}
			i--;
		}
	}

	private void unloadChunks() {
		SpoutChunk toUnload = unloadQueue.poll();
		int unloadAmt = SpoutConfiguration.UNLOAD_CHUNKS_PER_TICK.getInt();
		while (toUnload != null) {
			unloadAmt--;
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
			if (unloadAmt > 0) {
				toUnload = unloadQueue.poll();
			} else {
				break;
			}
		}
	}

	private static final Object logLock = new Object();

	/**
	 * Updates CollisionObjects in this region and adds/removes them from the simulation. 
	 * Steps simulation forward and finally alerts the API in components.
	 * @param dt
	 */
	private void updateDynamics(float dt) {
		try {
			lock.writeLock().lock();
			//Simulate physics
			simulation.stepSimulation(dt, 2);
			final Dispatcher dispatcher = simulation.getDispatcher();
			int manifolds = dispatcher.getNumManifolds();
			for (int i = 0; i < manifolds; i++) {
				PersistentManifold contact = dispatcher.getManifoldByIndexInternal(i);
				Object colliderRawA = contact.getBody0();
				Object colliderRawB = contact.getBody1();
				if (!(colliderRawA instanceof CollisionObject) || !(colliderRawB instanceof CollisionObject)) {
					continue;
				}
				Object holderA = ((CollisionObject) colliderRawA).getUserPointer();
				Object holderB = ((CollisionObject) colliderRawB).getUserPointer();
				int contacts = contact.getNumContacts();

				//Loop through the contact points
				for (int j = 0; j < contacts; j++) {
					//Grab a contact point
					final ManifoldPoint bulletPoint = contact.getContactPoint(j);
					//Contact point is no longer valid as negative values = still within contact so lets not resolve that to the API
					if (bulletPoint.getDistance() > 0f) {
						continue;
					}
					//3D position where colliderA contacted colliderB
					Point contactPointA = new Point(VectorMath.toVector3(bulletPoint.getPositionWorldOnA(new Vector3f())), getWorld());
					//3D position where colliderB contacted colliderA
					Point contactPointB = new Point(VectorMath.toVector3(bulletPoint.getPositionWorldOnB(new Vector3f())), getWorld());

					//Resolve Entity -> Entity Collisions
					if (holderA instanceof Entity) {
						//Entity was removed before the contact point could be resolved, break
						if (((Entity) holderA).isRemoved()) {
							break;
						}
						//HolderA: Entity
						//HolderB: Entity
						if (holderB instanceof Entity) {
							//Entity was removed before the contact point could be resolved, break
							if (((Entity) holderB).isRemoved()) {
								break;
							}
							//Call onCollide for colliderA's EntityComponents
							for (Component component : ((Entity) holderA).values()) {
								if (component instanceof EntityComponent) {
									((EntityComponent) component).onCollided(contactPointA, contactPointB, (Entity) holderB);
								}
							}
							//Call onCollide for colliderB's EntityComponents
							for (Component component : ((Entity) holderB).values()) {
								if (component instanceof EntityComponent) {
									((EntityComponent) component).onCollided(contactPointB, contactPointA, (Entity) holderA);
								}
							}
						//HolderA: Entity
						//HolderB: Block
						} else if (holderB instanceof Block) {
							//Call onCollide for colliderA's EntityComponents
							for (Component component : ((Entity) holderA).values()) {
								if (component instanceof EntityComponent) {
									((EntityComponent) component).onCollided(contactPointA, contactPointB, (Block) holderB);
								}
							}
							((Block) holderB).getMaterial().onCollided(contactPointB, contactPointA, (Entity) holderA);
						}
					//HolderA: Block
					//HolderB: Entity
					} else if (holderA instanceof Block) {
						if (holderB instanceof Entity) {
							//Entity was removed before the contact point could be resolved, break
							if (((Entity) holderB).isRemoved()) {
								break;
							}
							((Block) holderA).getMaterial().onCollided(contactPointA, contactPointB, (Entity) holderB);
							//Call onCollide for colliderB's EntityComponents
							for (Component component : ((Entity) holderB).values()) {
								if (component instanceof EntityComponent) {
									((EntityComponent) component).onCollided(contactPointB, contactPointA, (Block) holderA);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			synchronized(logLock) {
				Spout.getLogger().log(Level.SEVERE, "Exception while executing physics in region " + getBase().toBlockString(), e);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void startTickRun(int stage, long delta) {
		final float dt = delta / 1000f;
		switch (stage) {
			case 0: {
				taskManager.heartbeat(delta);
				updateAutosave();
				updateBlockComponents(dt);
				updateEntities(dt);
				updateLighting();
				updatePopulation();
				unloadChunks();
				break;
			}
			case 1: {
				updateDynamics(dt);
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
	public int getMaxStage() {
		return 2;
	}

	private int reapX = 0, reapY = 0, reapZ = 0;

	public void finalizeRun() {
		long worldAge = getWorld().getAge();
		for (int reap = 0; reap < SpoutConfiguration.REAP_CHUNKS_PER_TICK.getInt(); reap++) {
			if (++reapX >= CHUNKS.SIZE) {
				reapX = 0;
				if (++reapY >= CHUNKS.SIZE) {
					reapY = 0;
					if (++reapZ >= CHUNKS.SIZE) {
						reapZ = 0;
					}
				}
			}
			SpoutChunk chunk = chunks[reapX][reapY][reapZ].get();
			if (chunk != null) {
				chunk.compressIfRequired();
				boolean doUnload;
				if (doUnload = chunk.isReapable(worldAge)) {
					if (ChunkUnloadEvent.getHandlerList().getRegisteredListeners().length > 0) {
						ChunkUnloadEvent event = Spout.getEngine().getEventManager().callEvent(new ChunkUnloadEvent(chunk));
						if (event.isCancelled()) {
							doUnload = false;
						}
					}
				}
				if (doUnload) {
					chunk.unload(true);
				} else if (!chunk.isPopulated()) {
					chunk.queueForPopulation(false);
				}
			}
		}
		//Note: This must occur after any chunks are reaped, because reaping chunks may kill entities, which need to be finalized
		entityManager.finalizeRun();
	}

	private void syncChunkToPlayer(SpoutChunk chunk, Player player) {
		if (player.isOnline()) {
			NetworkSynchronizer synchronizer = player.getNetworkSynchronizer();
			if (!chunk.isDirtyOverflow() && !chunk.isLightDirty()) {
				for (int i = 0; true; i++) {
					Vector3 block = chunk.getDirtyBlock(i);
					if (block == null) {
						break;
					}

					try {
						synchronizer.updateBlock(chunk, (int) block.getX(), (int) block.getY(), (int) block.getZ());
					} catch (Exception e) {
						Spout.getEngine().getLogger().log(Level.SEVERE, "Exception thrown by plugin when attempting to send a block update to " + player.getName());
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
		if (chunk.isDirtyOverflow()) {    /* If overflow, notify for whole chunk */
			evt = new ChunkUpdatedEvent(chunk, null);
		} else {
			ArrayList<Vector3> lst = new ArrayList<Vector3>();
			boolean done = false;
			for (int i = 0; !done; i++) {
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
	}

	private WorldRenderer renderer = null;
	private static final int RENDER_QUEUE_LIMIT = 500;

	private WorldRenderer getRenderer(){
		if(renderer == null && ((SpoutClient) Spout.getEngine()).getRenderer() != null)
			renderer = ((SpoutClient) Spout.getEngine()).getRenderer().getWorldRenderer();
		return renderer;
	}

	public void preSnapshotRun() {
		entityManager.preSnapshotRun();

		Point playerPosition = null;
		int renderLimit = Spout.getPlatform() == Platform.CLIENT && getRenderer() != null ?
				RENDER_QUEUE_LIMIT - (getRenderer().getBatchWaiting() + renderChunkQueue.size()) :
					0;

		if (Spout.getEngine().getPlatform() == Platform.CLIENT) {
			SpoutWorld world = this.getWorld();

			boolean worldRenderQueueEnabled = world.isRenderQueueEnabled();
			boolean firstRenderQueueTick = (!renderQueueEnabled) && worldRenderQueueEnabled;
			boolean unloadRenderQueue = !worldRenderQueueEnabled && renderQueueEnabled;

			SpoutPlayer player = ((SpoutClient) Spout.getEngine()).getActivePlayer();
			if (player == null) {
				playerPosition = null;
			} else {
				playerPosition = player.getScene().getPosition();
			}

			if (firstRenderQueueTick && player != null) {
				for( SpoutChunk c : player.getObservingChunks()){
					c.setRenderDirty(true);
				}
				renderQueueEnabled = worldRenderQueueEnabled;
			}

			if(unloadRenderQueue){
				for(SpoutChunk c : rended.toArray(new SpoutChunk[rended.size()])){
					addUpdateToRenderQueue(playerPosition, c, false, false, false);

				}
			}
		}
		
		SpoutChunk spoutChunk;

		List<SpoutChunk> renderLater = new LinkedList<SpoutChunk>();
		List<SpoutChunk> couldNotSend = new LinkedList<SpoutChunk>();

		while ((spoutChunk = dirtyChunkQueue.poll()) != null) {

			if (renderQueueEnabled /*&& spoutChunk.isRenderDirty()*/) {
				if(spoutChunk.isInViewDistance() || (spoutChunk.isRendered() && spoutChunk.leftViewDistance())){
					if(renderLimit > 0 ){
						addUpdateToRenderQueue(playerPosition, spoutChunk, spoutChunk.isBlockDirty(), spoutChunk.isLightDirty(), false);
						renderLimit--;
					}else{
						renderLater.add(spoutChunk);
					}
				}else{
					spoutChunk.setRenderDirty(false);
					spoutChunk.viewDistanceCopy();
				}
			}

			if (spoutChunk.isPopulated() && spoutChunk.isDirty()) {
				if (!spoutChunk.canSend()) {
					couldNotSend.add(spoutChunk);
				} else {
					for (Player entity : spoutChunk.getObservingPlayers()) {
						syncChunkToPlayer(spoutChunk, entity);
					}
					processChunkUpdatedEvent(spoutChunk);

					spoutChunk.resetDirtyArrays();
					spoutChunk.setLightDirty(false);
				}
			}
		}
		
		for (SpoutChunk c : couldNotSend) {
			c.queueDirty();
		}

		for(SpoutChunk c : renderLater){
			c.setRenderDirty(true);
		}

		SpoutChunkSnapshotFuture snapshotFuture;
		while ((snapshotFuture = snapshotQueue.poll()) != null) {
			snapshotFuture.run();
		}

		renderSnapshotCacheBoth.clear();
		renderSnapshotCacheLight.clear();
		renderSnapshotCacheBlock.clear();

		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
				for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
					SpoutChunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						chunk.updateExpiredObservers();
					}
				}
			}
		}

		entityManager.syncEntities();
	}


	/*public ConcurrentSkipListSet<SpoutChunkSnapshotModel> getRenderChunkQueue() {
		return this.renderChunkQueue;
	}*/

	private TInt21TripleObjectHashMap<SpoutChunkSnapshot> renderSnapshotCacheBoth = new TInt21TripleObjectHashMap<SpoutChunkSnapshot>();
	private TInt21TripleObjectHashMap<SpoutChunkSnapshot> renderSnapshotCacheLight = new TInt21TripleObjectHashMap<SpoutChunkSnapshot>();
	private TInt21TripleObjectHashMap<SpoutChunkSnapshot> renderSnapshotCacheBlock = new TInt21TripleObjectHashMap<SpoutChunkSnapshot>();

	private void addUpdateToRenderQueue(Point p, SpoutChunk c, boolean block, boolean light, boolean force) {
		int bx = c.getX() - 1;
		int by = c.getY() - 1;
		int bz = c.getZ() - 1;

		if (c.isInViewDistance()) {
			int distance;
			if (p == null) {
				distance = 0;
			} else {
				distance = (int) p.getManhattanDistance(c.getBase());
			}
			int ox = bx - getChunkX();
			int oy = by - getChunkY();
			int oz = bz - getChunkZ();
			ChunkSnapshot[][][] chunks = new ChunkSnapshot[3][3][3];
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					for (int z = 0; z < 3; z++) {
						//if (x == 1 || y == 1 || z == 1) { //Need for light
						ChunkSnapshot snapshot = getRenderSnapshot(c, ox + x, oy + y, oz + z);
						if (snapshot == null) {
							//System.out.println("skip");
							return;
						}
						chunks[x][y][z] = snapshot;
						//}
					}
				}
			}
			boolean first = c.enteredViewDistance();
			final HashSet<RenderMaterial> updatedRenderMaterials;

			if (first || c.isDirtyOverflow() || force || c.isLightDirty()) {
				updatedRenderMaterials = null;
			} else {
				updatedRenderMaterials = new HashSet<RenderMaterial>();
				int dirtyBlocks = c.getDirtyBlocks();
				for (int i = 0; i < dirtyBlocks; i++) {
					addMaterialToSet(updatedRenderMaterials, c.getDirtyOldState(i));
					addMaterialToSet(updatedRenderMaterials, c.getDirtyNewState(i));
				}
				int size = BLOCKS.SIZE;
				for (int i = 0; i < dirtyBlocks; i++) {
					Vector3 blockPos = c.getDirtyBlock(i);
					BlockMaterial material = c.getBlockMaterial(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ());
					ByteBitSet occlusion = material.getOcclusion(material.getData());
					for(BlockFace face : BlockFace.values()){ 
						if (face.equals(BlockFace.THIS)) {
							continue;
						}
						if (occlusion.get(face)) {
							continue;
						}
						Vector3 neighborPos = blockPos.add(face.getOffset());
						int nx = neighborPos.getFloorX();
						int ny = neighborPos.getFloorX();
						int nz = neighborPos.getFloorX();
						if (nx >= 0 && ny >= 0 && nz >= 0 && nx < size && ny < size && nz < size) {
							int state = c.getBlockFullState(nx, ny, nz);
							addMaterialToSet(updatedRenderMaterials, state);
							//addSubMeshToSet(updatedSubMeshes, neighborPos);
						}
					}
				}
			}
			c.setRendered(true);
			addRendedChunk(c);
			c.setRenderDirty(false);
			addToRenderQueue(new SpoutChunkSnapshotModel(getWorld(),bx + 1, by + 1, bz + 1, chunks, distance, updatedRenderMaterials, first, System.currentTimeMillis()));
		} else {
			if (c.leftViewDistance()) {
				c.setRendered(false);
				removeRendedChunk(c);
				c.setRenderDirty(false);
				addToRenderQueue(new SpoutChunkSnapshotModel(getWorld(),bx + 1, by + 1, bz + 1, true, System.currentTimeMillis()));
			}
		}
		c.viewDistanceCopy();
	}

	private Set<SpoutChunk> rended = new HashSet<SpoutChunk>();

	public void addRendedChunk(SpoutChunk chunk){
		rended.add(chunk);
	}

	public void removeRendedChunk(SpoutChunk chunk){
		rended.remove(chunk);
	}

	private static void addMaterialToSet(Set<RenderMaterial> set, int blockState) {
		BlockMaterial material = (BlockMaterial) MaterialRegistry.get(blockState);
		set.add(material.getModel().getRenderMaterial());
	}

	/*private static void addSubMeshToSet(Set<Vector3> set, Vector3 dirtyBlock) {
		set.add(ChunkMesh.getChunkSubMesh(dirtyBlock.getFloorX(), dirtyBlock.getFloorY(), dirtyBlock.getFloorZ()));
	}*/

	private ChunkSnapshot getRenderSnapshot(SpoutChunk cRef, int cx, int cy, int cz) {
		SpoutChunkSnapshot snapshot = renderSnapshotCacheBoth.get(cx, cy, cz);
		if (snapshot != null) {
			return snapshot;
		}

		SpoutChunk c = getLocalChunk(cx, cy, cz, LoadOption.NO_LOAD);

		if (c == null) {
			//Spout.getLogger().info("Getting " + cx + ", " + cy + ", " + cz + ": base = " + cRef.getBase().toBlockString() + " region base " + getBase().toBlockString());
			return null;
		} else {
			snapshot = c.getSnapshot(SnapshotType.BOTH, EntityType.NO_ENTITIES, ExtraData.NO_EXTRA_DATA);
			if (snapshot != null) {
				renderSnapshotCacheBoth.put(cx, cy, cz, snapshot);
			}
			return snapshot;
		}
	}

	public void runPhysics(int sequence) {
		
		dynamicBlockTree.setRegionThread(Thread.currentThread());

		if (sequence == -1) {
			runLocalPhysics();
		} else if (sequence == this.updateSequence) {
			runGlobalPhysics();
		}
	}

	public void runLocalPhysics() {
		boolean updated = true;

		while (updated) {
			updated = false;
			SpoutChunk c;
			while ((c = this.localPhysicsChunkQueue.poll()) != null) {
				updated |= c.runLocalPhysics();
			}
		}
	}

	public void runGlobalPhysics() {
		SpoutChunk c;
		while ((c = this.globalPhysicsChunkQueue.poll()) != null) {
			c.runGlobalPhysics();
		}
	}

	public void runDynamicUpdates(long time, int sequence) {
		scheduler.addUpdates(dynamicBlockTree.getLastUpdates());
		dynamicBlockTree.resetLastUpdates();
		dynamicBlockTree.setRegionThread(Thread.currentThread());

		if (sequence == -1) {
			runLocalDynamicUpdates(time);
		} else if (sequence == this.updateSequence) {
			runGlobalDynamicUpdates();
		}
	}

	public long getFirstDynamicUpdateTime() {
		return dynamicBlockTree.getFirstDynamicUpdateTime();
	}

	public void runLocalDynamicUpdates(long time) {
		long currentTime = getWorld().getAge();
		if (time > currentTime) {
			time = currentTime;
		}
		dynamicBlockTree.commitAsyncPending(currentTime);
		multiRegionUpdates = dynamicBlockTree.updateDynamicBlocks(currentTime, time);
	}

	public void runGlobalDynamicUpdates() {
		long currentTime = getWorld().getAge();
		if (multiRegionUpdates != null) {
			boolean updated = false;
			for (DynamicBlockUpdate update : multiRegionUpdates) {
				updated |= dynamicBlockTree.updateDynamicBlock(currentTime, update, true).isUpdated();
			}
			if (updated) {
				scheduler.addUpdates(1);
			}
		}
	}

	private int lightingUpdates = 0;

	private ImmutableHeightMapBuffer heightMapBuffer = null;
	private ImmutableCuboidBlockMaterialBuffer blockMaterialBuffer = null;
	private ChunkCuboidLightBufferWrapper<?>[] lightBuffers = null;
	
	@SuppressWarnings("rawtypes")
	public void runLighting(int sequence) {
		if (sequence != this.updateSequence) {
			return;
		}
		
		LightingManager<?>[] managers = getWorld().getLightingManagers();
		
		if (blockMaterialBuffer == null) {
			blockMaterialBuffer = new LocalRegionChunkCuboidBlockMaterialBufferWrapper(this, LoadOption.LOAD_ONLY, BlockMaterial.UNGENERATED);
		}
		
		if (lightBuffers == null || lightBuffers.length != managers.length) {
			lightBuffers = new ChunkCuboidLightBufferWrapper[managers.length];
		}
		
		if (heightMapBuffer == null) {
			heightMapBuffer = new LocalRegionChunkHeightMapBufferWrapper(this, LoadOption.LOAD_ONLY);
		}
		
		for (int i = 0; i < lightBuffers.length; i++) {
			short managerId = managers[i].getId();
			if (lightBuffers[i] == null || lightBuffers[i].getManagerId() != managerId) {
				lightBuffers[i] = new LocalRegionChunkCuboidLightBufferWrapper(this, managerId, LoadOption.LOAD_ONLY);
			}
		}
		
		List<SpoutChunk> newChunksList = new LinkedList<SpoutChunk>();
		
		int newChunksCount = 0;
		SpoutChunk newChunk;
		while ((newChunk = newChunkQueue.poll()) != null) {
			newChunksList.add(newChunk);
			newChunksCount++;
		}
		
		int cuboids = 0;
		int blocks = 0;
		int columns = 0;
		
		for (SpoutColumn col : this.dirtyColumnQueue) {
			columns += col.getDirtyColumns();
		}
		
		if (columns > 0) {
			int[] colX = new int[columns];
			int[] colZ = new int[columns];
			int[] oldH = new int[columns];
			int[] newH = new int[columns];

			int pos = 0;

			int minY = getBlockY();
			int maxY = minY + BLOCKS.SIZE;
			
			for (SpoutColumn col : this.dirtyColumnQueue) {
				pos = col.fillDirty(pos, colX, newH, oldH, colZ, minY, maxY);
			}

			if (pos > 0) {
				resolveColumns(colX, colZ, oldH, newH, managers, pos);
			}
		}
		for (SpoutChunk c : this.dirtyChunkQueue) {
			if (c.isDirtyOverflow()) {
				cuboids++;
			} else {
				blocks += c.getDirtyBlocks();
			}
		}
	
		SpoutChunk[] newChunksArray = new SpoutChunk[newChunksCount];
		SpoutChunk[] dirtyChunks = new SpoutChunk[cuboids];
		int[] x = new int[blocks];
		int[] y = new int[blocks];
		int[] z = new int[blocks];
		
		blocks = 0;
		cuboids = 0;
		
		for (SpoutChunk c : this.dirtyChunkQueue) {
			if (c.isDirtyOverflow()) {
				dirtyChunks[cuboids++] = c;
			} else {
				int dirtyBlocks = c.getDirtyBlocks();
				for (int i = 0; i < dirtyBlocks; i++) {
					Vector3 v = c.getDirtyBlock(i);
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
			resolveBlocks(x, y, z, managers);
		}

		scheduler.addUpdates(lightingUpdates);
		lightingUpdates = 0;
		
		if (blockMaterialBuffer != null) {
			((LocalRegionChunkCuboidBlockMaterialBufferWrapper) blockMaterialBuffer).clear();
		}
		
		if (lightBuffers != null) {
			for (i = 0; i < lightBuffers.length; i++) {
				if (lightBuffers[i] != null) {
					lightBuffers[i].clear();
				}
			}
		}
		
		if (heightMapBuffer != null) {
			((LocalRegionChunkHeightMapBufferWrapper) heightMapBuffer).clear();
		}
	}
	
	private void resolveCuboids(SpoutChunk[] chunks, LightingManager<?>[] managers, boolean init) {
		int cuboids = chunks.length;
		int[] bx = new int[cuboids];
		int[] by = new int[cuboids];
		int[] bz = new int[cuboids];
		if (init) {
			for (int i = 0; i < cuboids; i++) {
				SpoutChunk chunk = chunks[i];
				bx[i] = chunk.getBlockX();
				by[i] = chunk.getBlockY();
				bz[i] = chunk.getBlockZ();
			}
			for (int i = 0; i < managers.length; i++) {
				managers[i].initChunksUnchecked(lightBuffers[i], blockMaterialBuffer, heightMapBuffer, bx, by, bz, cuboids);
			}
		} else {
			int[] tx = new int[cuboids];
			int[] ty = new int[cuboids];
			int[] tz = new int[cuboids];
			for (int i = 0; i < cuboids; i++) {
				SpoutChunk chunk = chunks[i];
				IntVector3 min = chunk.getMinDirty();
				IntVector3 max = chunk.getMaxDirty();
				bx[i] = chunk.getBlockX() + min.getX();
				by[i] = chunk.getBlockY() + min.getY();
				bz[i] = chunk.getBlockZ() + min.getZ();
				tx[i] = chunk.getBlockX() + max.getX() + 1;
				ty[i] = chunk.getBlockY() + max.getY() + 1;
				tz[i] = chunk.getBlockZ() + max.getZ() + 1;
			}
			for (int i = 0; i < managers.length; i++) {
				managers[i].resolveChunksUnchecked(lightBuffers[i], blockMaterialBuffer, heightMapBuffer, bx, by, bz, tx, ty, tz, cuboids);
			}
		}
	}
	
	private void resolveBlocks(int[] x, int[] y, int[] z, LightingManager<?>[] managers) {
		for (int i = 0; i < managers.length; i++) {
			managers[i].resolveUnchecked(lightBuffers[i], blockMaterialBuffer, heightMapBuffer, x, y, z, x.length);
		}
	}

	private void resolveColumns(int[] hx, int[] hz, int[] oldHy, int[] newHy, LightingManager<?>[] managers, int changedColumns) {
		for (int i = 0; i < managers.length; i++) {
			managers[i].resolveColumnsUnchecked(lightBuffers[i], blockMaterialBuffer, heightMapBuffer, hx, hz, oldHy, newHy, changedColumns);
		}
	}
	
	public int getSequence() {
		return updateSequence;
	}

	@Override
	public List<Entity> getAll() {
		return new ArrayList<Entity>(entityManager.getAll());
	}

	@Override
	public SpoutEntity getEntity(int id) {
		return entityManager.getEntity(id);
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

	public boolean inputStreamExists(int x, int y, int z) {
		return chunkStore.inputStreamExists(getChunkKey(x, y, z));
	}

	public boolean attemptClose() {
		return chunkStore.attemptClose();
	}

	/**
	 * Gets the DataInputStream corresponding to a given Chunk.<br>
	 * <br>
	 * The stream is based on a snapshot of the array.
	 * @param x the chunk
	 * @return the DataInputStream
	 */
	public InputStream getChunkInputStream(int x, int y, int z) {
		return chunkStore.getBlockInputStream(getChunkKey(x, y, z));
	}

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

	@Override
	public boolean hasChunkAtBlock(int x, int y, int z) {
		return this.getWorld().hasChunkAtBlock(x, y, z);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, cause);
	}

	@Override
	public boolean addBlockData(int x, int y, int z, short data, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).addBlockData(x, y, z, data, cause);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, data, cause);
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockLight(x, y, z, light, cause);
	}

	@Override
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockSkyLight(x, y, z, light, cause);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, boolean set, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, set, cause);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, cause);
	}

	@Override
	public short clearBlockDataBits(int x, int y, int z, int bits, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).clearBlockDataBits(x, y, z, bits, cause);
	}

	@Override
	public int getBlockDataField(int x, int y, int z, int bits) {
		return this.getChunkFromBlock(x, y, z).getBlockDataField(x, y, z, bits);
	}

	@Override
	public boolean isBlockDataBitSet(int x, int y, int z, int bits) {
		return this.getChunkFromBlock(x, y, z).isBlockDataBitSet(x, y, z, bits);
	}

	@Override
	public int setBlockDataField(int x, int y, int z, int bits, int value, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockDataField(x, y, z, bits, value, cause);
	}

	@Override
	public int addBlockDataField(int x, int y, int z, int bits, int value, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).addBlockDataField(x, y, z, bits, value, cause);
	}

	@Override
	public BlockComponent getBlockComponent(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z).getBlockComponent(x, y, z);
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range) {
		queueBlockPhysics(x, y, z, range, null);
	}

	public void queueBlockPhysics(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial) {
		SpoutChunk c = getChunkFromBlock(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			c.queueBlockPhysics(x, y, z, range, oldMaterial);
		}
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z) {
		updateBlockPhysics(x, y, z, null);
	}

	public void updateBlockPhysics(int x, int y, int z, BlockMaterial oldMaterial) {
		SpoutChunk c = getChunkFromBlock(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			c.updateBlockPhysics(x, y, z, oldMaterial);
		}
	}

	public void reportChunkLightDirty(int x, int y, int z) {
		synchronized (lightDirtyChunks) {
			lightDirtyChunks.add(x & CHUNKS.MASK, y & CHUNKS.MASK, z & CHUNKS.MASK);
		}
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		return this.getWorld().getBiome(x, y, z);
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return this.getWorld().getBlock(x, y, z);
	}

	@Override
	public Block getBlock(float x, float y, float z) {
		return this.getWorld().getBlock(x, y, z);
	}

	@Override
	public Block getBlock(Vector3 position) {
		return this.getWorld().getBlock(position);
	}

	@Override
	public int getBlockFullState(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z).getBlockFullState(x, y, z);
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
	public byte getBlockSkyLightRaw(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockSkyLightRaw(x, y, z);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data, cause);
	}

	@Override
	public List<Player> getPlayers() {
		return entityManager.getPlayers();
	}

	/**
	 * Test if region file exists
	 * @param world world
	 * @param x region x coordinate
	 * @param y region y coordinate
	 * @param z region z coordinate
	 * @return true if exists, false if doesn't exist
	 */
	public static boolean regionFileExists(World world, int x, int y, int z) {
		File worldDirectory = world.getDirectory();
		File regionDirectory = new File(worldDirectory, "region");
		File regionFile = new File(regionDirectory, "reg" + x + "_" + y + "_" + z + ".spr");
		return regionFile.exists();
	}

	@Override
	public TaskManager getTaskManager() {
		return taskManager;
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		setChunkModified(x, y, z);
		dynamicBlockTree.resetBlockUpdates(x, y, z);
	}
	
	@Override
	public void resetDynamicBlocks(Chunk c) {
		((SpoutChunk) c).setModified();
		dynamicBlockTree.resetBlockUpdates(c);
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		setChunkModified(x, y, z);
		dynamicBlockTree.syncResetBlockUpdates(x, y, z);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, boolean exclusive) {
		setChunkModified(x, y, z);
		return dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate, data, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, boolean exclusive) {
		setChunkModified(x, y, z);
		return dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, boolean exclusive) {
		setChunkModified(x, y, z);
		return dynamicBlockTree.queueBlockUpdates(x, y, z, exclusive);
	}
	
	public void setChunkModified(int x, int y, int z) {
		SpoutChunk c = getChunk(x, y, z, LoadOption.NO_LOAD);
		if (c != null) {
			c.setModified();
		}
	}

	public void setGravity(Vector3 gravity) {
		synchronized(simulation) {
			simulation.setGravity(VectorMath.toVector3f(gravity));
		}
	}

	public Vector3 getGravity() {
		synchronized(simulation) {
			Vector3f vector = new Vector3f();
			vector = simulation.getGravity(vector);
			return VectorMath.toVector3(vector);
		}
	}

	public List<DynamicBlockUpdate> getDynamicBlockUpdates(Chunk c) {
		Set<DynamicBlockUpdate> updates = dynamicBlockTree.getDynamicBlockUpdates(c);
		int size = updates == null ? 0 : updates.size();
		List<DynamicBlockUpdate> list = new ArrayList<DynamicBlockUpdate>(size);
		if (updates != null) {
			list.addAll(updates);
		}
		return list;
	}

	public boolean removeDynamicBlockUpdates(Chunk c) {
		boolean removed = dynamicBlockTree.removeDynamicBlockUpdates(c);
		return removed;
	}

	public void addSnapshotFuture(SpoutChunkSnapshotFuture future) {
		snapshotQueue.add(future);
	}

	public void unlinkNeighbours() {
		TickStage.checkStage(TickStage.SNAPSHOT);
		final int rx = getX();
		final int ry = getY();
		final int rz = getZ();
		final SpoutWorld world = getWorld();
		for (int dx = 0; dx < 3; dx++) {
			for (int dy = 0; dy < 3; dy++) {
				for (int dz = 0; dz < 3; dz++) {
					SpoutRegion region = world.getRegion(rx + dx - 1, ry + dy - 1, rz + dz - 1, LoadOption.NO_LOAD);
					if (region != null) {
						region.unlinkNeighbour(this);
					}
				}
			}
		}
	}

	private void unlinkNeighbour(SpoutRegion r) {
		for (int dx = 0; dx < 3; dx++) {
			for (int dy = 0; dy < 3; dy++) {
				for (int dz = 0; dz < 3; dz++) {
					neighbours[dx][dy][dz].compareAndSet(r, null);
				}
			}
		}
	}

	@Override
	public SpoutRegion getLocalRegion(BlockFace face, LoadOption loadopt) {
		Vector3 offset = face.getOffset();
		return getLocalRegion(offset.getFloorX() + 1, offset.getFloorY() + 1, offset.getFloorZ() + 1, loadopt);
	}

	@Override
	public SpoutRegion getLocalRegion(int dx, int dy, int dz, LoadOption loadopt) {
		if (loadopt != LoadOption.NO_LOAD) {
			TickStage.checkStage(~TickStage.SNAPSHOT);
		}
		AtomicReference<SpoutRegion> ref = neighbours[dx][dy][dz];
		SpoutRegion region = ref.get();
		if (region == null) {
			region = getWorld().getRegion(getX() + dx - 1, getY() + dy - 1, getZ() + dz - 1, loadopt);
			ref.compareAndSet(null, region);
		}
		return region;
	}

	@Override
	public SpoutChunk getLocalChunk(Chunk c, BlockFace face, LoadOption loadopt) {
		Vector3 offset = face.getOffset();
		return getLocalChunk(c, offset.getFloorX(), offset.getFloorY(), offset.getFloorZ(), loadopt);
	}

	@Override
	public SpoutChunk getLocalChunk(Chunk c, int ox, int oy, int oz, LoadOption loadopt) {
		return getLocalChunk(c.getX(), c.getY(), c.getZ(), ox, oy, oz, loadopt);
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
		int dx = 1 + (x >> CHUNKS.BITS);
		int dy = 1 + (y >> CHUNKS.BITS);
		int dz = 1 + (z >> CHUNKS.BITS);
		SpoutRegion region = getLocalRegion(dx, dy, dz, loadopt);
		if (region == null) {
			return null;
		}
		return region.getChunk(x, y, z, loadopt);
	}

	public void addChunk(int x, int y, int z, short[] blockIds, short[] blockData, byte[] blockLight, byte[] skyLight, BiomeManager biomes) {
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;
		SpoutChunk chunk = chunks[x >> Region.CHUNKS.BITS][y >> Region.CHUNKS.BITS][z >> Region.CHUNKS.BITS].get();
		if (chunk != null) {
			chunk.unload(false);
		}
		SpoutChunk newChunk = new SpoutChunk(getWorld(), this, getBlockX() | x, getBlockY() | y, getBlockZ() | z, SpoutChunk.PopulationState.POPULATED, blockIds, blockData, skyLight, blockLight, new ManagedHashMap(), true);
		setChunk(newChunk, x, y, z, null, true);
		checkChunkLoaded(newChunk, LoadOption.LOAD_GEN);
	}

	private class MeshGeneratorThread extends Thread {

		private WorldRenderer renderer = null;

		public MeshGeneratorThread() {
			super(SpoutRegion.this.toString() + " Mesh Generation Thread");
			this.setDaemon(true);
		}

		public void run() {
			//Sleep while the renderer doesn't exist
			while(((SpoutClient) Spout.getEngine()).getRenderer() == null){
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
					return;
				}
			}
			
			while (renderer == null) {
				renderer = ((SpoutClient) Spout.getEngine()).getRenderer().getWorldRenderer();
				if (renderer == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						return;
					}
				}
			}
			while (!Thread.interrupted()) {
				try {
					SpoutChunkSnapshotModel model;
					while( ( model = removeFromRenderQueue() ) != null){
						handle(model);
					}
				} catch (InterruptedException ie) {
					break;
				}
			}
			SpoutChunkSnapshotModel model;
			boolean done = false;
			while (!done) {
				try {
					model = removeFromRenderQueue();
					if (model != null) {
						handle(model);
					} else {
						done = true;
					}
				} catch (InterruptedException e) {
				}
			}
		}

		private void handle(SpoutChunkSnapshotModel model) {
			ChunkMesh mesh = new ChunkMesh(model);

			mesh.update();
			
			//Debug
			//System.out.println("Generate ChunkMesh take " + (System.currentTimeMillis() - mesh.getTime()) + " (in queue : " + renderChunkQueue.size() +")");
			
			renderer.addMeshToBatchQueue(mesh);
		}

	}
	
	@Override
	public CuboidLightBuffer getLightBuffer(short id) {
		throw new UnsupportedOperationException("Unable to get a light buffer corresponding to a region");
	}

	private SpoutChunk[][][] getChunks(int x, int y, int z, CuboidBlockMaterialBuffer buffer) {
		Vector3 size = buffer.getSize();

		int startX = Math.max(x, getBlockX());
		int startY = Math.max(y, getBlockY());
		int startZ = Math.max(z, getBlockZ());

		int endX = Math.min(getBlockX() + BLOCKS.SIZE - 1, x + size.getFloorX());
		int endY = Math.min(getBlockY() + BLOCKS.SIZE - 1, y + size.getFloorY());
		int endZ = Math.min(getBlockZ() + BLOCKS.SIZE - 1, z + size.getFloorZ());

		Chunk start = getChunkFromBlock(startX, startY, startZ);
		Chunk end = getChunkFromBlock(endX - 1, endY - 1, endZ - 1);

		int chunkSizeX = end.getX() - start.getX() + 1;
		int chunkSizeY = end.getY() - start.getY() + 1;
		int chunkSizeZ = end.getZ() - start.getZ() + 1;

		int chunkStartX = start.getX();
		int chunkStartY = start.getY();
		int chunkStartZ = start.getZ();

		int chunkEndX = end.getX();
		int chunkEndY = end.getY();
		int chunkEndZ = end.getZ();

		SpoutChunk[][][] chunks = new SpoutChunk[chunkSizeX][chunkSizeY][chunkSizeZ];
		for (int dx = chunkStartX; dx <= chunkEndX; dx++) {
			for (int dy = chunkStartY; dy <= chunkEndY; dy++) {
				for (int dz = chunkStartZ; dz <= chunkEndZ; dz++) {
					SpoutChunk chunk = getChunk(dx, dy, dz, LoadOption.LOAD_GEN);
					if (chunk == null) {
						throw new IllegalStateException("Null chunk loaded with LoadOption.LOAD_GEN");
					}
					chunks[dx - chunkStartX][dy - chunkStartY][dz - chunkStartZ] = chunk;
				}
			}
		}
		return chunks;
	}
	
	@Override
	public boolean commitCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		Vector3 base = buffer.getBase();
		int x = base.getFloorX();
		int y = base.getFloorY();
		int z = base.getFloorZ();
		
		return getWorld().commitCuboid(getChunks(x, y, z, buffer), buffer, cause);
	}

	@Override
	public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		Vector3 base = buffer.getBase();
		setCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer, cause);
	}

	@Override
	public void setCuboid(int x, int y, int z, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		getWorld().setCuboid(getChunks(x, y, z, buffer), x, y, z, buffer, cause);
	}

	@Override
	public void getCuboid(CuboidBlockMaterialBuffer buffer) {
		Vector3 base = buffer.getBase();
		getCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer);
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz) {
		return getCuboid(bx, by, bz, sx, sy, sz, true);
	}
	
	@Override
	public CuboidBlockMaterialBuffer getCuboid(boolean backBuffer) {
		return getCuboid(getBlockX(), getBlockY(), getBlockZ(), Region.BLOCKS.SIZE, Region.BLOCKS.SIZE, Region.BLOCKS.SIZE, backBuffer);
	}
	
	@Override
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz, boolean backBuffer) {
		CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(bx, by, bz, sx, sy, sz, backBuffer);
		getCuboid(bx, by, bz, buffer);
		return buffer;
	}

	@Override
	public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer) {
		getWorld().getCuboid(getChunks(bx, by, bz, buffer), bx, by, bz, buffer);
	}
	
	private class RegionSetQueueElement extends SetQueueElement<Cube> {

		public RegionSetQueueElement(SetQueue<Cube> queue, SpoutRegion value) {
			super(queue, value);
		}

		@Override
		protected boolean isValid() {
			return true;
		}
		
	}

	@Override
	public Thread getExecutionThread() {
		return executionThread;
	}
	
	@Override
	public void setExecutionThread(Thread t) {
		this.executionThread = t;
	}

	@Override
	public ImmutableHeightMapBuffer getLocalHeightMap(int x, int z, LoadOption loadopt) {
		SpoutColumn col = getWorld().getColumn((getChunkX() + x) << Chunk.BLOCKS.BITS, (getChunkZ() + z) << Chunk.BLOCKS.BITS, true);
		if (col == null) {
			return null;
		}
		return col.getHeightMapBuffer();
	}

}
