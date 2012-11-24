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

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.procedure.TShortObjectProcedure;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.component.ChunkComponentOwner;
import org.spout.api.component.components.BlockComponent;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.Entity;
import org.spout.api.event.Cause;
import org.spout.api.event.block.BlockChangeEvent;
import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.AreaBlockSource;
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
import org.spout.api.geo.cuboid.LightContainer;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.ComplexMaterial;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.material.block.BlockSnapshot;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.Platform;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.hashing.NibbleQuadHashed;
import org.spout.api.util.list.TNibbleQuadList;
import org.spout.api.util.map.concurrent.AtomicBlockStore;
import org.spout.api.util.map.concurrent.palette.AtomicPaletteBlockStore;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.thread.snapshotable.Snapshotable;
import org.spout.engine.world.physics.PhysicsQueue;
import org.spout.engine.world.physics.UpdateQueue;

import com.google.common.collect.Sets;

public class SpoutChunk extends Chunk implements Snapshotable {
	public static final WeakReference<Chunk> NULL_WEAK_REFERENCE = new WeakReference<Chunk>(null);
	
	//Not static to allow the engine to parse values first
	private final int autosaveInterval = SpoutConfiguration.AUTOSAVE_INTERVAL.getInt(60000);

	private final Set<SpoutEntity> observers = Sets.newSetFromMap(new ConcurrentHashMap<SpoutEntity, Boolean>());
	private final Set<SpoutEntity> unmodifiableObservers = Collections.unmodifiableSet(observers);
	private final Set<SpoutPlayer> observingPlayers = Sets.newSetFromMap(new ConcurrentHashMap<SpoutPlayer, Boolean>());
	private final Set<SpoutPlayer> unmodifiableObservingPlayers = Collections.unmodifiableSet(observingPlayers);
	private final ConcurrentLinkedQueue<SpoutEntity> expiredObserversQueue = new ConcurrentLinkedQueue<SpoutEntity>();
	private final LinkedHashSet<SpoutEntity> expiredObservers = new LinkedHashSet<SpoutEntity>();
	private final Set<SpoutEntity> unmodifiableExpiredObservers = Collections.unmodifiableSet(expiredObservers);

	/**
	 * Not thread safe, synchronize on access
	 */
	private final TShortObjectHashMap<BlockComponent> blockComponents = new TShortObjectHashMap<BlockComponent>();

	/**
	 * Multi-thread write access to the block store is only allowed during the
	 * allowed stages. During the restricted stages, only the region thread may
	 * modify the block store.
	 */
	private static final int restrictedStages = TickStage.PHYSICS | TickStage.DYNAMIC_BLOCKS;
	private static final int allowedStages = TickStage.STAGE1 | TickStage.STAGE2P | TickStage.TICKSTART | TickStage.GLOBAL_PHYSICS | TickStage.GLOBAL_DYNAMIC_BLOCKS;
	;
	/**
	 * Storage for block ids, data and auxiliary data. For blocks with data = 0
	 * and auxiliary data = null, the block is stored as a short.
	 */
	protected AtomicBlockStore blockStore;
	/**
	 * Indicates that the chunk should be saved if unloaded
	 */
	protected final AtomicReference<SaveState> saveState = new AtomicReference<SaveState>(SaveState.NONE);
	/**
	 * The parent region that manages this chunk
	 */
	protected final SpoutRegion parentRegion;
	/**
	 * Holds if the chunk is populated
	 */
	private final AtomicReference<PopulationState> populationState;
	/**
	 * Stores a short value of the sky light
	 * <p/>
	 * Note: These do not need to be thread-safe as long as only one thread (the
	 * region) is allowed to modify the values. If setters are provided, this
	 * will need to be made safe.
	 */
	protected byte[] skyLight;
	protected byte[] blockLight;
	/**
	 * The mask that should be applied to the x, y and z coords
	 */
	protected final SpoutColumn column;
	protected final AtomicBoolean columnRegistered = new AtomicBoolean(true);
	protected final AtomicLong lastUnloadCheck = new AtomicLong();
	/**
	 * True if this chunk is initializing lighting, False if not
	 */
	protected final AtomicBoolean isInitializingLighting = new AtomicBoolean(false);
	/**
	 * True if this chunk should be resent due to light calculations
	 */
	protected final AtomicBoolean lightDirty = new AtomicBoolean(false);
	/**
	 * True if this chunk mesh needs to be recalculated
	 */
	protected final AtomicBoolean renderDirty = new AtomicBoolean(false);
	/**
	 * If -1, there are no changes. If higher, there are changes and the number
	 * denotes how many ticks these have been there.<br> Every time a change is
	 * committed the value is set to 0. The region will increment it as well.
	 */
	protected final AtomicInteger lightingCounter = new AtomicInteger(-1);
	/**
	 * Contains the pending block light operations of blocks in this chunk
	 */
	protected final TNibbleQuadList blockLightOperations = new TNibbleQuadList();
	/**
	 * Contains the pending sky light operations of blocks in this chunk
	 */
	protected final TNibbleQuadList skyLightOperations = new TNibbleQuadList();
	/**
	 * Contains the pending block light updates of blocks in this chunk
	 */
	protected final TNibbleQuadList blockLightUpdates = new TNibbleQuadList();
	/**
	 * Contains the pending sky light updates of blocks in this chunk
	 */
	protected final TNibbleQuadList skyLightUpdates = new TNibbleQuadList();
	/**
	 * Data map and Datatable associated with it
	 */
	protected final ManagedHashMap dataMap;
	/**
	 * Shift cache array for shifting fields
	 */
	protected final static int[] shiftCache = new int[65536];
	/**
	 * The thread associated with the region
	 */
	private final Thread regionThread;
	
