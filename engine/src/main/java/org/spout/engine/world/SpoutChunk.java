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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import com.google.common.collect.Sets;

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;

import org.spout.api.ClientOnly;
import org.spout.api.Platform;
import org.spout.api.ServerOnly;
import org.spout.api.Spout;
import org.spout.api.component.BlockComponentOwner;
import org.spout.api.component.Component;
import org.spout.api.component.block.BlockComponent;
import org.spout.api.component.entity.NetworkComponent;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.entity.Entity;
import org.spout.api.event.Cause;
import org.spout.api.event.block.BlockChangeEvent;
import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.BlockComponentContainer;
import org.spout.api.geo.cuboid.BlockContainer;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.api.geo.cuboid.ContainerFillOrder;
import org.spout.api.geo.cuboid.Cube;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.lighting.LightingManager;
import org.spout.api.lighting.LightingRegistry;
import org.spout.api.lighting.Modifiable;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.material.block.BlockSnapshot;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.IntVector3;
import org.spout.api.protocol.event.BlockUpdateEvent;
import org.spout.api.protocol.event.ChunkSendEvent;
import org.spout.api.render.RenderMaterial;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.hashing.NibbleQuadHashed;
import org.spout.api.util.list.concurrent.setqueue.SetQueue;
import org.spout.api.util.list.concurrent.setqueue.SetQueueElement;
import org.spout.api.util.map.concurrent.AtomicBlockStore;
import org.spout.api.util.map.concurrent.palette.AtomicPaletteBlockStore;

import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.thread.snapshotable.Snapshotable;
import org.spout.engine.world.physics.PhysicsQueue;
import org.spout.engine.world.physics.UpdateQueue;
import org.spout.math.GenericMath;
import org.spout.math.vector.Vector3f;

public class SpoutChunk extends Chunk implements Snapshotable, Modifiable {
	public static final WeakReference<SpoutChunk> NULL_WEAK_REFERENCE = new WeakReference<>(null);
	//Not static to allow the engine to parse values first
	private final int autosaveInterval = SpoutConfiguration.AUTOSAVE_INTERVAL.getInt(60000);
	private final Set<SpoutEntity> observers = Sets.newSetFromMap(new ConcurrentHashMap<SpoutEntity, Boolean>());
	private final Set<SpoutEntity> unmodifiableObservers = Collections.unmodifiableSet(observers);
	private final Set<SpoutPlayer> observingPlayers = Sets.newSetFromMap(new ConcurrentHashMap<SpoutPlayer, Boolean>());
	private final Set<SpoutPlayer> unmodifiableObservingPlayers = Collections.unmodifiableSet(observingPlayers);
	/**
	 * An entity may still exist but no longer be an observer. As such, we need to remove all entities that it was observing. The following three variables deal with this.
	 */
	private final ConcurrentLinkedQueue<SpoutEntity> expiredObserversQueue = new ConcurrentLinkedQueue<>();
	private final LinkedHashSet<SpoutEntity> expiredObservers = new LinkedHashSet<>();
	private final Set<SpoutEntity> unmodifiableExpiredObservers = Collections.unmodifiableSet(expiredObservers);
	/**
	 * Not thread safe, synchronize on access
	 */
	private final TShortObjectHashMap<BlockComponentOwner> blockComponents = new TShortObjectHashMap<>();
	/**
	 * Multi-thread write access to the block store is only allowed during the allowed stages. During the restricted stages, only the region thread may modify the block store.
	 */
	private static final int restrictedStages = TickStage.PHYSICS | TickStage.DYNAMIC_BLOCKS;
	private static final int allowedStages = TickStage.STAGE1 | TickStage.STAGE2P | TickStage.TICKSTART | TickStage.GLOBAL_PHYSICS | TickStage.GLOBAL_DYNAMIC_BLOCKS;
	/**
	 * Storage for block ids, data and auxiliary data. For blocks with data = 0 and auxiliary data = null, the block is stored as a short.
	 */
	protected AtomicBlockStore blockStore;
	/**
	 * Indicates that the chunk should be saved if unloaded
	 */
	protected final AtomicReference<SaveState> saveState = new AtomicReference<>(SaveState.NONE);
	private final ChunkSetQueueElement<Cube> saveMarkedElement;
	/**
	 * The parent region that manages this chunk
	 */
	protected final SpoutRegion parentRegion;
	/**
	 * Holds if the chunk is populated
	 */
	private final AtomicReference<PopulationState> populationState;
	/**
	 * The {@link SpoutColumn} that this chunk exists in.
	 */
	protected final SpoutColumn column;
	protected final AtomicBoolean columnRegistered = new AtomicBoolean(true);
	protected final AtomicLong lastUnloadCheck = new AtomicLong();
	/**
	 * True if this chunk should be resent due to light calculations
	 */
	protected final AtomicBoolean lightDirty = new AtomicBoolean(false);
	/**
	 * Data map and Datatable associated with it
	 */
	protected final ManagedHashMap dataMap;
	/**
	 * Shift cache array for shifting fields
	 */
	protected final static int[] shiftCache = new int[65536];
	/**
	 * The order that blocks are stored in the block store
	 */
	private static ContainerFillOrder STORE_FILL_ORDER = ContainerFillOrder.XZY;
	/**
	 * A set of all blocks in this chunk that need a physics update in the next tick. The coordinates in this set are relative to the <b>Region</b> containing the chunk.
	 */
	private final PhysicsQueue physicsQueue;
	private final SpoutScheduler scheduler;
	/**
	 * Keeps track if the chunk has been modified since it's last save
	 */
	private final AtomicBoolean chunkModified = new AtomicBoolean(false);
	private final AtomicBoolean entitiesModified = new AtomicBoolean(false);
	/**
	 * A WeakReference to this chunk
	 */
	private final WeakReference<SpoutChunk> selfReference;
	/**
	 * An array of light buffers associated with this Chunk
	 */
	private final AtomicReference<CuboidLightBuffer[]> lightBuffers = new AtomicReference<>(new CuboidLightBuffer[0]);
	private final static CuboidLightBuffer[] lightBufferExample = new CuboidLightBuffer[0];
	/**
	 * This field is used to store a "fake" observer for force queuing population.
	 */
	private final AtomicBoolean popObserver = new AtomicBoolean(false);
	private final AtomicInteger autosaveTicks = new AtomicInteger(0);
	private final ChunkSetQueueElement<SpoutChunk> unloadQueueElement;
	private final ChunkSetQueueElement<SpoutChunk> populationQueueElement;
	private final ChunkSetQueueElement<SpoutChunk> populationPriorityQueueElement;
	private final ChunkSetQueueElement<SpoutChunk> localPhysicsChunkQueueElement;
	private final ChunkSetQueueElement<SpoutChunk> globalPhysicsChunkQueueElement;
	private final ChunkSetQueueElement<SpoutChunk> dirtyChunkQueueElement;
	private final ChunkSetQueueElement<SpoutChunk> newChunkQueueElement;
	// TODO: remove this and replace with above;
	private boolean firstRender = true;
	// Rendering
	private final AtomicInteger renderSequence = new AtomicInteger(0);
	private SpoutChunkSnapshot renderSnapshotCache;
	private int generationIndex = -1;

	static {
		for (int i = 0; i < shiftCache.length; i++) {
			int shift = 0;
			while ((i > 0) && (i >> shift) << shift == i) {
				shift++;
			}
			shiftCache[i] = shift - 1;
		}
	}

