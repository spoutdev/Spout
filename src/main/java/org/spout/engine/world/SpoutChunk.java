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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.BlockController;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.event.block.BlockChangeEvent;
import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.discrete.Point;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.material.block.BlockSnapshot;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.cuboid.CuboidBuffer;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.map.concurrent.AtomicBlockStore;
import org.spout.api.util.map.concurrent.AtomicBlockStoreImpl;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashMap;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashSet;

public class SpoutChunk extends Chunk {
	/**
	 * Multi-thread write access to the block store is only allowed during the allowed stages. During the restricted stages, only the region thread may modify the block store.
	 */
	private static final int restrictedStages = TickStage.FINALIZE;
	private static final int allowedStages = TickStage.STAGE1 | TickStage.STAGE2P | TickStage.TICKSTART;

	/**
	 * Time in ms between chunk reaper unload checks
	 */
	protected static final long UNLOAD_PERIOD = 60000;
	/**
	 * Storage for block ids, data and auxiliary data. For blocks with data = 0 and auxiliary data = null, the block is stored as a short.
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
	private AtomicBoolean populated;
	/**
	 * Snapshot Manager
	 */
	protected final SnapshotManager snapshotManager = new SnapshotManager();
	/**
	 * A set of all entities who are observing this chunk
	 */
	protected final SnapshotableHashMap<Entity, Integer> observers = new SnapshotableHashMap<Entity, Integer>(snapshotManager);
	/**
	 * A set of entities contained in the chunk
	 */
	// Hash set should return "dirty" list
	protected final SnapshotableHashSet<Entity> entities = new SnapshotableHashSet<Entity>(snapshotManager);
	/**
	 * Stores a short value of the sky light
	 * <p/>
	 * Note: These do not need to be thread-safe as long as only one thread (the region) is allowed to modify the values. If setters are provided, this will need to be made safe.
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
	 * True if this chunk should be resent due to light calculations
	 */
	protected final AtomicBoolean lightDirty = new AtomicBoolean(false);
	/**
	 * If -1, there are no changes. If higher, there are changes and the number denotes how many ticks these have been there.<br>
	 * Every time a change is committed the value is set to 0. The region will increment it as well.
	 */
	protected final AtomicInteger lightingCounter = new AtomicInteger(-1);

	/**
	 * Indicates if there has been any changes since the last render snapshot
	 */
	private final AtomicBoolean renderDirtyFlag = new AtomicBoolean(true);
	private final AtomicBoolean renderSnapshotInProgress = new AtomicBoolean(false);

	/**
	 * Data map and Datatable associated with it
	 */
	protected final DatatableMap datatableMap;
	protected final DataMap dataMap;

	/**
	 * Manages the biomes for this chunk
	 */
	private final BiomeManager biomes;

	/**
	 * Shift cache array for shifting fields
	 */
	private final static int[] shiftCache = new int[65536];

	/**
	 * The thread associated with the region
	 */
	private final Thread regionThread;