	/**
	 * The order that blocks are stored in the block store
	 */
	private static ContainerFillOrder STORE_FILL_ORDER = ContainerFillOrder.XZY;

	/**
	 * A set of all blocks in this chunk that need a physics update in the next
	 * tick. The coordinates in this set are relative to the <b>Region</b> containing the
	 * chunk.
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
	private final WeakReference<Chunk> selfReference;

	/**
	 * Indicates that the chunk has been added to the dirty queue
	 */
	private final AtomicBoolean dirtyQueued = new AtomicBoolean(false);

	private final AtomicBoolean populationQueued = new AtomicBoolean(false);

	private final AtomicInteger autosaveTicks = new AtomicInteger(0);
	
	private boolean wasInViewDistance = false;
	private boolean isInViewDistance = false;
	
	protected void setIsInViewDistance(boolean value){
		if (value && isBlockUniform() && getBlockMaterial(0, 0, 0) == BlockMaterial.AIR) {
			return;
		}
		isInViewDistance = value;
		if (wasInViewDistance != isInViewDistance) {
			setRenderDirty(true);
		}
	}
	
	public boolean enteredViewDistance() {
		return (!wasInViewDistance) && isInViewDistance;
	}

	public boolean isInViewDistance() {
		return isInViewDistance;
	}
	
	public boolean leftViewDistance() {
		return wasInViewDistance && (!isInViewDistance);
	}
	
	public void viewDistanceCopy() {
		wasInViewDistance = isInViewDistance;
	}
	