	/**
	 * Chunk generation
	 */
	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, AtomicBlockStore blockStore, ManagedHashMap extraData) {
		this(world, region, x, y, z, PopulationState.UNTOUCHED, extraData, blockStore);
	}

	// TODO: investigate "storeState" in the following two new AtomicPaletteBlockStore...
	/**
	 * Chunk loading
	 */
	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, PopulationState popState, int[] palette, int blockArrayWidth, int[] variableWidthBlockArray, ManagedHashMap extraData, boolean lightStable) {
		this(world, region, x, y, z, popState, extraData, new AtomicPaletteBlockStore(BLOCKS.BITS, Spout.getEngine().getPlatform() == Platform.CLIENT, true, 10, palette, blockArrayWidth, variableWidthBlockArray));
	}

	/**
	 * Chunk sending to client and client LOAD_GEN 
	 */
	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, PopulationState popState, short[] blocks, short[] data, ManagedHashMap extraData) {
		this(world, region, x, y, z, popState, extraData, new AtomicPaletteBlockStore(BLOCKS.BITS, Spout.getEngine().getPlatform() == Platform.CLIENT, false, 10, blocks, data));
	}

	/**
	 * Common logic constructor
	 */
	private SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, PopulationState popState, ManagedHashMap extraData, AtomicBlockStore blockStore) {
		super(world, x * BLOCKS.SIZE, y * BLOCKS.SIZE, z * BLOCKS.SIZE);
		parentRegion = region;
		this.populationState = new AtomicReference<>(popState);
		this.blockStore = blockStore;
		blockStore.resetDirtyArrays();

		if (extraData != null) {
			this.dataMap = extraData;
		} else {
			this.dataMap = new ManagedHashMap();
		}

		physicsQueue = new PhysicsQueue(this);

		if (Spout.getPlatform() == Platform.CLIENT) {
			column = world.getColumn(this.getX(), this.getZ(), LoadOption.NO_LOAD);
		} else {
			column = world.getColumn(this.getX(), this.getZ(), LoadOption.LOAD_GEN);
		}
		column.registerCuboid(getBlockY(), getBlockY() + Chunk.BLOCKS.SIZE - 1);
		columnRegistered.set(true);
		lastUnloadCheck.set(world.getAge());

		// loaded chunk
		selfReference = new WeakReference<>(this);
		this.scheduler = (SpoutScheduler) Spout.getScheduler();
		this.saveMarkedElement = new ChunkSetQueueElement<>(getRegion().saveMarkedQueue, this);
		this.unloadQueueElement = new ChunkSetQueueElement<>(getRegion().unloadQueue, this);
		this.populationQueueElement = new ChunkSetQueueElement<>(getRegion().populationQueue, this);
		this.populationPriorityQueueElement = new ChunkSetQueueElement<>(getRegion().populationPriorityQueue, this);
		this.localPhysicsChunkQueueElement = new ChunkSetQueueElement<>(getRegion().localPhysicsChunkQueue, this);
		this.globalPhysicsChunkQueueElement = new ChunkSetQueueElement<>(getRegion().globalPhysicsChunkQueue, this);
		this.dirtyChunkQueueElement = new ChunkSetQueueElement<>(getRegion().dirtyChunkQueue, this);
		this.newChunkQueueElement = new ChunkSetQueueElement<>(getRegion().newChunkQueue, this);
	}

	@Override
	public SpoutWorld getWorld() {
		return (SpoutWorld) super.getWorld();
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Cause<?> cause) {
		setBlockDataField(x, y, z, 0xFFFF, data, cause);

		return true;
	}

	@Override
	public boolean addBlockData(int x, int y, int z, short data, Cause<?> cause) {
		addBlockDataField(x, y, z, 0xFFFF, data, cause);

		return true;
	}

	public int touchBlock(int x, int y, int z) {
		int bx = x & BLOCKS.MASK;
		int by = y & BLOCKS.MASK;
		int bz = z & BLOCKS.MASK;
		try {
			return blockStore.touchBlock(bx, by, bz);
		} finally {
			queueDirty();
		}
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Cause<?> cause) {
		return setBlockMaterial(x, y, z, material, data, cause, true);
	}

	private boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Cause<?> cause, boolean event) {
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		material = material.getSubMaterial(data);
		short dataMask = material.getDataMask();
		data = (short) ((data & ~dataMask) | (material.getData() & dataMask));

		if (event) {
			// TODO - move to block change method?
			Block block = getBlock(x, y, z);
			BlockChangeEvent blockEvent = new BlockChangeEvent(block, new BlockSnapshot(block, material, data), cause);
			Spout.getEngine().getEventManager().callEvent(blockEvent);
			if (blockEvent.isCancelled()) {
				return false;
			}
			material = blockEvent.getSnapshot().getMaterial();
			data = blockEvent.getSnapshot().getData();
		}

		short newId = material.getId();
		short newData = data;
		int newState = BlockFullState.getPacked(newId, newData);

		int oldState = getAndSetBlock(x, y, z, newId, newData);
		BlockMaterial oldMaterial = MaterialRegistry.get(oldState);

		if (newState != oldState) {
			short oldData = BlockFullState.getData(oldState);
			blockChanged(x, y, z, material, newData, oldMaterial, oldData, cause);
			return true;
		}
		return false;
	}

	@Override
	public boolean commitCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		blockStore.writeLock();

		try {
			Vector3f base = buffer.getBase();
			int x = base.getFloorX();
			int y = base.getFloorY();
			int z = base.getFloorZ();

			if (testCuboid(x, y, z, buffer)) {
				return false;
			}

			setCuboid(x, y, z, buffer, cause);

			return true;
		} finally {
			blockStore.writeUnlock();
		}
	}

	@Override
	public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		Vector3f base = buffer.getBase();
		setCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer, cause);
	}

	@Override
	public void setCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		blockStore.writeLock();
		try {
			Vector3f size = buffer.getSize();

			int startX = Math.max(bx, this.getBlockX());
			int startY = Math.max(by, this.getBlockY());
			int startZ = Math.max(bz, this.getBlockZ());

			int endX = Math.min(bx + size.getFloorX(), this.getBlockX() + BLOCKS.SIZE - 1);
			int endY = Math.min(by + size.getFloorY(), this.getBlockY() + BLOCKS.SIZE - 1);
			int endZ = Math.min(bz + size.getFloorZ(), this.getBlockZ() + BLOCKS.SIZE - 1);

			Vector3f base = buffer.getBase();

			int offX = bx - base.getFloorX();
			int offY = by - base.getFloorY();
			int offZ = bz - base.getFloorZ();

			for (int dx = startX; dx < endX; dx++) {
				for (int dy = startY; dy < endY; dy++) {
					for (int dz = startZ; dz < endZ; dz++) {
						setBlockMaterial(dx, dy, dz, buffer.get(dx - offX, dy - offY, dz - offZ), buffer.getData(dx - offX, dy - offY, dz - offZ), cause, false);
					}
				}
			}
		} finally {
			blockStore.writeUnlock();
		}
	}

	/**
	 * @return true if all materials are the same
	 */
	public boolean testCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer) {
		blockStore.writeLock();
		try {
			Vector3f size = buffer.getSize();

			int startX = Math.max(bx, this.getBlockX());
			int startY = Math.max(by, this.getBlockY());
			int startZ = Math.max(bz, this.getBlockZ());

			int endX = Math.min(bx + size.getFloorX(), this.getBlockX() + BLOCKS.SIZE - 1);
			int endY = Math.min(by + size.getFloorY(), this.getBlockY() + BLOCKS.SIZE - 1);
			int endZ = Math.min(bz + size.getFloorZ(), this.getBlockZ() + BLOCKS.SIZE - 1);

			Vector3f base = buffer.getBase();

			int offX = bx - base.getFloorX();
			int offY = by - base.getFloorY();
			int offZ = bz - base.getFloorZ();

			for (int dx = startX; dx < endX; dx++) {
				for (int dy = startY; dy < endY; dy++) {
					for (int dz = startZ; dz < endZ; dz++) {
						BlockMaterial worldMaterial = getBlockMaterial(dx, dy, dz);
						BlockMaterial bufferMaterial = buffer.get(dx - offX, dy - offY, dz - offZ);

						if (worldMaterial != bufferMaterial) {
							return false;
						}

						short worldData = getBlockData(dx, dy, dz);
						short bufferData = buffer.getData(dx - offX, dy - offY, dz - offZ);

						if (worldData != bufferData) {
							return false;
						}
					}
				}
			}

			return true;
		} finally {
			blockStore.writeUnlock();
		}
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(boolean backBuffer) {
		return getCuboid(getBlockX(), getBlockY(), getBlockZ(), Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, backBuffer);
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz) {
		return getCuboid(bx, by, bz, sx, sy, sz, true);
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz, boolean backBuffer) {
		CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(bx, by, bz, sx, sy, sz, backBuffer);
		getCuboid(bx, by, bz, buffer);
		return buffer;
	}

	@Override
	public void getCuboid(CuboidBlockMaterialBuffer buffer) {
		Vector3f base = buffer.getBase();
		getCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer);
	}

	@Override
	public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer) {
		blockStore.writeLock();
		try {
			Vector3f size = buffer.getSize();

			int startX = Math.max(bx, this.getBlockX());
			int startY = Math.max(by, this.getBlockY());
			int startZ = Math.max(bz, this.getBlockZ());

			int endX = Math.min(bx + size.getFloorX(), this.getBlockX() + BLOCKS.SIZE);
			int endY = Math.min(by + size.getFloorY(), this.getBlockY() + BLOCKS.SIZE);
			int endZ = Math.min(bz + size.getFloorZ(), this.getBlockZ() + BLOCKS.SIZE);

			Vector3f base = buffer.getBase();

			int offX = bx - base.getFloorX();
			int offY = by - base.getFloorY();
			int offZ = bz - base.getFloorZ();

			for (int dx = startX; dx < endX; dx++) {
				for (int dy = startY; dy < endY; dy++) {
					for (int dz = startZ; dz < endZ; dz++) {
						int packed = this.getBlockFullState(dx, dy, dz);
						buffer.set(dx - offX, dy - offY, dz - offZ, BlockFullState.getMaterial(packed).getId(), BlockFullState.getData(packed));
					}
				}
			}
		} finally {
			blockStore.writeUnlock();
		}
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		parentRegion.resetDynamicBlock(getBlockX(x), getBlockY(y), getBlockZ(z));
	}

	@Override
	public void resetDynamicBlocks(Chunk c) {
		parentRegion.resetDynamicBlocks(c);
	}

	public void resetDynamicBlocks() {
		resetDynamicBlocks(this);
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		parentRegion.syncResetDynamicBlock(getBlockX(x), getBlockY(y), getBlockZ(z));
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, boolean exclusive) {
		return parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z), nextUpdate, data, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, boolean exclusive) {
		return parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z), nextUpdate, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, boolean exclusive) {
		return parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z), exclusive);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		checkChunkLoaded();
		int state = blockStore.getFullData(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK);
		short data = BlockFullState.getData(state);
		short id = BlockFullState.getId(state);
		BlockMaterial mat = BlockMaterial.get(id);
		if (mat == null) {
			return BlockMaterial.AIR;
		}

		return mat.getSubMaterial(data);
	}

	@Override
	public int getBlockFullState(int x, int y, int z) {
		return blockStore.getFullData(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK);
	}

	public int getBlockFullState(int index) {
		return blockStore.getFullData(index);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		checkChunkLoaded();
		return (short) blockStore.getData(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK);
	}

	public void setPhysicsActive(boolean local) {
		if (local) {
			this.localPhysicsChunkQueueElement.add();
		} else {
			this.globalPhysicsChunkQueueElement.add();
		}
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range) {
		queueBlockPhysics(x, y, z, range, null);
	}

	public void queueBlockPhysics(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial) {
		checkChunkLoaded();
		int rx = x & BLOCKS.MASK;
		int ry = y & BLOCKS.MASK;
		int rz = z & BLOCKS.MASK;
		rx += (getX() & Region.CHUNKS.MASK) << BLOCKS.BITS;
		ry += (getY() & Region.CHUNKS.MASK) << BLOCKS.BITS;
		rz += (getZ() & Region.CHUNKS.MASK) << BLOCKS.BITS;
		physicsQueue.queueForUpdateAsync(rx, ry, rz, range, oldMaterial);
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z) {
		updateBlockPhysics(x, y, z, null);
	}

	public void updateBlockPhysics(int x, int y, int z, BlockMaterial oldMaterial) {
		checkChunkLoaded();
		int rx = x & BLOCKS.MASK;
		int ry = y & BLOCKS.MASK;
		int rz = z & BLOCKS.MASK;
		rx += (getX() & Region.CHUNKS.MASK) << BLOCKS.BITS;
		ry += (getY() & Region.CHUNKS.MASK) << BLOCKS.BITS;
		rz += (getZ() & Region.CHUNKS.MASK) << BLOCKS.BITS;
		physicsQueue.queueForUpdate(rx, ry, rz, oldMaterial);
	}

	@Override
	public void unload(boolean save) {
		unloadNoMark(save);
		markForSaveUnload();
	}

	public void unloadNoMark(boolean save) {
		SaveState.unload(saveState, save);
	}

	public boolean cancelUnload() {
		return SaveState.cancelUnload(saveState);
	}

	public SaveState getSaveState() {
		return saveState.get();
	}

	@Override
	public void save() {
		checkChunkLoaded();
		saveNoMark();
		markForSaveUnload();
	}

	public void saveNoMark() {
		checkChunkLoaded();
		SaveState.save(saveState);
	}

	private void markForSaveUnload() {
		saveMarkedElement.add();
	}

	public void saveComplete() {
		if (isObserved()) {
			SaveState.resetPostSaving(saveState);
		} else {
			SaveState.setPostSaved(saveState);
		}
		saveMarkedElement.add();
	}

	public SaveState getAndResetSaveState() {
		return SaveState.getAndResetSaveState(saveState);
	}

	// Saves the chunk data - this occurs directly after a snapshot update
	public void syncSave() {
		if (this.chunkModified.get() || entitiesModified.get() || this.hasEntities()) {
			chunkModified.set(false);
			entitiesModified.set(false);
			WorldSavingThread.saveChunk(this);
		} else {
			saveComplete();
		}
	}

	@Override
	public ChunkSnapshot getSnapshot() {
		return getSnapshot(SnapshotType.BOTH, EntityType.NO_ENTITIES, ExtraData.NO_EXTRA_DATA);
	}

	@Override
	public void fillBlockContainer(BlockContainer container) {
		ContainerFillOrder sourceOrder = SpoutChunk.STORE_FILL_ORDER;
		ContainerFillOrder destOrder = container.getOrder();

		int size = BLOCKS.SIZE;

		int sourceIndex = 0;

		int thirdStep = destOrder.thirdStep(sourceOrder, size, size, size);
		int secondStep = destOrder.secondStep(sourceOrder, size, size, size);
		int firstStep = destOrder.firstStep(sourceOrder, size, size, size);

		int thirdMax = destOrder.getThirdSize(size, size, size);
		int secondMax = destOrder.getSecondSize(size, size, size);
		int firstMax = destOrder.getFirstSize(size, size, size);

		for (int third = 0; third < thirdMax; third++) {
			int secondStart = sourceIndex;
			for (int second = 0; second < secondMax; second++) {
				int firstStart = sourceIndex;
				for (int first = 0; first < firstMax; first++) {
					container.setBlockFullState(getBlockFullState(sourceIndex));
					sourceIndex += firstStep;
				}
				sourceIndex = firstStart + secondStep;
			}
			sourceIndex = secondStart + thirdStep;
		}
	}

	@Override
	public void fillBlockComponentContainer(final BlockComponentContainer container) {
		synchronized (getBlockComponentOwners()) {
			container.setBlockComponentCount(getBlockComponentOwners().size());
			getBlockComponentOwners().forEachEntry(new TShortObjectProcedure<BlockComponentOwner>() {
				@Override
				public boolean execute(short index, BlockComponentOwner component) {
					int x = NibbleQuadHashed.key1(index);
					int y = NibbleQuadHashed.key2(index);
					int z = NibbleQuadHashed.key3(index);
					container.setBlockComponent(x, y, z, component);
					return true;
				}
			});
		}
	}

	@Override
	public SpoutChunkSnapshot getSnapshot(SnapshotType type, EntityType entities, ExtraData data) {
		return getSnapshot(type, entities, data, false);
	}

	public SpoutChunkSnapshot getSnapshot(SnapshotType type, EntityType entities, ExtraData data, boolean palette) {
		checkChunkLoaded();
		short[] blockIds = null, blockData = null;
		CuboidLightBuffer[] lightBuffersCopy = null;
		switch (type) {
			case NO_BLOCK_DATA:
				break;
			case BLOCK_IDS_ONLY:
				blockIds = blockStore.getBlockIdArray();
				break;
			case BLOCKS_ONLY:
				blockIds = blockStore.getBlockIdArray();
				blockData = blockStore.getDataArray();
				break;
			case LIGHT_ONLY:
				lightBuffersCopy = copyLightBuffers();
				break;
			case BOTH:
				blockIds = blockStore.getBlockIdArray();
				blockData = blockStore.getDataArray();
				lightBuffersCopy = copyLightBuffers();
				break;
		}

		if (palette) {
			return new SpoutChunkSnapshot(this, blockStore.getPalette(), blockStore.getPackedWidth(), blockStore.getPackedArray(), lightBuffersCopy, entities, data);
		} else {
			return new SpoutChunkSnapshot(this, blockIds, blockData, lightBuffersCopy, entities, data);
		}
	}

	@Override
	public Future<ChunkSnapshot> getFutureSnapshot() {
		return getFutureSnapshot(SnapshotType.BOTH, EntityType.NO_ENTITIES, ExtraData.NO_EXTRA_DATA);
	}

	@Override
	public Future<ChunkSnapshot> getFutureSnapshot(SnapshotType type, EntityType entities, ExtraData data) {
		SpoutChunkSnapshotFuture future = new SpoutChunkSnapshotFuture(this, type, entities, data);
		parentRegion.addSnapshotFuture(future);
		return future;
	}

	@Override
	public boolean refreshObserver(Entity entity) {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("Cannot refresh observers when not in server mode!");
		}
		TickStage.checkStage(TickStage.FINALIZE);
		checkChunkLoaded();

		if (!isPopulated()) {
			queueForPopulation(false);
		}
		boolean wasEmpty = observers.isEmpty();
		if (observers.add((SpoutEntity) entity) && (entity instanceof SpoutPlayer)) {
			observingPlayers.add((SpoutPlayer) entity);
			if (wasEmpty) {
				RegionGenerator generator = getRegion().getRegionGenerator();
				if (generator != null) {
					generator.touchChunkNeighbors(this);
				}
			}
		}
		SaveState.resetPostSaving(saveState);
		return true;
	}

	@Override
	public boolean removeObserver(Entity entity) {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("Cannot remove observers when not in server mode!");
		}
		TickStage.checkStage(TickStage.FINALIZE);
		checkChunkLoaded();

		if (observers.remove(entity) && (entity instanceof SpoutPlayer)) {
			observingPlayers.remove(entity);
		}
		expiredObserversQueue.add((SpoutEntity) entity);
		if (!isObserved()) {
			this.unloadQueueElement.add();
		}
		return true;
	}

	@ServerOnly
	public boolean isObserved() {
		return !observers.isEmpty();
	}

	@Override
	@ServerOnly
	public int getNumObservers() {
		return observers.size();
	}

	@Override
	public Set<SpoutPlayer> getObservingPlayers() {
		return unmodifiableObservingPlayers;
	}

	@Override
	public Set<SpoutEntity> getObservers() {
		return unmodifiableObservers;
	}

	/**
	 * Gets observers that have expired during the most recent tick
	 *
	 * @return the expired observers
	 */
	public Set<SpoutEntity> getExpiredObservers() {
		return unmodifiableExpiredObservers;
	}

	public void updateExpiredObservers() {
		expiredObservers.clear();
		SpoutEntity e;
		while ((e = expiredObserversQueue.poll()) != null) {
			if (!observers.contains(e)) {
				expiredObservers.add(e);
			}
		}
	}

	public boolean compressIfRequired() {
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
		if (!blockStore.needsCompression()) {
			return false;
		}
		return compressRaw();
	}

	protected boolean compressRaw() {
		blockStore.compress();
		return true;
	}

	public void setLightDirty(boolean dirty) {
		lightDirty.set(dirty);
		if (dirty) { //To send to the renderer
			queueDirty();
		}
	}

	public boolean isLightDirty() {
		return lightDirty.get();
	}

	public boolean isBlockDirty() {
		return blockStore.isDirty();
	}

	public boolean isDirty() {
		if (lightDirty.get() || blockStore.isDirty()) {
			return true;
		} else {
			return false;
		}
	}

	public void lockStore() {
		blockStore.writeLock();
	}

	public void unlockStore() {
		blockStore.writeUnlock();
	}

	public boolean tryLockStore() {
		return blockStore.tryWriteLock();
	}

	public boolean isDirtyOverflow() {
		return blockStore.isDirtyOverflow();
	}

	protected IntVector3 getMaxDirty() {
		return blockStore.getMaxDirty();
	}

	protected IntVector3 getMinDirty() {
		return blockStore.getMinDirty();
	}

	protected Vector3f getDirtyBlock(int i) {
		return blockStore.getDirtyBlock(i);
	}

	public int getDirtyBlocks() {
		return blockStore.getDirtyBlocks();
	}

	public int getDirtyOldState(int i) {
		return blockStore.getDirtyOldState(i);
	}

	public int getDirtyNewState(int i) {
		return blockStore.getDirtyNewState(i);
	}

	public void resetDirtyArrays() {
		blockStore.resetDirtyArrays();
	}

	@Override
	public boolean isLoaded() {
		return !saveState.get().isUnloaded();
	}

	public void setUnloaded() {
		TickStage.checkStage(TickStage.SNAPSHOT);
		setUnloadedRaw(true);
	}

	/**
	 * This method should only be used for chunks which were unnecessarily loaded
	 */
	public void setUnloadedUnchecked() {
		setUnloadedRaw(false);
	}

	private void setUnloadedRaw(boolean saveColumn) {
		SaveState oldState = saveState.getAndSet(SaveState.UNLOADED);
		//Clear as much as possible to limit the damage of a potential leak
		this.blockStore = null;
		this.dataMap.clear();
		if (!oldState.isUnloaded()) {
			deregisterFromColumn(saveColumn);
		}
	}

	private void checkChunkLoaded() {
		if (saveState.get() == SaveState.UNLOADED) {
			throw new ChunkAccessException("Chunk has been unloaded");
		}
	}

	private void checkBlockStoreUpdateAllowed() {
		TickStage.checkStage(allowedStages, restrictedStages, getRegion().getExecutionThread());
	}

	@Override
	public void copySnapshot() {
	}

	public enum SaveState {
		UNLOAD_SAVE,
		UNLOAD,
		SAVE,
		NONE,
		SAVING,
		POST_SAVED,
		UNLOADED;

		public boolean isSave() {
			return this == SAVE || this == UNLOAD_SAVE;
		}

		public boolean isUnload() {
			return this == UNLOAD || this == POST_SAVED;
		}

		public boolean isPostUnload() {
			return this == SAVING;
		}

		public boolean isUnloaded() {
			return this == UNLOADED;
		}

		public static boolean cancelUnload(AtomicReference<SaveState> saveState) {
			boolean success = false;
			SaveState oldState = null;
			while (!success) {
				oldState = saveState.get();
				SaveState nextState;
				switch (oldState) {
					case UNLOAD_SAVE:
						nextState = SaveState.SAVE;
						break;
					case UNLOAD:
						nextState = SaveState.NONE;
						break;
					case POST_SAVED:
						nextState = SaveState.NONE;
						break;
					case SAVE:
						nextState = SaveState.SAVE;
						break;
					case NONE:
						nextState = SaveState.NONE;
						break;
					case UNLOADED:
						nextState = SaveState.UNLOADED;
						break;
					case SAVING:
						nextState = SaveState.NONE;
						break;
					default:
						throw new IllegalStateException("Unknown save state: " + oldState);
				}
				success = saveState.compareAndSet(oldState, nextState);
			}
			return oldState != SaveState.UNLOADED;
		}

		public static void unload(AtomicReference<SaveState> saveState, boolean save) {
			boolean success = false;
			while (!success) {
				SaveState state = saveState.get();
				SaveState nextState;
				switch (state) {
					case UNLOAD_SAVE:
						nextState = SaveState.UNLOAD_SAVE;
						break;
					case UNLOAD:
						nextState = save ? SaveState.UNLOAD_SAVE : SaveState.UNLOAD;
						break;
					case POST_SAVED:
						nextState = save ? SaveState.UNLOAD_SAVE : SaveState.POST_SAVED;
						break;
					case SAVE:
						nextState = SaveState.UNLOAD_SAVE;
						break;
					case NONE:
						nextState = save ? SaveState.UNLOAD_SAVE : SaveState.UNLOAD;
						break;
					case UNLOADED:
						nextState = SaveState.UNLOADED;
						break;
					case SAVING:
						nextState = SaveState.SAVING;
						break;
					default:
						throw new IllegalStateException("Unknown save state: " + state);
				}
				success = saveState.compareAndSet(state, nextState);
			}
		}

		public static void save(AtomicReference<SaveState> saveState) {
			boolean success = false;
			while (!success) {
				SaveState state = saveState.get();
				SaveState nextState;
				switch (state) {
					case UNLOAD_SAVE:
						nextState = SaveState.UNLOAD_SAVE;
						break;
					case UNLOAD:
						nextState = SaveState.UNLOAD_SAVE;
						break;
					case POST_SAVED:
						nextState = SaveState.UNLOAD_SAVE;
						break;
					case SAVE:
						nextState = SaveState.SAVE;
						break;
					case NONE:
						nextState = SaveState.SAVE;
						break;
					case UNLOADED:
						nextState = SaveState.UNLOADED;
						break;
					case SAVING:
						nextState = SaveState.SAVING;
						break;
					default:
						throw new IllegalStateException("Unknown save state: " + state);
				}
				success = saveState.compareAndSet(state, nextState);
			}
		}

		public static SaveState getAndResetSaveState(AtomicReference<SaveState> saveState) {
			boolean success = false;
			SaveState old = null;
			while (!success) {
				old = saveState.get();
				SaveState nextState;
				switch (old) {
					case UNLOAD_SAVE:
						nextState = SaveState.SAVING;
						break;
					case UNLOAD:
						nextState = SaveState.UNLOAD;
						break;
					case POST_SAVED:
						nextState = SaveState.POST_SAVED;
						break;
					case SAVE:
						nextState = SaveState.NONE;
						break;
					case NONE:
						nextState = SaveState.NONE;
						break;
					case UNLOADED:
						nextState = SaveState.UNLOADED;
						break;
					case SAVING:
						nextState = SaveState.SAVING;
						break;
					default:
						throw new IllegalStateException("Unknown save state: " + old);
				}
				success = saveState.compareAndSet(old, nextState);
			}
			return old;
		}

		public static void resetPostSaving(AtomicReference<SaveState> saveState) {
			saveState.compareAndSet(SaveState.SAVING, SaveState.NONE);
			saveState.compareAndSet(SaveState.POST_SAVED, SaveState.NONE);
		}

		public static void setPostSaved(AtomicReference<SaveState> saveState) {
			saveState.compareAndSet(SAVING, POST_SAVED);
		}
	}

	public static enum PopulationState {
		UNTOUCHED((byte) 0),
		CLEAR_POPULATED((byte) 1),
		POPULATED((byte) 2);
		private final byte id;
		private static final PopulationState[] BY_ID;

		static {
			PopulationState[] values = PopulationState.values();
			BY_ID = new PopulationState[values.length];
			int index = 0;
			for (PopulationState value : values) {
				BY_ID[index++] = value;
			}
		}

		private PopulationState(byte id) {
			this.id = id;
		}

		public byte getId() {
			return id;
		}

		public boolean incomplete() {
			return this == UNTOUCHED || this == CLEAR_POPULATED;
		}

		public static PopulationState byID(byte id) {
			if (id < 0 || id >= BY_ID.length) {
				return null;
			}
			return BY_ID[id];
		}
	}

	@Override
	public SpoutRegion getRegion() {
		return parentRegion;
	}

	@Override
	public boolean populate() {
		return populate(false);
	}

	@Override
	public void populate(boolean sync, boolean observe) {
		populate(sync, observe, false);
	}

	@Override
	public void populate(boolean sync, boolean observe, boolean priority) {
		if (observe) {
			this.popObserver.set(true);
		}
		if (sync) {
			if (populationState.get().incomplete()) {
				this.queueForPopulation(priority);
			}
		} else {
			populate(observe);
		}
	}

	@Override
	public boolean populate(boolean force) {
		if (!isObserved() && !force && !popObserver.get()) {
			return false;
		}

		popObserver.set(false);

		if (!populationState.get().incomplete() && !force) {
			return false;
		}

		final List<Populator> clearPopulators = new ArrayList<>();
		final List<Populator> populators = new ArrayList<>();
		for (Populator pop : getWorld().getGenerator().getPopulators()) {
			if (pop.needsClearance()) {
				clearPopulators.add(pop);
			} else {
				populators.add(pop);
			}
		}

		final int x = getX();
		final int y = getY();
		final int z = getZ();

		if (!clearPopulators.isEmpty()) {
			final SpoutChunk[] toPopulate = new SpoutChunk[9];
			int index = 0;
			for (byte xx = -1; xx <= 1; xx++) {
				for (byte zz = -1; zz <= 1; zz++) {
					final SpoutChunk chunk = getWorld().getChunk(x + xx, y, z + zz, LoadOption.LOAD_GEN);
					if (chunk.getPopulationState() == PopulationState.UNTOUCHED) {
						toPopulate[index++] = chunk;
					}
				}
			}
			for (Populator populator : clearPopulators) {
				try {
					for (index = 0; index < toPopulate.length; index++) {
						final SpoutChunk chunk = toPopulate[index];
						if (chunk != null) {
							chunk.populate(populator);
						}
					}
				} catch (Exception e) {
					Spout.getEngine().getLogger().log(Level.SEVERE, "Could not populate Chunk with " + populator.toString());
					e.printStackTrace();
				}
			}
			for (index = 0; index < toPopulate.length; index++) {
				final SpoutChunk chunk = toPopulate[index];
				if (chunk != null) {
					chunk.setPopulationState(PopulationState.CLEAR_POPULATED);
				}
			}
		}

		final Random random = new Random(WorldGeneratorUtils.getSeed(getWorld(), x, y, z, 42));
		for (Populator populator : populators) {
			try {
				long time = -bean.getCurrentThreadCpuTime();
				populator.populate(this, random);
				time += bean.getCurrentThreadCpuTime();
				populatorAdd(populator, time);
			} catch (Exception e) {
				Spout.getEngine().getLogger().log(Level.SEVERE, "Could not populate Chunk with " + populator.toString());
				e.printStackTrace();
			}
		}
		populated.incrementAndGet();

		populationState.set(PopulationState.POPULATED);

		notifyColumn();
		parentRegion.onChunkPopulated(this);
		resetDynamicBlocks();
		setModified();
		return true;
	}

	public void populate(Populator populator) {
		try {
			long time = -bean.getCurrentThreadCpuTime();
			populator.populate(this, new Random(WorldGeneratorUtils.getSeed(getWorld(), getX(), getY(), getZ(), 42)));
			time += bean.getCurrentThreadCpuTime();
			populatorAdd(populator, time);
		} catch (Exception e) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Could not populate Chunk with " + populator.toString());
			e.printStackTrace();
		}
	}

	private final static ConcurrentHashMap<Class<? extends Populator>, AtomicLong> populatorProfilerMap = new ConcurrentHashMap<>();
	private final static ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	private final static AtomicInteger populated = new AtomicInteger();

	private void populatorAdd(Populator populator, long delta) {
		AtomicLong i = populatorProfilerMap.get(populator.getClass());
		if (i == null) {
			i = new AtomicLong();
			AtomicLong oldCounter = populatorProfilerMap.putIfAbsent(populator.getClass(), i);
			if (oldCounter != null) {
				i = oldCounter;
			}
		}
		i.addAndGet(delta);
	}

	private final static Comparator<Entry<Class<? extends Populator>, Long>> comp = new Comparator<Entry<Class<? extends Populator>, Long>>() {
		@Override
		public int compare(Entry<Class<? extends Populator>, Long> o1, Entry<Class<? extends Populator>, Long> o2) {
			if (o1.getValue() > o2.getValue()) {
				return 1;
			} else if (o1.getValue() < o2.getValue()) {
				return -1;
			} else {
				return 0;
			}
		}
	};

	public static List<Entry<Class<? extends Populator>, Long>> getProfileResults() {

		List<Entry<Class<? extends Populator>, Long>> list = new ArrayList<>(populatorProfilerMap.size());

		for (Entry<Class<? extends Populator>, AtomicLong> e : populatorProfilerMap.entrySet()) {
			list.add(new AbstractMap.SimpleEntry<Class<? extends Populator>, Long>(e.getKey(), e.getValue().get()));
		}

		Collections.sort(list, comp);

		return list;
	}

	public static int getChunksPopulated() {
		return populated.get();
	}

	@Override
	public boolean isPopulated() {
		return populationState.get() == PopulationState.POPULATED;
	}

	public PopulationState getPopulationState() {
		return populationState.get();
	}

	public void setPopulationState(PopulationState state) {
		populationState.set(state);
	}

	public void queueForPopulation(boolean priority) {
		if (!priority) {
			populationQueueElement.add();
		} else {
			populationPriorityQueueElement.add();
		}
	}

	@Override
	public List<Entity> getEntities() {
		ArrayList<Entity> entities = new ArrayList<>();
		for (Entity e : parentRegion.getEntityManager().getAll()) {
			if (e.getChunk() == this) {
				entities.add(e);
			}
		}
		return Collections.unmodifiableList(entities);
	}

	@Override
	public List<Entity> getLiveEntities() {
		ArrayList<Entity> entities = new ArrayList<>();
		for (Entity e : parentRegion.getEntityManager().getAllLive()) {
			if (((SpoutEntity) e).getChunkLive() == this) {
				entities.add(e);
			}
		}
		return entities;
	}

	public boolean hasEntities() {
		for (Entity e : parentRegion.getEntityManager().getAllLive()) {
			if (((SpoutEntity) e).getChunkLive() == this) {
				return true;
			}
		}
		return false;
	}

	public void deregisterFromColumn(boolean save) {
		if (columnRegistered.compareAndSet(true, false)) {
			column.deregisterChunk(save);
		} else {
			throw new IllegalStateException("Chunk at " + getX() + ", " + getZ() + " deregistered from column more than once");
		}
	}

	public boolean isReapable() {
		if (Spout.getPlatform() == Platform.SERVER) {
			return isReapable(getWorld().getAge());
		} else {
			return false;
		}
	}

	private boolean isReapable(long worldAge) {
		if (lastUnloadCheck.get() + SpoutConfiguration.CHUNK_REAP_DELAY.getLong() >= worldAge) {
			return false;
		}

		lastUnloadCheck.set(worldAge);
		return !isObserved();
	}

	public void notifyColumn() {
		for (int x = 0; x < BLOCKS.SIZE; x++) {
			for (int z = 0; z < BLOCKS.SIZE; z++) {
				notifyColumn(x, z);
			}
		}
	}

	private void notifyColumn(int x, int z) {
		if (columnRegistered.get()) {
			column.notifyChunkAdded(this, x, z);
		}
	}

	@Override
	public String toString() {
		return "SpoutChunk{ (" + getX() + ", " + getY() + ", " + getZ() + ") }";
	}

	public static class ChunkAccessException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ChunkAccessException(String message) {
			super(message);
		}
	}

	/**
	 * Not thread-safe, must synchronize on access
	 *
	 * @return block components
	 */
	public TShortObjectMap<BlockComponentOwner> getBlockComponentOwners() {
		return blockComponents;
	}

	/**
	 * Scans for block components.  This method must ONLY be called during load from disk
	 */
	public void blockComponentScan() {
		for (int dx = 0; dx < Chunk.BLOCKS.SIZE; dx++) {
			for (int dy = 0; dy < Chunk.BLOCKS.SIZE; dy++) {
				for (int dz = 0; dz < Chunk.BLOCKS.SIZE; dz++) {
					BlockMaterial bm = getBlockMaterial(dx, dy, dz);
					short packed = NibbleQuadHashed.key(dx, dy, dz, 0);
					//Does not need synchronized, the chunk is not yet accessible outside this thread
					BlockComponentOwner get = getBlockComponentOwners().get(packed);
					if (get == null) {
						get = new BlockComponentOwner(getDataMap(), dx + getBlockX(), dy + getBlockY(), dz + getBlockZ(), getWorld());
						getBlockComponentOwners().put(packed, get);
					}
					for (Class<? extends BlockComponent> c : bm.getComponents()) {
						get.add(c);
					}
				}
			}
		}
	}

	private int getAndSetBlock(int x, int y, int z, short newId, short newData) {
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		synchronized (blockComponents) {
			int oldState = blockStore.getAndSetBlock(x, y, z, newId, newData);
			if (newId != BlockFullState.getId(oldState)) {//Only try to change if they aren't the same id
				BlockMaterial newMaterial = MaterialRegistry.get(BlockFullState.getPacked(newId, newData));
				short packed = NibbleQuadHashed.key(x, y, z, 0);
				BlockComponentOwner oldHolder = blockComponents.remove(packed);//All components get reset, always
				if (oldHolder != null) {
					for (Component c : oldHolder.values()) {
						oldHolder.detach(c.getClass());//Detach if possible
					}
				}
				if (!newMaterial.getComponents().isEmpty()) {
					BlockComponentOwner newHolder = new BlockComponentOwner(getDataMap(), x + getBlockX(), y + getBlockY(), z + getBlockZ(), getWorld());
					blockComponents.put(packed, newHolder);
					for (Class<? extends BlockComponent> c : newMaterial.getComponents()) {
						newHolder.add(c);
					}
				}
			}
			return oldState;
		}
	}

	public BlockComponentOwner getBlockComponentOwner(int x, int y, int z, boolean create) {
		synchronized (blockComponents) {
			short packed = NibbleQuadHashed.key(x, y, z, 0);
			BlockComponentOwner value = blockComponents.get(packed);
			if (value == null && create) {
				value = new BlockComponentOwner(getDataMap(), NibbleQuadHashed.key1(packed), NibbleQuadHashed.key2(packed), NibbleQuadHashed.key3(packed), getWorld());
				blockComponents.put(packed, value);
			}
			return value;
		}
	}

	protected void tickBlockComponents(float dt) {
		synchronized (blockComponents) {
			procedure.dt = dt;
			blockComponents.forEachValue(procedure);
		}
	}

	private final BlockComponentTickProcedure procedure = new BlockComponentTickProcedure();

	private static class BlockComponentTickProcedure implements TObjectProcedure<BlockComponentOwner> {
		private float dt;

		@Override
		public boolean execute(BlockComponentOwner component) {
			for (Component c : component.values()) {
				try {
					if (c.canTick()) {
						c.tick(dt);
					}
				} catch (Exception e) {
					Spout.getLogger().log(Level.SEVERE, "Unhandled exception while ticking block component", e);
				}
			}
			return true;
		}
	}

	@Override
	public Block getBlock(float x, float y, float z) {
		return getBlock(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
	}

	@Override
	public Block getBlock(Vector3f position) {
		return getBlock(position.getX(), position.getY(), position.getZ());
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return new SpoutBlock(this.getWorld(), getBlockX(x), getBlockY(y), getBlockZ(z), this);
	}

	@Override
	public boolean compareAndSetData(int bx, int by, int bz, int expect, short data, Cause<?> cause) {
		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		bx &= BLOCKS.MASK;
		by &= BLOCKS.MASK;
		bz &= BLOCKS.MASK;

		short expId = BlockFullState.getId(expect);
		short expData = BlockFullState.getData(expect);

		boolean success = this.blockStore.compareAndSetBlock(bx & BLOCKS.MASK, by & BLOCKS.MASK, bz & BLOCKS.MASK, expId, expData, expId, data);
		if (success && expData != data) {
			blockChanged(bx, by, bz, expId, data, expId, expData, cause);
		}
		return success;
	}

	@Override
	public short setBlockDataBits(int bx, int by, int bz, int bits, boolean set, Cause<?> cause) {
		if (set) {
			return this.setBlockDataBits(bx, by, bz, bits, cause);
		} else {
			return this.clearBlockDataBits(bx, by, bz, bits, cause);
		}
	}

	@Override
	public short setBlockDataBits(int bx, int by, int bz, int bits, Cause<?> cause) {
		return (short) setBlockDataFieldRaw(bx, by, bz, bits & 0xFFFF, 0xFFFF, cause);
	}

	@Override
	public short clearBlockDataBits(int bx, int by, int bz, int bits, Cause<?> cause) {
		return (short) setBlockDataFieldRaw(bx, by, bz, bits & 0xFFFF, 0x0000, cause);
	}

	@Override
	public int getBlockDataField(int bx, int by, int bz, int bits) {
		checkChunkLoaded();

		bx &= BLOCKS.MASK;
		by &= BLOCKS.MASK;
		bz &= BLOCKS.MASK;

		int shift = shiftCache[bits];
		int state = this.blockStore.getFullData(bx, by, bz);
		short data = BlockFullState.getData(state);

		return (data & bits) >> (shift);
	}

	@Override
	public int setBlockDataField(int bx, int by, int bz, int bits, int value, Cause<?> cause) {
		int oldData = setBlockDataFieldRaw(bx, by, bz, bits, value, cause);

		int shift = shiftCache[bits];

		return (oldData & bits) >> shift;
	}

	@Override
	public int addBlockDataField(int bx, int by, int bz, int bits, int value, Cause<?> cause) {
		int oldData = addBlockDataFieldRaw(bx, by, bz, bits, value, cause);

		int shift = shiftCache[bits];

		return (oldData & bits) >> shift;
	}

	@Override
	public boolean isBlockDataBitSet(int bx, int by, int bz, int bits) {
		return getBlockDataField(bx, by, bz, bits) != 0;
	}

	protected int setBlockDataFieldRaw(int bx, int by, int bz, int bits, int value, Cause<?> cause) {
		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		bx &= BLOCKS.MASK;
		by &= BLOCKS.MASK;
		bz &= BLOCKS.MASK;

		int shift = shiftCache[bits];

		value &= 0xFFFF;

		boolean updated = false;

		boolean success = false;
		short oldData = 0;
		short oldId = 0;
		short newData = 0;
		short newId = 0;
		while (!success) {
			int state = this.blockStore.getFullData(bx, by, bz);
			oldData = BlockFullState.getData(state);
			oldId = BlockFullState.getId(state);
			newData = (short) (((value << shift) & bits) | (oldData & ~bits));
			BlockMaterial bm = BlockMaterial.get(state);
			newId = bm.getId();

			Block block = getBlock(bx, by, bz);
			BlockChangeEvent blockEvent = new BlockChangeEvent(block, new BlockSnapshot(block, bm, newData), cause);
			Spout.getEngine().getEventManager().callEvent(blockEvent);
			if (!blockEvent.isCancelled()) {
				newId = blockEvent.getSnapshot().getMaterial().getId();
				newData = blockEvent.getSnapshot().getData();
				success = blockStore.compareAndSetBlock(bx, by, bz, oldId, oldData, newId, newData);
				updated = oldData != newData || oldId != newId;
			}
		}

		if (updated) {
			blockChanged(bx, by, bz, newId, newData, oldId, oldData, cause);
		}

		return oldData;
	}

	protected int addBlockDataFieldRaw(int bx, int by, int bz, int bits, int value, Cause<?> cause) {
		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		bx &= BLOCKS.MASK;
		by &= BLOCKS.MASK;
		bz &= BLOCKS.MASK;

		int shift = shiftCache[bits];

		value &= 0xFFFF;

		boolean updated = false;

		boolean success = false;
		short oldData = 0;
		short oldId = 0;
		short newData = 0;
		short newId = 0;
		while (!success) {
			int state = this.blockStore.getFullData(bx, by, bz);
			oldData = BlockFullState.getData(state);
			oldId = BlockFullState.getId(state);
			newData = (short) (((oldData + (value << shift)) & bits) | (oldData & ~bits));
			BlockMaterial bm = BlockMaterial.get(state);
			newId = bm.getId();

			Block block = getBlock(bx, by, bz);
			BlockChangeEvent blockEvent = new BlockChangeEvent(block, new BlockSnapshot(block, bm, newData), cause);
			Spout.getEngine().getEventManager().callEvent(blockEvent);
			if (!blockEvent.isCancelled()) {
				newId = blockEvent.getSnapshot().getMaterial().getId();
				newData = blockEvent.getSnapshot().getData();
				success = blockStore.compareAndSetBlock(bx, by, bz, oldId, oldData, newId, newData);
				updated = oldData != newData || oldId != newId;
			}
		}

		if (updated) {
			blockChanged(bx, by, bz, newId, newData, oldId, oldData, cause);
		}

		return oldData;
	}

	private void blockChanged(int x, int y, int z, short newId, short newData, short oldId, short oldData, Cause<?> cause) {
		BlockMaterial newMaterial = (BlockMaterial) MaterialRegistry.get(newId).getSubMaterial(newData);
		BlockMaterial oldMaterial = (BlockMaterial) MaterialRegistry.get(oldId).getSubMaterial(oldData);
		if (oldMaterial == null) {
			oldMaterial = BlockMaterial.ERROR;
		}
		blockChanged(x, y, z, newMaterial, newData, oldMaterial, oldData, cause);
	}

	private void blockChanged(int x, int y, int z, BlockMaterial newMaterial, short newData, BlockMaterial oldMaterial, short oldData, Cause<?> cause) {
		// Add chunk to regions's dirty queue
		queueDirty();

		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		int rx = x + getBlockX();
		int ry = y + getBlockY();
		int rz = z + getBlockZ();

		if (Spout.getEngine().getPlatform() == Platform.CLIENT) {
			int maxBlock = Chunk.BLOCKS.SIZE - 1;
			if (x == 0) {
				SpoutChunk c = getRegion().getLocalChunk(this, -1, 0, 0, LoadOption.NO_LOAD);
				if (c != null) {
					c.touchBlock(maxBlock, y, z);
				}
			} else if (x == maxBlock) {
				SpoutChunk c = getRegion().getLocalChunk(this, +1, 0, 0, LoadOption.NO_LOAD);
				if (c != null) {
					c.touchBlock(0, y, z);
				}
			}
			if (y == 0) {
				SpoutChunk c = getRegion().getLocalChunk(this, 0, -1, 0, LoadOption.NO_LOAD);
				if (c != null) {
					c.touchBlock(x, maxBlock, z);
				}
			} else if (y == maxBlock) {
				SpoutChunk c = getRegion().getLocalChunk(this, 0, +1, 0, LoadOption.NO_LOAD);
				if (c != null) {
					c.touchBlock(x, 0, z);
				}
			}
			if (z == 0) {
				SpoutChunk c = getRegion().getLocalChunk(this, 0, 0, -1, LoadOption.NO_LOAD);
				if (c != null) {
					c.touchBlock(x, y, maxBlock);
				}
			} else if (z == maxBlock) {
				SpoutChunk c = getRegion().getLocalChunk(this, 0, 0, 1, LoadOption.NO_LOAD);
				if (c != null) {
					c.touchBlock(x, y, 0);
				}
			}
			dirtyChunkQueueElement.add();
		}

		// Handle onPlacement for dynamic materials
		if (newMaterial instanceof DynamicMaterial) {
			if (oldMaterial instanceof DynamicMaterial) {
				if (!oldMaterial.isCompatibleWith(newMaterial) || !newMaterial.isCompatibleWith(oldMaterial)) {
					parentRegion.resetDynamicBlock(rx, ry, rz);
				}
			} else {
				parentRegion.resetDynamicBlock(rx, ry, rz);
			}
		}

		// Only do physics when not populating
		if (this.isPopulated()) {
			EffectRange physicsRange = newMaterial.getPhysicsRange(newData);
			queueBlockPhysics(x, y, z, physicsRange, oldMaterial);
			if (newMaterial != oldMaterial) {
				EffectRange destroyRange = oldMaterial.getDestroyRange(oldData);
				if (destroyRange != physicsRange) {
					queueBlockPhysics(x, y, z, destroyRange, oldMaterial);
				}
			}
		}

		int wy = y + this.getBlockY();
		column.notifyBlockChange(x, wy, z);

		setModified();
	}

	public ManagedHashMap getDataMap() {
		return dataMap;
	}

	public WeakReference<SpoutChunk> getWeakReference() {
		return selfReference;
	}

	protected void queueDirty() {
		dirtyChunkQueueElement.add();
	}

	protected void queueNew() {
		newChunkQueueElement.add();
	}

	int physicsUpdates = 0;

	public boolean runLocalPhysics() {
		scheduler.addUpdates(physicsUpdates);
		physicsUpdates = 0;
		SpoutWorld world = getWorld();

		boolean updated = false;
		updated |= physicsQueue.commitAsyncQueue();
		if (updated) {
			scheduler.addUpdates(1);
		}

		UpdateQueue queue = physicsQueue.getUpdateQueue();

		while (queue.hasNext()) {
			int x = queue.getX();
			int y = queue.getY();
			int z = queue.getZ();
			BlockMaterial oldMaterial = queue.getOldMaterial();
			if (!callOnUpdatePhysicsForRange(world, x, y, z, oldMaterial, false)) {
				physicsQueue.queueForUpdateMultiRegion(x, y, z, oldMaterial);
			}
		}
		return updated;
	}

	public void runGlobalPhysics() {
		scheduler.addUpdates(physicsUpdates);
		physicsUpdates = 0;
		SpoutWorld world = getWorld();

		UpdateQueue queue = physicsQueue.getMultiRegionQueue();

		while (queue.hasNext()) {
			int x = queue.getX();
			int y = queue.getY();
			int z = queue.getZ();
			BlockMaterial oldMaterial = queue.getOldMaterial();
			callOnUpdatePhysicsForRange(world, x, y, z, oldMaterial, true);
		}
	}

	private boolean callOnUpdatePhysicsForRange(World world, int x, int y, int z, BlockMaterial oldMaterial, boolean force) {
		int packed = getBlockFullState(x, y, z);
		BlockMaterial material = BlockFullState.getMaterial(packed);
		if (material.hasPhysics()) {
			short data = BlockFullState.getData(packed);
			if (!force && !material.getMaximumPhysicsRange(data).isRegionLocal(x, y, z)) {
				return false;
			}
			//switch region block coords (0-255) to world block coords
			SpoutRegion region = getRegion();
			Block block = world.getBlock(x + region.getBlockX(), y + region.getBlockY(), z + region.getBlockZ());
			block.getMaterial().onUpdate(oldMaterial, block);
			physicsUpdates++;
		}
		return true;
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		return getWorld().getBiome((x & BLOCKS.MASK) + this.getBlockX(), (y & BLOCKS.MASK) + this.getBlockY(), (z & BLOCKS.MASK) + this.getBlockZ());
	}

	protected void setAutosaveTicks(int ticks) {
		autosaveTicks.set(ticks);
	}

	protected int getAutosaveTicks() {
		return autosaveTicks.get();
	}

	/**
	 * Called when an entity enters the chunk. This method is NOT called for players. <p> This method occurs during finalizeRun
	 */
	public void onEntityEnter(SpoutEntity e) {
		entitiesModified.compareAndSet(false, true);
	}

	/**
	 * Called when an entity leaves the chunk. This method is NOT called for players. <p> This method occurs during finalizeRun
	 */
	public void onEntityLeave(SpoutEntity e) {
		entitiesModified.compareAndSet(false, true);
	}

	@Override
	public void setModified() {
		if (chunkModified.compareAndSet(false, true)) {
			setAutosaveTicks(new Random().nextInt(autosaveInterval * 2));
		}
	}

	public boolean isBlockUniform() {
		return blockStore.isBlockUniform();
	}

	public void addLightingBufferData(List<LightingManager<?>> lightManagers, List<byte[]> lightData) {
		for (int i = 0; i < lightData.size(); i++) {
			LightingManager<?> manager = lightManagers.get(i);
			byte[] data = lightData.get(i);
			CuboidLightBuffer buffer;
			buffer = manager.deserialize(this, getBlockX(), getBlockY(), getBlockZ(), Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, data);
			setIfAbsentLightBuffer(manager.getId(), buffer);
		}
	}

	public void setLightingBufferData(Map<Short, byte[]> lightData) {
		List<CuboidLightBuffer> list = new ArrayList<>();
		for (Entry<Short, byte[]> e : lightData.entrySet()) {
			LightingManager<?> manager = LightingRegistry.get(e.getKey());
			byte[] data = e.getValue();
			CuboidLightBuffer buffer = manager.deserialize(this, getBlockX(), getBlockY(), getBlockZ(), Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, data);
			list.add(buffer);
		}
		lightBuffers.set(list.toArray(new CuboidLightBuffer[0]));
	}

	@Override
	public CuboidLightBuffer getLightBuffer(short id) {
		//TickStage.checkStage(TickStage.LIGHTING);
		return setIfAbsentLightBuffer(id, null);
	}

	protected CuboidLightBuffer setIfAbsentLightBuffer(short id, CuboidLightBuffer buffer) {
		if (id < 0) {
			throw new IllegalArgumentException("Id must be positive");
		} else if (buffer != null && buffer.getManagerId() != id) {
			throw new IllegalArgumentException("Manager Id does not match buffer id");
		}

		CuboidLightBuffer[] array = lightBuffers.get();
		CuboidLightBuffer buf;
		if (id < array.length) {
			buf = array[id];
			if (buf != null) {
				return buf;
			}
		}

		CuboidLightBuffer newBuf;
		if (buffer == null) {
			LightingManager<?> manager = LightingRegistry.get(id);
			if (manager == null) {
				return null;
			}
			newBuf = manager.newLightBuffer(this, getBlockX(), getBlockY(), getBlockZ(), BLOCKS.SIZE, BLOCKS.SIZE, BLOCKS.SIZE);
		} else {
			newBuf = buffer;
		}

		boolean success = false;

		while (!success) {
			array = lightBuffers.get();
			if (id < array.length) {
				buf = array[id];
				if (buf != null) {
					return buf;
				}
			}
			CuboidLightBuffer[] newArray = new CuboidLightBuffer[Math.max(id + 1, array.length)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			newArray[id] = newBuf;
			success = lightBuffers.compareAndSet(array, newArray);
		}

		return newBuf;
	}

	protected CuboidLightBuffer[] getLightBuffers() {
		CuboidLightBuffer[] array = lightBuffers.get();
		ArrayList<CuboidLightBuffer> list = new ArrayList<>(array.length);
		for (int i = 0; i < array.length; i++) {
			CuboidLightBuffer b = array[i];
			if (b != null) {
				list.add(b);
			} else {
				System.out.println("Removed null LightBuffer");
			}
		}
		return list.toArray(lightBufferExample);
	}

	protected CuboidLightBuffer[] copyLightBuffers() {
		CuboidLightBuffer[] live = getLightBuffers();
		CuboidLightBuffer[] newArray = new CuboidLightBuffer[live.length];
		for (int i = 0; i < live.length; i++) {
			newArray[i] = live[i].copy();
		}
		return newArray;
	}

	private class ChunkSetQueueElement<T extends Cube> extends SetQueueElement<T> {
		private final boolean validIfUnloaded;

		public ChunkSetQueueElement(SetQueue<T> queue, T value) {
			this(queue, value, false);
		}

		public ChunkSetQueueElement(SetQueue<T> queue, T value, boolean validIfUnloaded) {
			super(queue, value);
			this.validIfUnloaded = validIfUnloaded;
		}

		@Override
		protected boolean isValid() {
			return validIfUnloaded || isLoaded();
		}
	}

	@Override
	public int getGenerationIndex() {
		return generationIndex;
	}

	protected void setGenerationIndex(int index) {
		this.generationIndex = index;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public <T extends CuboidLightBuffer> T getLightBuffer(LightingManager<T> manager) {
		return (T) getLightBuffer(manager.getId());
	}

	public int getRenderSequence() {
		return renderSequence.get();
	}

	private ChunkSnapshot getRenderSnapshot() {
		SpoutChunkSnapshot snapshot = renderSnapshotCache;
		if (snapshot != null) {
			return snapshot;
		}

		snapshot = getSnapshot(SnapshotType.BOTH, EntityType.NO_ENTITIES, ExtraData.NO_EXTRA_DATA);
		renderSnapshotCache = snapshot;
		return snapshot;
	}

	public void touchNeighborRender(BlockFaces neigbourstoUpdate) {
		for (BlockFace face : neigbourstoUpdate) {
			SpoutChunk chunk = getWorld().getChunk(getX() + face.getOffset().getFloorX(), getY() + face.getOffset().getFloorY(), getZ() + face.getOffset().getFloorZ(), LoadOption.NO_LOAD);
			if (chunk != null) {
				chunk.render(BlockFaces.NONE);
			}
		}
	}

	public static AtomicInteger meshesGenerated = new AtomicInteger();

	public void render() {
		render(BlockFaces.BTNSWE);
	}

	public void render(BlockFaces neigbourstoUpdate) {
		ChunkSnapshot[][][] chunks = new ChunkSnapshot[3][3][3];

		chunks[1][1][1] = getRenderSnapshot();

		for (BlockFace face : BlockFaces.BTNSWE) {
			SpoutChunk chunk = getWorld().getChunk(getX() + face.getOffset().getFloorX(), getY() + face.getOffset().getFloorY(), getZ() + face.getOffset().getFloorZ(), LoadOption.NO_LOAD);

			if (chunk == null) {
				continue;
			}

			chunks[face.getOffset().getFloorX() + 1][face.getOffset().getFloorY() + 1][face.getOffset().getFloorZ() + 1] = chunk.getRenderSnapshot();
		}

		boolean first = firstRender;
		//boolean first = enteredViewDistance();
		final HashSet<RenderMaterial> updatedRenderMaterials;

		// TODO restore original functionality
		if (true || first || isDirtyOverflow() || isLightDirty()) {
			updatedRenderMaterials = null;
			firstRender = false;
		} else {
			updatedRenderMaterials = new HashSet<>();
			int dirtyBlocks = getDirtyBlocks();
			for (int i = 0; i < dirtyBlocks; i++) {
				addMaterialToSet(updatedRenderMaterials, getDirtyOldState(i));
				addMaterialToSet(updatedRenderMaterials, getDirtyNewState(i));
			}
			int size = BLOCKS.SIZE;
			for (int i = 0; i < dirtyBlocks; i++) {
				Vector3f blockPos = getDirtyBlock(i);
				BlockMaterial material = getBlockMaterial(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ());
				ByteBitSet occlusion = material.getOcclusion(material.getData());
				for (BlockFace face : BlockFace.values()) {
					if (face.equals(BlockFace.THIS)) {
						continue;
					}
					if (occlusion.get(face)) {
						continue;
					}
					Vector3f neighborPos = blockPos.add(face.getOffset());
					int nx = neighborPos.getFloorX();
					int ny = neighborPos.getFloorX();
					int nz = neighborPos.getFloorX();
					if (nx >= 0 && ny >= 0 && nz >= 0 && nx < size && ny < size && nz < size) {
						int state = getBlockFullState(nx, ny, nz);
						addMaterialToSet(updatedRenderMaterials, state);
						//addSubMeshToSet(updatedSubMeshes, neighborPos);
					}
				}
			}
		}
		SpoutChunkSnapshotModel model = new SpoutChunkSnapshotModel(getWorld(), getX(), getY(), getZ(), chunks, 1, updatedRenderMaterials, first, System.currentTimeMillis());
		SpoutScheduler.addToQueue(model);

		touchNeighborRender(neigbourstoUpdate);
	}

	private static void addMaterialToSet(Set<RenderMaterial> set, int blockState) {
		BlockMaterial material = MaterialRegistry.get(blockState);
		if (material == null) {
			return;
		}
		set.add(material.getModel().getRenderMaterial());
	}

	@Override
	public void sync(NetworkComponent network) {
		if (!isDirtyOverflow() && !isLightDirty()) {
			for (int i = 0; true; i++) {
				Vector3f block = getDirtyBlock(i);
				if (block == null) {
					break;
				}

				try {
					network.callProtocolEvent(new BlockUpdateEvent(this, block.getFloorX(), block.getFloorY(), block.getFloorZ()));
				} catch (Exception e) {
					Spout.getEngine().getLogger().log(Level.SEVERE, "Exception thrown by plugin when attempting to send a block update");
				}
			}
		} else {
			network.callProtocolEvent(new ChunkSendEvent(this));
		}
	}

	@ClientOnly
	public void rawSetBlockStore(short[] blocks, short[] data) {
		if (Spout.getPlatform() != Platform.CLIENT) {
			throw new UnsupportedOperationException("Cannot raw set the block store unless in client mode.");
		}
		blockStore = new AtomicPaletteBlockStore(BLOCKS.BITS, false, false, 10, blocks, data);
		// Basically a new chunk, we want to rerender everything
		firstRender = true;
	}
}
