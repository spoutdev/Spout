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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.spout.api.Spout;
import org.spout.api.component.impl.PhysicsComponent;
import org.spout.api.component.type.BlockComponent;
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
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.render.RenderMaterial;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.api.util.map.TByteTripleObjectHashMap;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.api.util.set.TByteTripleHashSet;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.entity.component.SpoutPhysicsComponent;
import org.spout.engine.filesystem.ChunkDataForRegion;
import org.spout.engine.filesystem.versioned.ChunkFiles;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.TripleInt;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.world.collision.RegionShape;
import org.spout.engine.world.collision.SpoutPhysicsWorld;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;
import org.spout.engine.world.dynamic.DynamicBlockUpdateTree;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.collision.shapes.voxel.VoxelWorldShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class SpoutRegion extends Region {
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
	private final ConcurrentLinkedQueue<SpoutChunkSnapshotFuture> snapshotQueue = new ConcurrentLinkedQueue<SpoutChunkSnapshotFuture>();
	protected Queue<Chunk> unloadQueue = new ConcurrentLinkedQueue<Chunk>();
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
	final ArrayBlockingQueue<SpoutChunk> populationQueue = new ArrayBlockingQueue<SpoutChunk>(CHUNKS.VOLUME);
	final ArrayBlockingQueue<SpoutChunk> populationPriorityQueue = new ArrayBlockingQueue<SpoutChunk>(CHUNKS.VOLUME);
	private final AtomicBoolean[][] generatedColumns = new AtomicBoolean[CHUNKS.SIZE][CHUNKS.SIZE];
	private final SpoutTaskManager taskManager;
	private final Thread executionThread;
	private final List<Thread> meshThread;
	private final SpoutScheduler scheduler;
	private final LinkedHashMap<SpoutPlayer, TByteTripleHashSet> observers = new LinkedHashMap<SpoutPlayer, TByteTripleHashSet>();
	private final ConcurrentLinkedQueue<SpoutChunk> observedChunkQueue = new ConcurrentLinkedQueue<SpoutChunk>();
	private final ArrayBlockingQueue<SpoutChunk> localPhysicsChunks = new ArrayBlockingQueue<SpoutChunk>(CHUNKS.VOLUME);
	private final ArrayBlockingQueue<SpoutChunk> globalPhysicsChunks = new ArrayBlockingQueue<SpoutChunk>(CHUNKS.VOLUME);
	private final ArrayBlockingQueue<SpoutChunk> dirtyChunks = new ArrayBlockingQueue<SpoutChunk>(CHUNKS.VOLUME);
	private final DynamicBlockUpdateTree dynamicBlockTree;
	private List<DynamicBlockUpdate> multiRegionUpdates = null;
	private boolean renderQueueEnabled = false;

	private final TByteTripleObjectHashMap<SpoutChunkSnapshotModel> renderChunkQueued = new TByteTripleObjectHashMap<SpoutChunkSnapshotModel>();
	private final ConcurrentSkipListSet<SpoutChunkSnapshotModel> renderChunkQueue = new ConcurrentSkipListSet<SpoutChunkSnapshotModel>();

	private final AtomicReference<SpoutRegion>[][][] neighbours;

	//Physics
	private final DiscreteDynamicsWorld simulation;
	private final CollisionDispatcher dispatcher;
	private final BroadphaseInterface broadphase;
	private final CollisionConfiguration configuration;
	private final SequentialImpulseConstraintSolver solver;

	@SuppressWarnings("unchecked")
	public SpoutRegion(SpoutWorld world, float x, float y, float z, RegionSource source) {
		super(world, x * Region.BLOCKS.SIZE, y * Region.BLOCKS.SIZE, z * Region.BLOCKS.SIZE);
		this.source = source;

		int xx = MathHelper.mod(getX(), 3);
		int yy = MathHelper.mod(getY(), 3);
		int zz = MathHelper.mod(getZ(), 3);
		updateSequence = (xx * 9) + (yy * 3) + zz;

		manager = new SpoutRegionManager(this, 2, new ThreadAsyncExecutor(this.toString() + " Thread", updateSequence), world.getEngine());

		AsyncExecutor ae = manager.getExecutor();
		if (ae instanceof Thread) {
			executionThread = (Thread) ae;
		} else {
			executionThread = null;
		}

		if (Spout.getPlatform() == Platform.CLIENT) {
			meshThread = new ArrayList<Thread>();
			for(int i = 0; i < 2; i++ ){//TODO : Make a option to choice the number of thread to make mesh
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

		for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
			for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
				generatedColumns[dx][dz] = new AtomicBoolean(false);
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

		this.chunkStore = world.getRegionFile(getX(), getY(), getZ());
		Thread t;
		AsyncExecutor e = manager.getExecutor();
		if (e instanceof Thread) {
			t = (Thread) e;
		} else {
			throw new IllegalStateException("AsyncExecutor should be instance of Thread");
		}
		taskManager = new SpoutTaskManager(world.getEngine().getScheduler(), false, t, world.getAge());
		scheduler = (SpoutScheduler) (Spout.getEngine().getScheduler());

		//Physics
		broadphase = new DbvtBroadphase();
		broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
		configuration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(configuration);
		solver = new SequentialImpulseConstraintSolver();
		simulation = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, configuration);
		simulation.setGravity(new Vector3f(0, -9.81F, 0));
		simulation.getDispatchInfo().allowedCcdPenetration = 5f;
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

	public void addPhysics(Entity e) {
		PhysicsComponent physics = e.get(PhysicsComponent.class);
		if (physics != null) {
			addPhysics(physics);
		}
	}

	public void removePhysics(Entity e) {
		PhysicsComponent physics = e.get(PhysicsComponent.class);
		if (physics != null) {
			removePhysics(physics);
		}
	}

	public void addPhysics(PhysicsComponent physics) {
		CollisionObject object = ((SpoutPhysicsComponent) physics).getCollisionObject();
		if (object == null || object.getCollisionShape() == null) {
			return;
		}
		synchronized(simulation) {
			if (object instanceof RigidBody) {
				simulation.addRigidBody((RigidBody) object);
			} else {
				simulation.addCollisionObject(object);
			}
		}
	}

	public void removePhysics(PhysicsComponent physics) {
		CollisionObject object = ((SpoutPhysicsComponent) physics).getCollisionObject();
		if (object == null || object.getCollisionShape() == null) {
			return;
		}
		synchronized(simulation) {
			if (object instanceof RigidBody) {
				simulation.removeRigidBody((RigidBody) object);
			} else {
				simulation.removeCollisionObject(object);
			}
		}
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
		if (loadopt != LoadOption.NO_LOAD) {
			TickStage.checkStage(~TickStage.SNAPSHOT);
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
		}

		if (loadopt.generateIfNeeded() && !fileExists && newChunk == null) {
			// TODO remove debug code below
			final boolean oldGenerated = generatedColumns[x][z].get();
			// TODO remove debug code above
			generateChunks(x, z, loadopt);
			final SpoutChunk generatedChunk = chunks[x][y][z].get();
			if (generatedChunk != null) {
				checkChunkLoaded(generatedChunk, loadopt);
				return generatedChunk;
			} else {
				Spout.getLogger().severe("Chunk failed to generate!  Column marked as generated: before " + oldGenerated + ", after " + generatedColumns[x][z].get());
				for (int yy = 0; yy < CHUNKS.SIZE; yy++) {
					Spout.getLogger().info(x + ", " + (y + getChunkY()) + ", " + z + " : " + chunks[x][yy][z]);
					Spout.getLogger().info("File exists: " + this.inputStreamExists(x, y, z));
				}
			}
		}

		if (newChunk == null) {
			return null;
		}

		return setChunk(newChunk, x, y, z, dataForRegion, false, loadopt);
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

	private void generateChunks(int x, int z, LoadOption loadopt) {
		final AtomicBoolean generated = generatedColumns[x][z];
		if (generated.get()) {
			return;
		}
		synchronized(generated) {
			if (generated.get()) {
				return;
			}
			int cx = getChunkX(x);
			int cy = getChunkY();
			int cz = getChunkZ(z);
			final SpoutWorld world = getWorld();

			final CuboidShortBuffer column = new CuboidShortBuffer(cx << Chunk.BLOCKS.BITS, cy << Chunk.BLOCKS.BITS, cz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE, CHUNKS.SIZE * Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE);
			getWorld().getGenerator().generate(column, cx, cy, cz, world);

			final int endY = cy + CHUNKS.SIZE;
			for (int yy = 0; cy < endY; cy++) {
				final CuboidShortBuffer chunk = new CuboidShortBuffer(cx << Chunk.BLOCKS.BITS, cy << Chunk.BLOCKS.BITS, cz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE);
				chunk.setSource(column);
				chunk.copyElement(0, yy * Chunk.BLOCKS.VOLUME, Chunk.BLOCKS.VOLUME);
				SpoutChunk newChunk = new SpoutChunk(world, this, cx, cy, cz, chunk.getRawArray(), null);
				newChunk.setModified();
				SpoutChunk currentChunk = setChunk(newChunk, x, yy++, z, null, true, loadopt);
				if (currentChunk != newChunk) {
					Spout.getLogger().info("Warning: Unable to set generated chunk, new Chunk " + newChunk + " chunk in memory " + currentChunk);
				}
			}
			if (!generated.compareAndSet(false, true)) {
				throw new IllegalStateException("Column generated twice");
			}
		}

	}

	private SpoutChunk setChunk(SpoutChunk newChunk, int x, int y, int z, ChunkDataForRegion dataForRegion, boolean generated, LoadOption loadopt) {
		final AtomicReference<SpoutChunk> chunkReference = chunks[x][y][z];
		while (true) {
			if (chunkReference.compareAndSet(null, newChunk)) {
				newChunk.notifyColumn();
				if (Spout.getEngine().getPlatform() == Platform.CLIENT) { 
					newChunk.setNeighbourRenderDirty(true);
				}
				numberActiveChunks.incrementAndGet();
				if (dataForRegion != null) {
					for (SpoutEntity entity : dataForRegion.loadedEntities) {
						entity.setupInitialChunk(entity.getTransform().getTransform());
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
				checkChunkLoaded(oldChunk, loadopt);
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
		TickStage.checkStage(TickStage.SNAPSHOT, executionThread);
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

			populationQueue.remove(currentChunk);

			dirtyChunks.remove(currentChunk);

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

	public void copySnapshotRun() {
		entityManager.copyAllSnapshots();

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
			}

			empty |= processChunkSaveUnload(chunkCoords.x, chunkCoords.y, chunkCoords.z);
		}

		SpoutChunk c;
		TByteTripleHashSet done = new TByteTripleHashSet();
		while ((c = observedChunkQueue.poll()) != null) {
			int cx = c.getX() & CHUNKS.MASK;
			int cy = c.getY() & CHUNKS.MASK;
			int cz = c.getZ() & CHUNKS.MASK;
			if (!done.add(cx, cy, cz)) {
				continue;
			}
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

	public boolean processChunkSaveUnload(int x, int y, int z) {
		boolean empty = false;
		SpoutChunk c = getChunk(x, y, z, LoadOption.NO_LOAD);
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

	public void queueChunkForPopulation(SpoutChunk c, boolean priority) {
		if (!priority) {
			populationQueue.add(c);
		} else {
			populationPriorityQueue.add(c);
		}
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

	private boolean isVisibleToPlayers() {
		if (this.entityManager.getPlayers().size() > 0) {
			return true;
		}
		//Search for players near to the center of the region
		int bx = getBlockX();
		int by = getBlockY();
		int bz = getBlockZ();
		int half = BLOCKS.SIZE / 2;
		Point center = new Point(getWorld(), bx + half, by + half, bz + half);
		return getWorld().getNearbyPlayers(center, BLOCKS.SIZE).size() > 0;
	}

	private void updateEntities(float dt) {
		boolean visible = isVisibleToPlayers();
		for (SpoutEntity ent : entityManager.getAll()) {
			try {
				//Try and determine if we should tick this entity
				//If the entity is not important (not an observer)
				//And the entity is not visible to players, don't tick it
				if (visible) { //TODO: Replace isImportant
					ent.tick(dt);
				}
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
	}

	private void updatePopulation() {
		for (int i = 0; i < POPULATE_PER_TICK; i++) {
			SpoutChunk toPopulate = populationPriorityQueue.poll();
			if (toPopulate == null) {
				toPopulate = populationQueue.poll();
				if (toPopulate == null) {
					break;
				}
				toPopulate.setNotQueuedForPopulation(false);
			} else {
				toPopulate.setNotQueuedForPopulation(true);
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
		Chunk toUnload = unloadQueue.poll();
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
			synchronized(simulation) {
				simulation.stepSimulation(dt, 10);
			}
		} catch (Exception e) {
			synchronized(logLock) {
				Spout.getLogger().log(Level.SEVERE, "Exception while executing physics in region " + getBase().toBlockString(), e);
			}
		}
	}

	public void startTickRun(int stage, long delta) {
		final float dt = delta / 1000F;
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
		default: {
			throw new IllegalStateException("Number of states exceeded limit for SpoutRegion");
		}
		}
	}

	public void haltRun() {
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
		if(renderer == null)
			renderer = ((SpoutClient) Spout.getEngine()).getWorldRenderer();
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
						playerPosition = player.getTransform().getPosition();
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

				List<SpoutChunk> renderLater = new LinkedList<SpoutChunk>();

				SpoutChunk spoutChunk;
				while ((spoutChunk = dirtyChunks.poll()) != null) {

					spoutChunk.setNotDirtyQueued();
					if (!spoutChunk.isLoaded()) {
						continue;
					}

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
						for (Player entity : spoutChunk.getObservingPlayers()) {
							syncChunkToPlayer(spoutChunk, entity);
						}
						processChunkUpdatedEvent(spoutChunk);

						spoutChunk.resetDirtyArrays();
						spoutChunk.setLightDirty(false);
					}
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

	public void queueDirty(SpoutChunk chunk) {
		dirtyChunks.add(chunk);
	}

	public void runPhysics(int sequence) throws InterruptedException {
		if (sequence == -1) {
			runLocalPhysics();
		} else if (sequence == this.updateSequence) {
			runGlobalPhysics();
		}
	}

	public void runLocalPhysics() throws InterruptedException {
		boolean updated = true;

		while (updated) {
			updated = false;
			SpoutChunk c;
			while ((c = this.localPhysicsChunks.poll()) != null) {
				c.setInactivePhysics(true);
				updated |= c.runLocalPhysics();
			}
		}
	}

	public void runGlobalPhysics() throws InterruptedException {
		SpoutChunk c;
		while ((c = this.globalPhysicsChunks.poll()) != null) {
			c.setInactivePhysics(false);
			c.runGlobalPhysics();
		}
	}

	public void runDynamicUpdates(long time, int sequence) throws InterruptedException {
		scheduler.addUpdates(dynamicBlockTree.getLastUpdates());
		dynamicBlockTree.resetLastUpdates();

		if (sequence == -1) {
			runLocalDynamicUpdates(time);
		} else if (sequence == this.updateSequence) {
			runGlobalDynamicUpdates();
		}
	}

	public long getFirstDynamicUpdateTime() {
		return dynamicBlockTree.getFirstDynamicUpdateTime();
	}

	public void runLocalDynamicUpdates(long time) throws InterruptedException {
		long currentTime = getWorld().getAge();
		if (time > currentTime) {
			time = currentTime;
		}
		dynamicBlockTree.commitAsyncPending(currentTime);
		multiRegionUpdates = dynamicBlockTree.updateDynamicBlocks(currentTime, time);
	}

	public void runGlobalDynamicUpdates() throws InterruptedException {
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

	int lightingUpdates = 0;

	public void runLighting(int sequence) throws InterruptedException {
		if (sequence == -1) {
			runLocalLighting();
		} else if (sequence == this.updateSequence) {
			runGlobalLighting();
		}
	}

	public void runLocalLighting() throws InterruptedException {
		scheduler.addUpdates(lightingUpdates);
		lightingUpdates = 0;
	}

	public void runGlobalLighting() throws InterruptedException {
		scheduler.addUpdates(lightingUpdates);
		lightingUpdates = 0;
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

	public Thread getExceutionThread() {
		return executionThread;
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

	protected void reportChunkLightDirty(int x, int y, int z) {
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

	public void markObserverDirty(SpoutChunk chunk) {
		observedChunkQueue.add(chunk);
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		dynamicBlockTree.resetBlockUpdates(x, y, z);
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		dynamicBlockTree.syncResetBlockUpdates(x, y, z);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data) {
		return dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate, data);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate) {
		return dynamicBlockTree.queueBlockUpdates(x, y, z, nextUpdate);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z) {
		return dynamicBlockTree.queueBlockUpdates(x, y, z);
	}

	public void setGravity(Vector3 gravity) {
		synchronized(simulation) {
			simulation.setGravity(MathHelper.toVector3f(gravity));
		}
	}

	public Vector3 getGravity() {
		synchronized(simulation) {
			Vector3f vector = new Vector3f();
			vector = simulation.getGravity(vector);
			return MathHelper.toVector3(vector);
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

	public void setPhysicsActive(SpoutChunk chunk, boolean local) {
		try {
			if (local) {
				localPhysicsChunks.add(chunk);
			} else {
				globalPhysicsChunks.add(chunk);
			}
		} catch (IllegalStateException ise) {
			throw new IllegalStateException("Physics chunk queue exceeded capacity", ise);
		}
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
		setChunk(newChunk, x, y, z, null, true, LoadOption.LOAD_GEN);
	}

	private class MeshGeneratorThread extends Thread {

		private WorldRenderer renderer = null;

		public MeshGeneratorThread() {
			super(SpoutRegion.this.toString() + " Mesh Generation Thread");
			this.setDaemon(true);
		}

		public void run() {
			while (renderer == null) {
				renderer = ((SpoutClient) Spout.getEngine()).getWorldRenderer();
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
						Thread.sleep(20);
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

			renderer.addMeshToBatchQueue(mesh);
		}

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
		CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(bx, by, bz, sx, sy, sz);
		getCuboid(bx, by, bz, buffer);
		return buffer;
	}

	@Override
	public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer) {
		getWorld().getCuboid(getChunks(bx, by, bz, buffer), bx, by, bz, buffer);
	}

}