	static {
		for (int i = 0; i < shiftCache.length; i++) {
			int shift = 0;
			while ((i > 0) && (i >> shift) << shift == i) {
				shift++;
			}
			shiftCache[i] = shift - 1;
		}
	}

	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, short[] initial, ManagedHashMap map) {
		this(world, region, x, y, z, PopulationState.UNTOUCHED, initial, null, null, null, map);
	}
	
	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, PopulationState popState, int[] palette, int blockArrayWidth, int[] variableWidthBlockArray, byte[] skyLight, byte[] blockLight, ManagedHashMap extraData) {
		this(world, region, x, y, z, popState, extraData);
		blockStore = new AtomicPaletteBlockStore(BLOCKS.BITS, Spout.getEngine().getPlatform() == Platform.CLIENT, 10, palette, blockArrayWidth, variableWidthBlockArray);
		
		if (skyLight == null) {
			this.skyLight = new byte[BLOCKS.HALF_VOLUME];
		} else {
			this.skyLight = skyLight;
		}
		if (blockLight == null) {
			this.blockLight = new byte[BLOCKS.HALF_VOLUME];
		} else {
			this.blockLight = blockLight;
		}
		
		blockStore.resetDirtyArrays();
	}
	
	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, PopulationState popState, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, ManagedHashMap extraData) {
		this(world, region, x, y, z, popState, extraData);
		blockStore = new AtomicPaletteBlockStore(BLOCKS.BITS, Spout.getEngine().getPlatform() == Platform.CLIENT, 10, blocks, data);
		
		if (skyLight == null) {
			this.skyLight = new byte[BLOCKS.HALF_VOLUME];
		} else {
			this.skyLight = skyLight;
		}
		if (blockLight == null) {
			this.blockLight = new byte[BLOCKS.HALF_VOLUME];
		} else {
			this.blockLight = blockLight;
		}
		
		blockStore.resetDirtyArrays();
	}

	private SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, PopulationState popState, ManagedHashMap extraData) {
		super(world, x * BLOCKS.SIZE, y * BLOCKS.SIZE, z * BLOCKS.SIZE);
		parentRegion = region;
		this.populationState = new AtomicReference<PopulationState>(popState);

		if (extraData != null) {
			this.dataMap = extraData;
		} else {
			this.dataMap = new ManagedHashMap();
		}

		physicsQueue = new PhysicsQueue(this);

		column = world.getColumn(this.getBlockX(), this.getBlockZ(), true);
		column.registerChunk(((int) y) << BLOCKS.BITS);
		columnRegistered.set(true);
		lastUnloadCheck.set(world.getAge());
		// loaded chunk
		this.regionThread = region.getExceutionThread();
		selfReference = new WeakReference<Chunk>(this);
		this.scheduler = (SpoutScheduler) Spout.getScheduler();
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
		int bx = x & BLOCKS.MASK;
		int by = y & BLOCKS.MASK;
		int bz = z & BLOCKS.MASK;

		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		if (event) {
			// TODO - move to block change method?
			Block block = getBlock(bx, by, bz);
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
		
		int oldState;
		BlockMaterial oldMaterial;
		if (material instanceof ComplexMaterial) {
			oldState = getAndSetBlockLocked(bx, by, bz, newId, newData);
			oldMaterial = (BlockMaterial) MaterialRegistry.get(oldState);
		} else {
			boolean success = false;
			do {
				oldState = blockStore.getFullData(bx, by, bz);
				oldMaterial = (BlockMaterial) MaterialRegistry.get(oldState);
				if (oldMaterial instanceof ComplexMaterial) {
					oldState = getAndSetBlockLocked(bx, by, bz, newId, newData);
					success = true;
				} else {
					short oldId = BlockFullState.getId(oldState);
					short oldData = BlockFullState.getData(oldState);
					success = blockStore.compareAndSetBlock(bx, by, bz, oldId, oldData, newId, newData);
				}
			} while (!success);
		}
		
		short oldData = BlockFullState.getData(oldState);

		int oldheight = column.getSurfaceHeight(bx, bz);
		int wy = by + this.getBlockY();
		column.notifyBlockChange(bx, wy, bz);
		int wx = bx + this.getBlockX();
		int wz = bz + this.getBlockZ();
		int newheight = column.getSurfaceHeight(bx, bz);

		if (this.isPopulated()) {
			SpoutWorld world = this.getWorld();

			// Update block lighting
			if (!this.setBlockLight(wx, wy, wz, material.getLightLevel(data), cause)) {
				// if the light level is left unchanged, refresh lighting from
				// neighbors
				addBlockLightOperation(wx, wy, wz, SpoutWorldLighting.REFRESH);
			}

			// Update sky lighting
			if (newheight > oldheight) {
				// set sky light of blocks below to 0
				for (y = oldheight; y < newheight; y++) {
					world.setBlockSkyLight(wx, y + 1, wz, (byte) 0, cause);
				}
			} else if (newheight < oldheight) {
				// set sky light of blocks above to 15
				for (y = newheight; y < oldheight; y++) {
					world.setBlockSkyLight(wx, y + 1, wz, (byte) 15, cause);
				}
			} else {
				this.addSkyLightUpdates(x, y, z, 0);
				addSkyLightOperation(wx, wy, wz, SpoutWorldLighting.REFRESH);
			}
		}

		if (newState != oldState) {
			blockChanged(bx, by, bz, material, newData, oldMaterial, oldData, cause);
			return true;
		}
		return false;
	}
	
	/**
	 * This is always 'this', it is changed to a snapshot of the chunk in initLighting()
	 * Do NOT set this to something else or use it elsewhere but in initLighting()
	 */
	protected AreaBlockSource lightBlockSource = this;

	protected void addSkyLightOperation(int x, int y, int z, int operation) {
		SpoutWorldLightingModel model = this.getWorld().getLightingManager().getSkyModel();
		if (operation == SpoutWorldLighting.REFRESH) {
			if (!model.canRefresh(this.lightBlockSource, x, y, z)) {
				return;
			}
		} else if (operation == SpoutWorldLighting.GREATER) {
			if (!model.canGreater(this.lightBlockSource, x, y, z)) {
				return;
			}
		}
		synchronized (this.skyLightOperations) {
			if (this.skyLightOperations.isEmpty()) {
				// Let the lighting manager know this chunk requires a lighting update
				this.getWorld().getLightingManager().addChunk(this.getX(), this.getY(), this.getZ());
			}
			this.skyLightOperations.add(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK, operation);
		}
	}

	protected void addBlockLightOperation(int x, int y, int z, int operation) {
		SpoutWorldLightingModel model = this.getWorld().getLightingManager().getBlockModel();
		if (operation == SpoutWorldLighting.REFRESH) {
			if (!model.canRefresh(this.lightBlockSource, x, y, z)) {
				return;
			}
		} else if (operation == SpoutWorldLighting.GREATER) {
			if (!model.canGreater(this.lightBlockSource, x, y, z)) {
				return;
			}
		}
		synchronized (this.blockLightOperations) {
			if (this.blockLightOperations.isEmpty()) {
				// Let the lighting manager know this chunk requires a lighting update
				this.getWorld().getLightingManager().addChunk(this.getX(), this.getY(), this.getZ());
			}
			this.blockLightOperations.add(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK, operation);
		}
	}
	
	protected void addSkyLightUpdates(int x, int y, int z, int level) {
		synchronized (this.skyLightUpdates) {
			if (this.skyLightUpdates.isEmpty()) {
				// Let the lighting manager know this chunk requires a lighting update
				this.getWorld().getLightingManager().addChunk(this.getX(), this.getY(), this.getZ());
			}
			this.skyLightUpdates.add(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK, level);
		}
	}

	protected void addBlockLightUpdates(int x, int y, int z, int level) {
		synchronized (this.blockLightUpdates) {
			if (this.blockLightUpdates.isEmpty()) {
				// Let the lighting manager know this chunk requires a lighting update
				this.getWorld().getLightingManager().addChunk(this.getX(), this.getY(), this.getZ());
			}
			this.blockLightUpdates.add(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK, level);
		}
	}


	@Override
	public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		Vector3 base = buffer.getBase();
		setCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer, cause);
	}

	@Override
	public void setCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		blockStore.writeLock();
		try {
			Vector3 size = buffer.getSize();
			
			int startX = Math.max(bx, this.getBlockX());
			int startY = Math.max(by, this.getBlockY());
			int startZ = Math.max(bz, this.getBlockZ());

			int endX = Math.min(bx + size.getFloorX(), this.getBlockX() + BLOCKS.SIZE - 1);
			int endY = Math.min(by + size.getFloorY(), this.getBlockY() + BLOCKS.SIZE - 1);
			int endZ = Math.min(bz + size.getFloorZ(), this.getBlockZ() + BLOCKS.SIZE - 1);
			
			Vector3 base = buffer.getBase();
			
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
	
	@Override
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz) {
		CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(bx, by, bz, sx, sy, sz);
		getCuboid(bx, by, bz, buffer);
		return buffer;
	}
	
	@Override
	public void getCuboid(CuboidBlockMaterialBuffer buffer) {
		Vector3 base = buffer.getBase();
		getCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer);
	}

	@Override
	public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer) {
		blockStore.writeLock();
		try {
			Vector3 size = buffer.getSize();

			int startX = Math.max(bx, this.getBlockX());
			int startY = Math.max(by, this.getBlockY());
			int startZ = Math.max(bz, this.getBlockZ());

			int endX = Math.min(bx + size.getFloorX(), this.getBlockX() + BLOCKS.SIZE - 1);
			int endY = Math.min(by + size.getFloorY(), this.getBlockY() + BLOCKS.SIZE - 1);
			int endZ = Math.min(bz + size.getFloorZ(), this.getBlockZ() + BLOCKS.SIZE - 1);
			
			Vector3 base = buffer.getBase();
			
			int offX = bx - base.getFloorX();
			int offY = by - base.getFloorY();
			int offZ = bz - base.getFloorZ();
			
			for (int dx = startX; dx < endX; dx++) {
				for (int dy = startY; dy < endY; dy++) {
					for (int dz = startZ; dz < endZ; dz++) {
						int packed = this.getBlockFullState(dx, dy, dz);
						buffer.set(dx - offX, dy - offY, dz - offZ, BlockFullState.getMaterial(packed), BlockFullState.getData(packed));
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
	public void syncResetDynamicBlock(int x, int y, int z) {
		parentRegion.syncResetDynamicBlock(getBlockX(x), getBlockY(y), getBlockZ(z));
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data) {
		return parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z), nextUpdate, data);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate) {
		return parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z), nextUpdate);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z) {
		return parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z));
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

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Cause<?> cause) {
		addBlockLightUpdates(x, y, z, light);
		return true;
	}
	
	public boolean setBlockLightSync(int x, int y, int z, byte light, Cause<?> cause) {
		light &= 0xF;
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		checkChunkLoaded();
		int index = getBlockIndex(x, y, z);
		byte oldLight;
		if ((index & 1) == 1) {
			index = index >> 1;
			oldLight = NibblePairHashed.key1(blockLight[index]);
			blockLight[index] = NibblePairHashed.setKey1(blockLight[index], light);
		} else {
			index = index >> 1;
			oldLight = NibblePairHashed.key2(blockLight[index]);
			blockLight[index] = NibblePairHashed.setKey2(blockLight[index], light);
		}
		if (light > oldLight) {
			// light increased
			this.addBlockLightOperation(x, y, z, SpoutWorldLighting.GREATER);
		} else if (light < oldLight) {
			// light decreased
			this.addBlockLightOperation(x, y, z, SpoutWorldLighting.LESSER);
		} else {
			return false;
		}
		this.notifyLightChange();
		this.setModified();
		return true;
	}

	@Override
	public byte getBlockLight(int x, int y, int z) {
		checkChunkLoaded();
		int index = getBlockIndex(x, y, z);
		byte light = blockLight[index >> 1];
		if ((index & 1) == 1) {
			return NibblePairHashed.key1(light);
		} else {
			return NibblePairHashed.key2(light);
		}
	}

	@Override
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Cause<?> cause) {
		addSkyLightUpdates(x, y, z, light);
		return true;
	}
	
	public boolean setBlockSkyLightSync(int x, int y, int z, byte light, Cause<?> cause) {
		light &= 0xF;
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		checkChunkLoaded();
		int index = getBlockIndex(x, y, z);
		byte oldLight;
		if ((index & 1) == 1) {
			index = index >> 1;
			oldLight = NibblePairHashed.key1(skyLight[index]);
			skyLight[index] = NibblePairHashed.setKey1(skyLight[index], light);
		} else {
			index = index >> 1;
			oldLight = NibblePairHashed.key2(skyLight[index]);
			skyLight[index] = NibblePairHashed.setKey2(skyLight[index], light);
		}

		if (light > oldLight) {
			// light increased
			this.addSkyLightOperation(x, y, z, SpoutWorldLighting.GREATER);
		} else if (light < oldLight) {
			// light decreased
			this.addSkyLightOperation(x, y, z, SpoutWorldLighting.LESSER);
		} else {
			return false;
		}
		this.notifyLightChange();
		this.setModified();
		return true;
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		int light = this.getBlockSkyLightRaw(x, y, z) - (15 - this.getWorld().getSkyLight());
		return light < 0 ? (byte) 0 : (byte) light;
	}

	@Override
	public byte getBlockSkyLightRaw(int x, int y, int z) {
		checkChunkLoaded();
		int index = getBlockIndex(x, y, z);
		return getBlockSkyLightRaw(index);
	}
	
	protected byte getBlockSkyLightRaw(int index) {
		byte light = skyLight[index >> 1];
		if ((index & 1) == 1) {
			return NibblePairHashed.key1(light);
		} else {
			return NibblePairHashed.key2(light);
		}
	}
	
	protected byte getBlockBlockLightRaw(int index) {
		byte light = blockLight[index >> 1];
		if ((index & 1) == 1) {
			return NibblePairHashed.key1(light);
		} else {
			return NibblePairHashed.key2(light);
		}
	}

	private void notifyLightChange() {
		if (this.lightingCounter.getAndSet(0) == -1) {
			this.parentRegion.reportChunkLightDirty(this.getX(), this.getY(), this.getZ());
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
		physicsQueue.queueForUpdate(rx, ry, rz, null);
	}

	private int getBlockIndex(int x, int y, int z) {
		return (y & BLOCKS.MASK) << 8 | (z & BLOCKS.MASK) << 4 | (x & BLOCKS.MASK);
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
		(parentRegion).markForSaveUnload(this);
	}

	public void saveComplete() {
		if (isObserved()) {
			SaveState.resetPostSaving(saveState);
		} else {
			SaveState.setPostSaved(saveState);
		}
		parentRegion.markForSaveUnload(this);
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
	public void fillSkyLightContainer(LightContainer container) {
		fillLightContainerRaw(container, false);
	}
	
	@Override
	public void fillBlockLightContainer(LightContainer container) {
		fillLightContainerRaw(container, true);
	}
	
	public void fillLightContainerRaw(LightContainer container, boolean blockLight) {
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
					if (blockLight) {
						container.setLightLevel(getBlockBlockLightRaw(sourceIndex));
					} else {
						container.setLightLevel(getBlockSkyLightRaw(sourceIndex));
					}
					sourceIndex += firstStep;
				}
				sourceIndex = firstStart + secondStep;
			}
			sourceIndex = secondStart + thirdStep;
		}
	}

	@Override
	public void fillBlockComponentContainer(final BlockComponentContainer container) {
		synchronized(getBlockComponents()) {
			final int bx = getBlockX();
			final int by = getBlockY();
			final int bz = getBlockZ();
			container.setBlockComponentCount(getBlockComponents().size());
			getBlockComponents().forEachEntry(new TShortObjectProcedure<BlockComponent>() {
				@Override
				public boolean execute(short index, BlockComponent component) {
					int x = NibbleQuadHashed.key1(index) + bx;
					int y = NibbleQuadHashed.key2(index) + by;
					int z = NibbleQuadHashed.key3(index) + bz;
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
		byte[] blockLightCopy = null, skyLightCopy = null;
		short[] blockIds = null, blockData = null;
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
				blockLightCopy = new byte[blockLight.length];
				System.arraycopy(blockLight, 0, blockLightCopy, 0, blockLight.length);
				skyLightCopy = new byte[skyLight.length];
				System.arraycopy(skyLight, 0, skyLightCopy, 0, skyLight.length);
				break;
			case BOTH:
				blockIds = blockStore.getBlockIdArray();
				blockData = blockStore.getDataArray();

				blockLightCopy = new byte[blockLight.length];
				System.arraycopy(blockLight, 0, blockLightCopy, 0, blockLight.length);
				skyLightCopy = new byte[skyLight.length];
				System.arraycopy(skyLight, 0, skyLightCopy, 0, skyLight.length);
				break;
		}

		if (palette) {
			return new SpoutChunkSnapshot(this, blockStore.getPalette(), blockStore.getPackedWidth(), blockStore.getPackedArray(), blockLightCopy, skyLightCopy, entities, data);
		} else {
			return new SpoutChunkSnapshot(this, blockIds, blockData, blockLightCopy, skyLightCopy, entities, data);
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
		TickStage.checkStage(TickStage.FINALIZE);

		checkChunkLoaded();
		parentRegion.markObserverDirty(this);
		parentRegion.unloadQueue.remove(this);
		if (!isPopulated()) {
			if (populationQueued.compareAndSet(false, true)) {
				parentRegion.queueChunkForPopulation(this);
			}
		}
		if (observers.add((SpoutEntity) entity) && (entity instanceof SpoutPlayer)) {
			observingPlayers.add((SpoutPlayer) entity);
			Engine engine = Spout.getEngine();
			if(engine.getPlatform() ==  Platform.CLIENT && ((SpoutClient) engine).getActivePlayer() == entity) {
				setIsInViewDistance(true);
			}
		}
		SaveState.resetPostSaving(saveState);
		return true;
	}

	@Override
	public boolean removeObserver(Entity entity) {
		checkChunkLoaded();
		parentRegion.markObserverDirty(this);
		TickStage.checkStage(TickStage.FINALIZE);

		Integer oldDistance = ((SpoutEntity) entity).getPrevViewDistance();
		if (oldDistance == null) {
			return false;
		}

		if (observers.remove((SpoutEntity) entity) && (entity instanceof SpoutPlayer)) {
			observingPlayers.remove((SpoutPlayer) entity);
			
			Engine engine = Spout.getEngine();
			if(engine.getPlatform() ==  Platform.CLIENT && ((SpoutClient) engine).getActivePlayer() == entity) {
				setIsInViewDistance(false);
			}
		}
		expiredObserversQueue.add((SpoutEntity) entity);
		if (!isObserved()) {
			parentRegion.unloadQueue.add(this);
		}
		return true;
	}

	public boolean isObserved() {
		return getNumObservers() > 0;
	}

	@Override
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
		TickStage.checkStage(TickStage.FINALIZE, regionThread);
		if (!blockStore.needsCompression()) {
			return false;
		}
		blockStore.compress();
		return true;
	}

	public void setNeighbourRenderDirty(boolean dirty) {
		SpoutRegion parent = getRegion();
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					if (x == 0 || y == 0 || z == 0) {
						SpoutChunk c = parent.getLocalChunk(this, x, y, z, LoadOption.NO_LOAD);
						if (c != null) {
							c.setRenderDirty(dirty);
						}
					}
				}
			}
		}
	}
	
	public void setRenderDirty(boolean dirty) {
		renderDirty.set(dirty);
		if (dirty) {
			queueDirty();
		}
	}
	
	public boolean isRenderDirty() {
		return renderDirty.get() || isDirty();
	}

	public void setLightDirty(boolean dirty) {
		lightDirty.set(dirty);
	}

	public boolean isLightDirty() {
		return lightDirty.get();
	}

	public boolean isDirty() {
		if (lightDirty.get() || blockStore.isDirty()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canSend() {
		boolean canSend = isPopulated() && !this.isCalculatingLighting();
		if (!canSend && !isPopulated() && isObserved()) {
			if (populationQueued.compareAndSet(false, true)) {
				parentRegion.queueChunkForPopulation(this);
			}
		}
		return canSend;
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


	public boolean isCalculatingLighting() {
		return this.lightingCounter.get() >= 0;
	}

	public boolean isDirtyOverflow() {
		return blockStore.isDirtyOverflow();
	}

	protected Vector3 getDirtyBlock(int i) {
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
		TickStage.checkStage(TickStage.SNAPSHOT, regionThread);
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
		this.blockLight = null;
		this.skyLight = null;
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
		TickStage.checkStage(allowedStages, restrictedStages, regionThread);
	}

	@Override
	public void copySnapshot() {
	}

	public enum SaveState {
		UNLOAD_SAVE, UNLOAD, SAVE, NONE, SAVING, POST_SAVED, UNLOADED;

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
						nextState = SaveState.SAVE; break;
					case UNLOAD:
						nextState = SaveState.NONE; break;
					case POST_SAVED:
						nextState = SaveState.NONE;	break;
					case SAVE:
						nextState = SaveState.SAVE;	break;
					case NONE:
						nextState = SaveState.NONE;	break;
					case UNLOADED:
						nextState = SaveState.UNLOADED;	break;
					case SAVING:
						nextState = SaveState.NONE;	break;
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
						nextState = SaveState.UNLOAD_SAVE; break;
					case UNLOAD:
						nextState = save ? SaveState.UNLOAD_SAVE : SaveState.UNLOAD; break;
					case POST_SAVED:
						nextState = save ? SaveState.UNLOAD_SAVE : SaveState.POST_SAVED; break;
					case SAVE:
						nextState = SaveState.UNLOAD_SAVE; break;
					case NONE:
						nextState = save ? SaveState.UNLOAD_SAVE : SaveState.UNLOAD; break;
					case UNLOADED:
						nextState = SaveState.UNLOADED; break;
					case SAVING:
						nextState = SaveState.SAVING; break;
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
						nextState = SaveState.UNLOAD_SAVE; break;
					case UNLOAD:
						nextState = SaveState.UNLOAD_SAVE; break;
					case POST_SAVED:
						nextState = SaveState.UNLOAD_SAVE; break;
					case SAVE:
						nextState = SaveState.SAVE; break;
					case NONE:
						nextState = SaveState.SAVE; break;
					case UNLOADED:
						nextState = SaveState.UNLOADED; break;
					case SAVING:
						nextState = SaveState.SAVING; break;
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
						nextState = SaveState.SAVING; break;
					case UNLOAD:
						nextState = SaveState.UNLOAD; break;
					case POST_SAVED:
						nextState = SaveState.POST_SAVED; break;
					case SAVE:
						nextState = SaveState.NONE; break;
					case NONE:
						nextState = SaveState.NONE; break;
					case UNLOADED:
						nextState = SaveState.UNLOADED; break;
					case SAVING:
						nextState = SaveState.SAVING; break;
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
	public boolean populate(boolean force) {
		if (!isObserved() && !force) {
			return false;
		}

		if (!populationState.get().incomplete() && !force) {
			return false;
		}

		final List<Populator> clearPopulators = new ArrayList<Populator>();
		final List<Populator> populators = new ArrayList<Populator>();
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
				populator.populate(this, random);
			} catch (Exception e) {
				Spout.getEngine().getLogger().log(Level.SEVERE, "Could not populate Chunk with " + populator.toString());
				e.printStackTrace();
			}
		}

		populationState.set(PopulationState.POPULATED);
		if (SpoutConfiguration.LIGHTING_ENABLED.getBoolean()) {
			this.initLighting();
		}
		parentRegion.onChunkPopulated(this);
		setModified();
		return true;
	}

	public void populate(Populator populator) {
		try {
			populator.populate(this, new Random(WorldGeneratorUtils.getSeed(getWorld(), getX(), getY(), getZ(), 42)));
		} catch (Exception e) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Could not populate Chunk with " + populator.toString());
			e.printStackTrace();
		}
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

	public void queueForPopulation() {
		if (populationQueued.compareAndSet(false, true)) {
			parentRegion.queueChunkForPopulation(this);
		}
	}

	public void setNotQueuedForPopulation() {
		populationQueued.set(false);
	}

	@Override
	public void initLighting() {
		this.isInitializingLighting.set(true);
		this.notifyLightChange();
		int x, y, z, minY, maxY, columnY;
		// Lock operations to prevent premature handling
		Arrays.fill(this.blockLight, (byte) 0);
		Arrays.fill(this.skyLight, (byte) 0);

		// Initialize block lighting
		this.lightBlockSource = this.getSnapshot(SnapshotType.BLOCKS_ONLY, EntityType.NO_ENTITIES, ExtraData.NO_EXTRA_DATA);
		for (x = 0; x < BLOCKS.SIZE; x++) {
			for (y = 0; y < BLOCKS.SIZE; y++) {
				for (z = 0; z < BLOCKS.SIZE; z++) {
					if (!this.setBlockLight(x, y, z, this.lightBlockSource.getBlockMaterial(x, y, z).getLightLevel(this.lightBlockSource.getBlockData(x, y, z)), null)) {
						// Refresh the block if at an edge to update from surrounding chunks
						if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
							this.addBlockLightOperation(x, y, z, SpoutWorldLighting.REFRESH);
						}
					}
				}
			}
		}

		// Report the columns that require a sky-light update
		minY = this.getBlockY();
		maxY = minY + BLOCKS.SIZE;
		for (x = 0; x < BLOCKS.SIZE; x++) {
			for (z = 0; z < BLOCKS.SIZE; z++) {
				columnY = this.column.getSurfaceHeight(x, z) + 1;
				if (columnY < minY) {
					// everything is air - ignore refresh checks
					for (y = 0; y < BLOCKS.SIZE; y++) {
						this.addSkyLightUpdates(x, y, z, 15);
					}
				} else {
					// fill area above height with light
					for (y = columnY; y < maxY; y++) {
						this.addSkyLightUpdates(x, y, z, 15);
					}

					if (x == 0 || x == 15 || z == 0 || z == 15) {
						// refresh area below height at the edges
						for (y = columnY; y >= minY; y--) {
							this.addSkyLightOperation(x, y, z, SpoutWorldLighting.REFRESH);
						}
					} else {
						// Refresh top and bottom blocks
						this.addSkyLightOperation(x, 0, z, SpoutWorldLighting.REFRESH);
						if (columnY >= maxY) {
							this.addSkyLightOperation(x, 15, z, SpoutWorldLighting.REFRESH);
						}
					}
				}
			}
		}
		this.isInitializingLighting.set(false);
		this.lightBlockSource = this; // stop using the snapshot from now on
	}

	@Override
	public List<Entity> getEntities() {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for (Entity e : parentRegion.getAll()) {
			if (e.getChunk() == this) {
				entities.add(e);
			}
		}
		return Collections.unmodifiableList(entities);
	}

	@Override
	public List<Entity> getLiveEntities() {
		ArrayList<Entity> entities = new ArrayList<Entity>();
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
		return isReapable(getWorld().getAge());
	}

	public boolean isReapable(long worldAge) {
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
	public TShortObjectMap<BlockComponent> getBlockComponents() {
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
					if (bm instanceof ComplexMaterial) {
						BlockComponent component = ((ComplexMaterial)bm).createBlockComponent();
						short packed = NibbleQuadHashed.key(dx, dy, dz, 0);
						//Does not need synchronized, the chunk is not yet accessible outside this thread
						getBlockComponents().put(packed, component);
						ChunkComponentOwner owner = new ChunkComponentOwner(this, getBlockX() + dx, getBlockY() + dy, getBlockZ() + dz);
						component.attachTo(owner);
					}
				}
			}
		}
	}
	
	private int getAndSetBlockLocked(int x, int y, int z, short newId, short newData) {
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;
		
		synchronized(blockComponents) {
			int oldState = blockStore.getAndSetBlock(x, y, z, newId, newData);
			short oldId = BlockFullState.getId(oldState);
			BlockMaterial newMaterial = (BlockMaterial) MaterialRegistry.get(BlockFullState.getPacked(newId, newData));
			if (newId != oldId) {
				if (newMaterial instanceof ComplexMaterial) {
					BlockComponent component = ((ComplexMaterial) newMaterial).createBlockComponent();
					blockComponents.put(NibbleQuadHashed.key(x, y, z, 0), component);
					component.attachTo(new ChunkComponentOwner(this, x + getBlockX(), y + getBlockY(), z + getBlockZ()));
					component.onAttached();
				} else {
					blockComponents.remove(NibbleQuadHashed.key(x, y, z, 0));
				}
			}
			return oldState;
		}
	}

	@Override
	public BlockComponent getBlockComponent(int x, int y, int z) {
		synchronized(blockComponents) {
			return blockComponents.get(NibbleQuadHashed.key(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK, 0));
		}
	}

	protected void tickBlockComponents(float dt) {
		synchronized(blockComponents) {
			for (BlockComponent c : blockComponents.valueCollection()) {
				try {
					if (c.canTick()) {
						c.tick(dt);
					}
				} catch (Exception e) {
					Spout.getLogger().log(Level.SEVERE, "Unhandled exception while ticking block component", e);
				}
			}
		}
	}

	@Override
	public Block getBlock(float x, float y, float z) {
		return getBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
	}

	@Override
	public Block getBlock(Vector3 position) {
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
			BlockMaterial bm = (BlockMaterial) BlockMaterial.get(state);
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
			BlockMaterial bm = (BlockMaterial) BlockMaterial.get(state);
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

		// Update block lighting
		this.setBlockLight(x, y, z, newMaterial.getLightLevel(newData), cause);
		
		setModified();
	}

	@Override
	public SerializableMap getDataMap() {
		return dataMap;
	}

	public WeakReference<Chunk> getWeakReference() {
		return selfReference;
	}

	public void setNotDirtyQueued() {
		dirtyQueued.set(false);
	}

	private void queueDirty() {
		if (dirtyQueued.compareAndSet(false, true)) {
			parentRegion.queueDirty(this);
		}
	}

	public void setInactivePhysics(boolean local) {
		physicsQueue.setInactive(local);
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
	 * Called when an entity enters the chunk.
	 * This method is NOT called for players.
	 * 
	 * This method occurs during finalizeRun
	 * 
	 * @param e
	 */
	public void onEntityEnter(SpoutEntity e) {
		entitiesModified.compareAndSet(false, true);
	}

	/**
	 * Called when an entity leaves the chunk.
	 * This method is NOT called for players.
	 * 
	 * This method occurs during finalizeRun
	 * 
	 * @param e
	 */
	public void onEntityLeave(SpoutEntity e) {
		entitiesModified.compareAndSet(false, true);
	}
	
	protected void setModified() {
		if (chunkModified.compareAndSet(false, true)) {
			setAutosaveTicks(autosaveInterval);
		}
	}
	
	public boolean isBlockUniform() {
		return blockStore.isBlockUniform();
	}

	private boolean rended = false;
	
	public void setRended(boolean rended) {
		this.rended = rended;
	}
	public boolean isRended() {
		return rended;
	}
}