	static {
		for (int i = 0; i < shiftCache.length; i++) {
			int shift = 0;
			while ((i > 0) && (i >> shift) << shift == i) {
				shift++;
			}
			shiftCache[i] = shift - 1;
		}
	}

	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, short[] initial, BiomeManager manager, DataMap map) {
		this(world, region, x, y, z, false, initial, null, null, null, manager, map.getRawMap());
	}

	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, boolean populated, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, BiomeManager manager, DatatableMap extraData) {
		super(world, x * BLOCKS.SIZE, y * BLOCKS.SIZE, z * BLOCKS.SIZE);
		parentRegion = region;
		blockStore = new AtomicBlockStoreImpl(BLOCKS.BITS, 10, blocks, data);
		this.populated = new AtomicBoolean(populated);

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

		if (extraData != null) {
			this.datatableMap = extraData;
		} else {
			this.datatableMap = new GenericDatatableMap();;
		}
		this.dataMap = new DataMap(this.datatableMap);

		column = world.getColumn(this.getBlockX(), this.getBlockZ(), true);
		column.registerChunk(((int) y) << BLOCKS.BITS);
		columnRegistered.set(true);
		lastUnloadCheck.set(world.getAge());
		blockStore.resetDirtyArrays();	// Clear false dirty state on freshly loaded chunk
		this.biomes = manager;
		this.regionThread = region.getExceutionThread();
	}

	@Override
	public SpoutWorld getWorld() {
		return (SpoutWorld) super.getWorld();
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Source source) {
		if (source == null) {
			throw new NullPointerException("Source can not be null");
		}
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		BlockMaterial material = this.getBlockMaterial(x, y, z);
		int oldState = blockStore.getAndSetBlock(x, y, z, material.getId(), data);
		short oldData = BlockFullState.getData(oldState);

		if (((oldData ^ data) & material.getDataMask()) != 0) {
			Material oldMaterial = MaterialRegistry.get(oldState);
			if (material instanceof DynamicMaterial) {
				if (oldMaterial instanceof BlockMaterial) {
					BlockMaterial oldBlockMaterial = (BlockMaterial)oldMaterial;
					if (!oldBlockMaterial.isCompatibleWith(material) || !material.isCompatibleWith(oldBlockMaterial)) {
						parentRegion.resetDynamicBlock(x, y, z);
					}
				} else {
					parentRegion.resetDynamicBlock(x, y, z);
				}
			}
		}

		// Data component does not alter height of the world. Change this?
		// column.notifyBlockChange(x, this.getBlockY() + y, z);

		// Update block lighting
		this.setBlockLight(x, y, z, material.getLightLevel(data), source);

		return true;
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		if (source == null) {
			throw new NullPointerException("Source can not be null");
		}
		return setBlockMaterial(x, y, z, material, data, source, true);
	}

	private boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source, boolean event) {
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		if (event) {
			Block block = new SpoutBlock(getWorld(), x, y, z, source);
			BlockChangeEvent blockEvent = new BlockChangeEvent(block, new BlockSnapshot(block, material, data), source);
			Spout.getEngine().getEventManager().callEvent(blockEvent);
			if (blockEvent.isCancelled()) {
				return false;
			}
			material = blockEvent.getSnapshot().getMaterial();
			data = blockEvent.getSnapshot().getData();
		}

		Material oldMaterial = MaterialRegistry.get(blockStore.getAndSetBlock(x, y, z, material.getId(), data));

		int oldheight = column.getSurfaceHeight(x, z);
		y += this.getBlockY();
		column.notifyBlockChange(x, y, z);
		x += this.getBlockX();
		z += this.getBlockZ();
		int newheight = column.getSurfaceHeight(x, z);

		if (this.isPopulated()) {
			SpoutWorld world = this.getWorld();

			// Update block lighting
			if (!this.setBlockLight(x, y, z, material.getLightLevel(data), source)) {
				// if the light level is left unchanged, refresh lighting from neighbors
				world.getLightingManager().blockLight.addRefresh(x, y, z);
			}

			// Update sky lighting
			if (newheight > oldheight) {
				// set sky light of blocks below to 0
				for (y = oldheight; y < newheight; y++) {
					world.setBlockSkyLight(x, y + 1, z, (byte) 0, source);
				}
			} else if (newheight < oldheight) {
				// set sky light of blocks above to 15
				for (y = newheight; y < oldheight; y++) {
					world.setBlockSkyLight(x, y + 1, z, (byte) 15, source);
				}
			} else {
				byte old = this.getBlockSkyLight(x, y, z);
				if (old == 0) {
					world.getLightingManager().skyLight.addRefresh(this, x, y, z);
				} else if (old < 15) {
					this.setBlockSkyLight(x, y, z, (byte) 0, source);
				}
			}
		}
		if (material instanceof DynamicMaterial) {
			if (oldMaterial instanceof BlockMaterial) {
				BlockMaterial oldBlockMaterial = (BlockMaterial)oldMaterial;
				if (!oldBlockMaterial.isCompatibleWith(material) || !material.isCompatibleWith(oldBlockMaterial)) {
					parentRegion.resetDynamicBlock(x, y, z);
				}
			} else {
				parentRegion.resetDynamicBlock(x, y, z);
			}
		}
		return true;
	}

	protected void setCuboid(CuboidBuffer buffer) {
		Point base = buffer.getBase();
		Vector3 size = buffer.getSize();

		int startX = base.getBlockX() - this.getBlockX();
		int startY = base.getBlockY() - this.getBlockY();
		int startZ = base.getBlockZ() - this.getBlockZ();

		int endX = (base.getBlockX() + (int)size.getX()) - this.getBlockX();
		int endY = (base.getBlockY() + (int)size.getY()) - this.getBlockY();
		int endZ = (base.getBlockZ() + (int)size.getZ()) - this.getBlockZ();

		endX &= BLOCKS.MASK;
		endY &= BLOCKS.MASK;
		endZ &= BLOCKS.MASK;

		for (int dx = startX; dx < endX; dx++) {
			for (int dy = startY; dy < endY; dy++) {
				for (int dz = startZ; dz < endZ; dz++) {
					setBlockMaterial(dx, dy, dz, BlockMaterial.get(buffer.get(dx, dy, dz)), (short)0, null, false);
				}
			}
		}
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		parentRegion.resetDynamicBlock(getBlockX(x), getBlockY(y), getBlockZ(z));
	}

	@Override
	public void queueDynamicUpdate(int x, int y, int z, long nextUpdate, Object hint) {
		parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z), nextUpdate, hint);
	}

	@Override
	public void queueDynamicUpdate(int x, int y, int z, long nextUpdate) {
		parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z), nextUpdate);
	}

	@Override
	public void queueDynamicUpdate(int x, int y, int z) {
		parentRegion.queueDynamicUpdate(getBlockX(x), getBlockY(y), getBlockZ(z));
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		checkChunkLoaded();
		int state = blockStore.getFullData(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK);
		short data = BlockFullState.getData(state);
		short id = BlockFullState.getId(state);
		BlockMaterial mat = BlockMaterial.get(id);
		if (mat != null) {
			return mat.getSubMaterial(data);
		} else {
			return BlockMaterial.AIR;
		}
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		checkChunkLoaded();
		return (short) blockStore.getData(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK);
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Source source) {
		if (source == null) {
			throw new NullPointerException("Source can not be null");
		}
		light &= 0xF;
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		checkChunkLoaded();
		int index = getBlockIndex(x, y, z);
		byte oldLight;
		if ((index & 1) == 1) {
			index >>= 1;
			oldLight = NibblePairHashed.key1(blockLight[index]);
			blockLight[index] = NibblePairHashed.setKey1(blockLight[index], light);
		} else {
			index >>= 1;
			oldLight = NibblePairHashed.key2(blockLight[index]);
			blockLight[index] = NibblePairHashed.setKey2(blockLight[index], light);
		}
		if (light > oldLight) {
			// light increased
			getWorld().getLightingManager().blockLight.addGreater(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else if (light < oldLight) {
			// light decreased
			getWorld().getLightingManager().blockLight.addLesser(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else {
			return false;
		}
		this.notifyLightChange();
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
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Source source) {
		if (source == null) {
			throw new NullPointerException("Source can not be null");
		}
		light &= 0xF;
		x &= BLOCKS.MASK;
		y &= BLOCKS.MASK;
		z &= BLOCKS.MASK;

		checkChunkLoaded();
		int index = getBlockIndex(x, y, z);
		byte oldLight;
		if ((index & 1) == 1) {
			index >>= 1;
			oldLight = NibblePairHashed.key1(skyLight[index]);
			skyLight[index] = NibblePairHashed.setKey1(skyLight[index], light);
		} else {
			index >>= 1;
			oldLight = NibblePairHashed.key2(skyLight[index]);
			skyLight[index] = NibblePairHashed.setKey2(skyLight[index], light);
		}

		if (light > oldLight) {
			// light increased
			getWorld().getLightingManager().skyLight.addGreater(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else if (light < oldLight) {
			// light decreased
			getWorld().getLightingManager().skyLight.addLesser(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else {
			return false;
		}
		this.notifyLightChange();
		return true;
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		checkChunkLoaded();
		int index = getBlockIndex(x, y, z);
		byte light = skyLight[index >> 1];
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
	public void updateBlockPhysics(int x, int y, int z, Source source) {
		checkChunkLoaded();
		this.getRegion().updateBlockPhysics(x, y, z, source);
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
				case SAVE:
					nextState = SaveState.UNLOAD_SAVE;
					break;
				case NONE:
					nextState = save ? SaveState.UNLOAD_SAVE : SaveState.UNLOAD;
					break;
				case UNLOADED:
					nextState = SaveState.UNLOADED;
					break;
				default:
					throw new IllegalStateException("Unknown save state: " + state);
			}
			success = saveState.compareAndSet(state, nextState);
		}
	}

	@Override
	public void save() {
		checkChunkLoaded();
		saveNoMark();
		markForSaveUnload();
	}

	private void markForSaveUnload() {
		(parentRegion).markForSaveUnload(this);
	}

	public void saveNoMark() {
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
				case SAVE:
					nextState = SaveState.SAVE;
					break;
				case NONE:
					nextState = SaveState.SAVE;
					break;
				case UNLOADED:
					nextState = SaveState.UNLOADED;
					break;
				default:
					throw new IllegalStateException("Unknown save state: " + state);
			}
			saveState.compareAndSet(state, nextState);
		}
	}

	public SaveState getAndResetSaveState() {
		boolean success = false;
		SaveState old = null;
		while (!success) {
			old = saveState.get();
			if (old != SaveState.UNLOADED) {
				success = saveState.compareAndSet(old, SaveState.NONE);
			} else {
				success = saveState.compareAndSet(old, SaveState.UNLOADED);
			}
		}
		return old;
	}

	/**
	 * @return true if the chunk can be skipped
	 * @throws InterruptedException
	 */
	public boolean copySnapshotRun() throws InterruptedException {
		// NOTE : This is only called for chunks with contain entities.
		snapshotManager.copyAllSnapshots();
		return entities.get().size() == 0;	
	}

	// Saves the chunk data - this occurs directly after a snapshot update
	public void syncSave() {
		WorldFiles.saveChunk(this, blockStore.getBlockIdArray(), blockStore.getDataArray(), skyLight, blockLight, datatableMap, this.parentRegion.getChunkOutputStream(this));
	}

	@Override
	public ChunkSnapshot getSnapshot() {
		return getSnapshot(true);
	}

	@Override
	public ChunkSnapshot getSnapshot(boolean entities) {
		checkChunkLoaded();
		byte[] blockLightCopy = new byte[blockLight.length];
		System.arraycopy(blockLight, 0, blockLightCopy, 0, blockLight.length);
		byte[] skyLightCopy = new byte[skyLight.length];
		System.arraycopy(skyLight, 0, skyLightCopy, 0, skyLight.length);
		return new SpoutChunkSnapshot(this, blockStore.getBlockIdArray(), blockStore.getDataArray(), blockLightCopy, skyLightCopy, entities);
	}

	@Override
	public Future<ChunkSnapshot> getFutureSnapshot() {
		return getFutureSnapshot(false);
	}

	@Override
	public Future<ChunkSnapshot> getFutureSnapshot(boolean entities) {
		return getFutureSnapshot(entities, false);
	}

	public Future<ChunkSnapshot> getFutureSnapshot(boolean entities, boolean renderSnapshot) {
		boolean renderDirty;
		if (renderSnapshot) {
			renderDirty = renderDirtyFlag.get();
			if (renderDirty) {
				if (!renderSnapshotInProgress.compareAndSet(false, true)) {
					throw new IllegalStateException("Only one render snapshot may be in progress at one time for a given chunk");
				}
			} else {
				return null;
			}
		}
		SpoutChunkSnapshotFuture future = new SpoutChunkSnapshotFuture(this, entities, renderSnapshot);
		parentRegion.addSnapshotFuture(future);
		return future;
	}

	@Override
	public boolean refreshObserver(Entity entity) {
		TickStage.checkStage(TickStage.FINALIZE);
		if (!entity.isObserver()) {
			throw new IllegalArgumentException("Cannot add an entity that isn't marked as an observer!");
		}
		checkChunkLoaded();
		parentRegion.unSkipChunk(this);
		int distance = (int) ((SpoutEntity) entity).getChunkLive().getBase().getDistance(getBase());
		Integer oldDistance = observers.put(entity, distance);
		if (oldDistance == null) {
			parentRegion.unloadQueue.remove(this);
			if (!isPopulated()) {
				parentRegion.queueChunkForPopulation(this);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeObserver(Entity entity) {
		checkChunkLoaded();
		parentRegion.unSkipChunk(this);
		TickStage.checkStage(TickStage.FINALIZE);
		Integer oldDistance = observers.remove(entity);
		if (oldDistance != null) {
			if (observers.isEmptyLive()) {
				parentRegion.unloadQueue.add(this);
			}
			return true;
		} else {
			return false;
		}
	}

	public Set<Entity> getObserversLive() {
		return observers.getLive().keySet();
	}

	public Set<Entity> getObservers() {
		return observers.get().keySet();
	}

	public boolean compressIfRequired() {
		checkChunkLoaded();
		TickStage.checkStage(restrictedStages, regionThread);
		if (blockStore.needsCompression()) {
			checkChunkLoaded();
			checkBlockStoreUpdateAllowed();
			blockStore.compress();
			return true;
		} else {
			return false;
		}
	}

	public void setLightDirty(boolean dirty) {
		lightDirty.set(dirty);
	}

	public boolean isLightDirty() {
		return lightDirty.get();
	}

	public boolean isDirty() {
		return lightDirty.get() || blockStore.isDirty();
	}

	int x = 0;

	@Override
	public boolean canSend() {
		boolean canSend = this.isPopulated() && !this.isCalculatingLighting();
		if (!canSend && !isPopulated() && this.observers.get().size() > 0 && this.observers.getLive().size() > 0) {
			((SpoutRegion)parentRegion).queueChunkForPopulation(this);
		}
		return canSend;
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

	public void setRenderClean() {
		renderDirtyFlag.set(false);
		if (!renderSnapshotInProgress.compareAndSet(true, false)) {
			Spout.getLogger().info("Render snapshot set to done when no snapshot was in progress");
		}
	}

	public void setRenderDirty() {
		renderDirtyFlag.set(true);
	}

	public void resetDirtyArrays() {
		blockStore.resetDirtyArrays();
	}

	@Override
	public boolean isLoaded() {
		return saveState.get() != SaveState.UNLOADED;
	}

	public void setUnloaded() {
		TickStage.checkStage(TickStage.SNAPSHOT);
		saveState.set(SaveState.UNLOADED);
		blockStore = null;
		deregisterFromColumn();
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
	public Biome getBiomeType(int x, int y, int z) {
		return biomes.getBiome(x & BLOCKS.MASK, y & BLOCKS.MASK, z & BLOCKS.MASK);
	}

	public static enum SaveState {

		UNLOAD_SAVE,
		UNLOAD,
		SAVE,
		NONE,
		UNLOADED;

		public boolean isSave() {
			return this == SAVE || this == UNLOAD_SAVE;
		}

		public boolean isUnload() {
			return this == UNLOAD_SAVE || this == UNLOAD;
		}
	}

	@Override
	public SpoutRegion getRegion() {
		return parentRegion;
	}

	@Override
	public void populate() {
		populate(false);
	}

	@Override
	public void populate(boolean force) {
		if (this.observers.get().size() == 0 || this.observers.getLive().size() == 0) {
			return;
		}
		
		if (this.populated.getAndSet(true) && !force) {
			return;
		}

		final Random random = new Random(WorldGeneratorUtils.getSeed(getWorld(), getX(), getY(), getZ(), 42));

		for (Populator populator : getWorld().getGenerator().getPopulators()) {
			try {
				populator.populate(this, random);
			} catch (Exception e) {
				Spout.getEngine().getLogger().log(Level.SEVERE, "Could not populate Chunk with " + populator.toString());
				e.printStackTrace();
			}
		}

		if (SpoutConfiguration.LIGHTING_ENABLED.getBoolean()) {
			this.initLighting();
		}
		parentRegion.onChunkPopulated(this);
	}

	@Override
	public boolean isPopulated() {
		return populated.get();
	}

	@Override
	public void initLighting() {
		this.notifyLightChange();
		SpoutWorld world = this.getWorld();
		int x, y, z, minY, maxY, columnY;
		Arrays.fill(this.blockLight, (byte) 0);
		Arrays.fill(this.skyLight, (byte) 0);

		// Initialize block lighting
		for (x = 0; x < BLOCKS.SIZE; x++) {
			for (y = 0; y < BLOCKS.SIZE; y++) {
				for (z = 0; z < BLOCKS.SIZE; z++) {
					if (!this.setBlockLight(x, y, z, this.getBlockMaterial(x, y, z).getLightLevel(this.getBlockData(x, y, z)), world)) {
						// Bugged? This requires additional testing!
						//world.getLightingManager().blockLight.addRefresh(this, x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
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
					columnY = minY;
				}

				// fill area above height with light
				for (y = columnY; y < maxY; y++) {
					this.setBlockSkyLight(x, y, z, (byte) 15, world);
				}
				// Bugged? This requires additional testing!
				/*
				// refresh area below height
				for (y = columnY; y >= minY; y--) {
					world.getLightingManager().skyLight.addRefresh(this, x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
				}
				*/
			}
		}
	}

	public boolean addEntity(SpoutEntity entity) {
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
		parentRegion.unSkipChunk(this);
		return entities.add(entity);
	}

	public boolean removeEntity(SpoutEntity entity) {
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
		parentRegion.unSkipChunk(this);
		return entities.remove(entity);
	}

	@Override
	public Set<Entity> getEntities() {
		return entities.get();
	}

	@Override
	public Set<Entity> getLiveEntities() {
		return entities.getLive();
	}

	// Handles network updates for all entities that were
	// - in the chunk at the last snapshot
	// - were not in a chunk at the last snapshot and are now in this chunk
	public void syncEntities() {
		Map<Entity, Integer> observerSnapshot = observers.get();
		Map<Entity, Integer> observerLive = observers.getLive();

		Set<Entity> entitiesSnapshot = entities.get();
		entities.getLive();

		// Changed means entered/left the chunk
		List<Entity> changedEntities = entities.getDirtyList();
		List<Entity> changedObservers = observers.getDirtyList();

		if (entitiesSnapshot.size() > 0) {
			for (Entity p : changedObservers) {
				Integer playerDistanceOld = observerSnapshot.get(p);
				if (playerDistanceOld == null) {
					playerDistanceOld = Integer.MAX_VALUE;
				}
				Integer playerDistanceNew = observerLive.get(p);
				if (playerDistanceNew == null) {
					playerDistanceNew = Integer.MAX_VALUE;
				}
				// Player Network sync
				if (p.getController() instanceof PlayerController) {
					Player player = ((PlayerController) p.getController()).getPlayer();

					NetworkSynchronizer n = player.getNetworkSynchronizer();
					for (Entity e : entitiesSnapshot) {
						if (player.getEntity().equals(e)) {
							continue;
						}
						int entityViewDistanceOld = ((SpoutEntity) e).getPrevViewDistance();
						int entityViewDistanceNew = e.getViewDistance();

						if (playerDistanceOld <= entityViewDistanceOld && playerDistanceNew > entityViewDistanceNew) {
							n.destroyEntity(e);
						} else if (playerDistanceNew <= entityViewDistanceNew && playerDistanceOld > entityViewDistanceOld) {
							n.spawnEntity(e);
						}
					}
				}
			}
		}

		for (Entity e : changedEntities) {
			SpoutChunk oldChunk = (SpoutChunk) e.getChunk();
			if (((SpoutEntity) e).justSpawned()) {
				oldChunk = null;
			}
			SpoutChunk newChunk = (SpoutChunk) ((SpoutEntity) e).getChunkLive();
			if (!(oldChunk != null && oldChunk.equals(this)) && !((SpoutEntity) e).justSpawned()) {
				continue;
			}
			for (Entity p : observerLive.keySet()) {
				if (p == null || p.equals(e)) {
					continue;
				}
				if (p.getController() instanceof PlayerController) {
					Integer playerDistanceOld;
					if (oldChunk == null) {
						playerDistanceOld = Integer.MAX_VALUE;
					} else {
						playerDistanceOld = oldChunk.observers.getLive().get(p);
						if (playerDistanceOld == null) {
							playerDistanceOld = Integer.MAX_VALUE;
						}
					}
					Integer playerDistanceNew;
					if (newChunk == null) {
						playerDistanceNew = Integer.MAX_VALUE;
					} else {
						playerDistanceNew = newChunk.observers.getLive().get(p);
						if (playerDistanceNew == null) {
							playerDistanceNew = Integer.MAX_VALUE;
						}
					}
					int entityViewDistanceOld = ((SpoutEntity) e).getPrevViewDistance();
					int entityViewDistanceNew = e.getViewDistance();

					Player player = ((PlayerController) p.getController()).getPlayer();
					NetworkSynchronizer n = player.getNetworkSynchronizer();

					if (n == null) {
						continue;
					}
					if (playerDistanceOld <= entityViewDistanceOld && playerDistanceNew > entityViewDistanceNew) {
						n.destroyEntity(e);
					} else if (playerDistanceNew <= entityViewDistanceNew && playerDistanceOld > entityViewDistanceOld) {
						n.spawnEntity(e);
					}
				}
			}
		}

		// Update all entities that are in the chunk
		// TODO - should have sorting based on view distance
		for (Map.Entry<Entity, Integer> entry : observerLive.entrySet()) {
			Entity p = entry.getKey();
			if (p.getController() instanceof PlayerController) {
				Player player = ((PlayerController) p.getController()).getPlayer();
				NetworkSynchronizer n = player.getNetworkSynchronizer();
				if (n != null) {
					int playerDistance = entry.getValue();
					Entity playerEntity = p;
					for (Entity e : entitiesSnapshot) {
						if (playerEntity != e) {
							if (playerDistance <= e.getViewDistance()) {
								if (((SpoutEntity) e).getPrevController() != e.getController()) {
									n.destroyEntity(e);
									n.spawnEntity(e);
								}
								n.syncEntity(e);
							}
						}
					}
					for (Entity e : changedEntities) {
						if (entitiesSnapshot.contains(e)) {
							continue;
						} else if (((SpoutEntity) e).justSpawned()) {
							if (playerEntity != e) {
								if (playerDistance <= e.getViewDistance()) {
									if (((SpoutEntity) e).getPrevController() != e.getController()) {
										n.destroyEntity(e);
										n.spawnEntity(e);
									}
									n.syncEntity(e);
								}
							}
						}
					}
				}
			}
		}
	}

	public void deregisterFromColumn() {
		deregisterFromColumn(true);
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
		if (lastUnloadCheck.get() + UNLOAD_PERIOD < worldAge) {
			lastUnloadCheck.set(worldAge);
			return this.observers.getLive().size() <= 0 && this.observers.get().size() <= 0;
		} else {
			return false;
		}
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

	@Override
	public void setBlockController(int x, int y, int z, BlockController controller) {
		getRegion().setBlockController(x, y, z, controller);
	}

	@Override
	public BlockController getBlockController(int x, int y, int z) {
		return getRegion().getBlockController(x, y, z);
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return this.getBlock(x, y, z, this.getWorld());
	}

	@Override
	public Block getBlock(float x, float y, float z, Source source) {
		return getBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), source);
	}

	@Override
	public Block getBlock(float x, float y, float z) {
		return getBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), this.getWorld());
	}

	@Override
	public Block getBlock(Vector3 position) {
		return getBlock(position, this.getWorld());
	}

	@Override
	public Block getBlock(Vector3 position, Source source) {
		return getBlock(position.getX(), position.getY(), position.getZ(), source);
	}

	@Override
	public Block getBlock(int x, int y, int z, Source source) {
		return new SpoutBlock(this.getWorld(), getBlockX(x), getBlockY(y), getBlockZ(z), this, source);
	}

	@Override
	public boolean compareAndSetData(int bx, int by, int bz, int expect, short data) {
		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();
		// TODO - this should probably trigger a dynamic block reset
		short expId = BlockFullState.getId(expect);
		short expData = BlockFullState.getData(expect);
		return this.blockStore.compareAndSetBlock(bx & BLOCKS.MASK, by & BLOCKS.MASK, bz & BLOCKS.MASK, expId, expData, expId, data);
	}

	@Override
	public short setBlockDataBits(int bx, int by, int bz, short bits) {
		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		bx &= BLOCKS.MASK;
		by &= BLOCKS.MASK;
		bz &= BLOCKS.MASK;

		boolean success = false;
		short oldData = 0;
		while (!success) {
			int state = this.blockStore.getFullData(bx, by, bz);
			oldData = BlockFullState.getData(state);
			short oldId = BlockFullState.getId(state);
			short newData = (short)(oldData | bits);
			// TODO - this should probably trigger a dynamic block reset
			success = blockStore.compareAndSetBlock(bx, by, bz, oldId, oldData, oldId, newData);
		}
		return oldData;
	}

	@Override
	public short clearBlockDataBits(int bx, int by, int bz, short bits) {
		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		bx &= BLOCKS.MASK;
		by &= BLOCKS.MASK;
		bz &= BLOCKS.MASK;

		boolean success = false;
		short oldData = 0;
		while (!success) {
			int state = this.blockStore.getFullData(bx, by, bz);
			oldData = BlockFullState.getData(state);
			short oldId = BlockFullState.getId(state);
			short newData = (short)(oldData & (~bits));
			// TODO - this should probably trigger a dynamic block reset
			success = blockStore.compareAndSetBlock(bx, by, bz, oldId, oldData, oldId, newData);
		}
		return oldData;
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
	public int setBlockDataField(int bx, int by, int bz, int bits, int value) {
		checkChunkLoaded();
		checkBlockStoreUpdateAllowed();

		bx &= BLOCKS.MASK;
		by &= BLOCKS.MASK;
		bz &= BLOCKS.MASK;

		int shift = shiftCache[bits];

		boolean success = false;
		short oldData = 0;
		while (!success) {
			int state = this.blockStore.getFullData(bx, by, bz);
			oldData = BlockFullState.getData(state);
			short oldId = BlockFullState.getId(state);
			short newData = (short)(((value << shift) & bits) | (oldData & (~bits)));

			// TODO - this should probably trigger a dynamic block reset
			success = blockStore.compareAndSetBlock(bx, by, bz, oldId, oldData, oldId, newData);
		}
		return (oldData & bits) >> shift;
	}

	@Override
	public DefaultedMap<String, Serializable> getDataMap() {
		return dataMap;
	}

	public BiomeManager getBiomeManager() {
		return biomes;
	}

}
