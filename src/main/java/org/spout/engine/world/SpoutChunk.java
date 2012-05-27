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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.map.concurrent.AtomicBlockStore;

import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableBoolean;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashMap;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashSet;

public class SpoutChunk extends Chunk {
	/**
	 * Time in ms between chunk reaper unload checks
	 */
	protected static final long UNLOAD_PERIOD = 60000;
	/**
	 * Storage for block ids, data and auxiliary data. For blocks with data = 0
	 * and auxiliary data = null, the block is stored as a short.
	 */
	protected AtomicBlockStore<DatatableMap> blockStore;
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
	private SnapshotableBoolean populated;
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
	 * Note: These do not need to be thread-safe as long as only one thread (the region)
	 * is allowed to modify the values. If setters are provided, this will need to be made safe.
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
	 * Data map and Datatable associated with it
	 */
	protected final DatatableMap datatableMap;
	protected final DataMap dataMap;

	/**
	 * Manages the biomes for this chunk
	 */
	private final BiomeManager biomes;

	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, short[] initial, BiomeManager manager, DataMap map) {
		this(world, region, x, y, z, false, initial, null, null, null, manager, map.getRawMap());
	}

	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, boolean populated, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, BiomeManager manager, DatatableMap extraData) {
		super(world, x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE);
		parentRegion = region;
		blockStore = new AtomicBlockStore<DatatableMap>(Chunk.CHUNK_SIZE_BITS, 10, blocks, data);
		this.populated = new SnapshotableBoolean(snapshotManager, populated);

		if (skyLight == null) {
			this.skyLight = new byte[CHUNK_VOLUME / 2];
		} else {
			this.skyLight = skyLight;
		}
		if (blockLight == null) {
			this.blockLight = new byte[CHUNK_VOLUME / 2];
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
		column.registerChunk();
		columnRegistered.set(true);
		lastUnloadCheck.set(world.getAge());
		this.biomes = manager;
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
		x &= BASE_MASK;
		y &= BASE_MASK;
		z &= BASE_MASK;

		checkChunkLoaded();
		BlockMaterial material = this.getBlockMaterial(x, y, z);
		blockStore.setBlock(x, y, z, material.getId(), data);

		//Data component does not alter height of the world. Change this?
		//column.notifyBlockChange(x, this.getBlockY() + y, z);

		//Update block lighting
		this.setBlockLight(x, y, z, material.getLightLevel(data), source);

		return true;
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		if (source == null) {
			throw new NullPointerException("Source can not be null");
		}
		x &= BASE_MASK;
		y &= BASE_MASK;
		z &= BASE_MASK;

		checkChunkLoaded();
		blockStore.setBlock(x, y, z, material.getId(), data);

		int oldheight = column.getSurfaceHeight(x, z);
		y += this.getBlockY();
		column.notifyBlockChange(x, y, z);
		x += this.getBlockX();
		z += this.getBlockZ();
		int newheight = column.getSurfaceHeight(x, z);

		if (this.isPopulated()) {
			SpoutWorld world = this.getWorld();

			//Update block lighting
			this.setBlockLight(x, y, z, material.getLightLevel(data), source);

			//Update sky lighting
			if (newheight > oldheight) {
				//set sky light of blocks below to 0
				for (y = oldheight; y < newheight; y++) {
					world.setBlockSkyLight(x, y, z, (byte) 0, source);
				}
			} else if (newheight < oldheight) {
				//set sky light of blocks above to 15
				for (y = newheight; y < oldheight; y++) {
					world.setBlockSkyLight(x, y, z, (byte) 15, source);
				}
			}
		}
		return true;
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		checkChunkLoaded();
		BlockFullState fullState = blockStore.getFullData(x & BASE_MASK, y & BASE_MASK, z & BASE_MASK);
		short id = fullState.getId();
		BlockMaterial mat = BlockMaterial.get(id);
		return mat == null ? BlockMaterial.AIR : mat;
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		checkChunkLoaded();
		return (short) blockStore.getData(x & BASE_MASK, y & BASE_MASK, z & BASE_MASK);
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Source source) {
		if (source == null) {
			throw new NullPointerException("Source can not be null");
		}
		light &= 0xF;
		x &= BASE_MASK;
		y &= BASE_MASK;
		z &= BASE_MASK;

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
			//light increased
			getWorld().getLightingManager().blockLightGreater.add(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else if (light < oldLight) {
			//light decreased
			getWorld().getLightingManager().blockLightLesser.add(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else {
			return false;
		}
		if (SpoutConfiguration.LIVE_LIGHTING.getBoolean()) {
			this.setLightDirty(true);
		}
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
		x &= BASE_MASK;
		y &= BASE_MASK;
		z &= BASE_MASK;

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
			//light increased
			getWorld().getLightingManager().skyLightGreater.add(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else if (light < oldLight) {
			//light decreased
			getWorld().getLightingManager().skyLightLesser.add(x + this.getBlockX(), y + this.getBlockY(), z + this.getBlockZ());
		} else {
			return false;
		}
		if (SpoutConfiguration.LIVE_LIGHTING.getBoolean()) {
			this.setLightDirty(true);
		}
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

	@Override
	public void updateBlockPhysics(int x, int y, int z, Source source) {
		checkChunkLoaded();
		this.getRegion().updateBlockPhysics(x, y, z, source);
	}

	private int getBlockIndex(int x, int y, int z) {
		return (y & BASE_MASK) << 8 | (z & BASE_MASK) << 4 | (x & BASE_MASK);
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
	public boolean refreshObserver(Entity entity) {
		if (!entity.isObserver()) {
			throw new IllegalArgumentException("Cannot add an entity that isn't marked as an observer!");
		}
		checkChunkLoaded();
		parentRegion.unSkipChunk(this);
		TickStage.checkStage(TickStage.FINALIZE);
		int distance = (int) ((SpoutEntity) entity).getChunkLive().getBase().getDistance(getBase());
		Integer oldDistance = observers.put(entity, distance);
		if (oldDistance == null) {
			parentRegion.unloadQueue.remove(this);
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
		if (blockStore.needsCompression()) {
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
		return blockStore.isDirty();
	}

	public boolean isDirtyOverflow() {
		return blockStore.isDirtyOverflow();
	}

	protected Vector3 getDirtyBlock(int i) {
		return blockStore.getDirtyBlock(i);
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

	@Override
	public Biome getBiomeType(int x, int y, int z) {
		return biomes.getBiome(x & BASE_MASK, y & BASE_MASK, z & BASE_MASK);
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
	public Region getRegion() {
		return parentRegion;
	}

	@Override
	public void populate() {
		populate(false);
	}

	@Override
	public void populate(boolean force) {
		if (!this.populated.compareAndSet(false, true) && !force) {
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

		//this.initLighting(); //TODO: We aren't ready for this!
		//this.setLightDirty(false);
		parentRegion.onChunkPopulated(this);
	}

	@Override
	public boolean isPopulated() {
		return populated.get();
	}

	@Override
	public void initLighting() {
		SpoutWorld world = this.getWorld();
		int x, y, z, minY, maxY;
		Arrays.fill(this.blockLight, (byte) 0);
		Arrays.fill(this.skyLight, (byte) 0);

		//Initialize block lighting
		for (x = 0; x < CHUNK_SIZE; x++) {
			for (y = 0; y < CHUNK_SIZE; y++) {
				for (z = 0; z < CHUNK_SIZE; z++) {
					this.setBlockLight(x, y, z, this.getBlockMaterial(x, y, z).getLightLevel(this.getBlockData(x, y, z)), world);
				}
			}
		}

		//Report the columns that require a sky-light update
		maxY = this.getBlockY() + CHUNK_SIZE - 1;
		for (x = 0; x < CHUNK_SIZE; x++) {
			for (z = 0; z < CHUNK_SIZE; z++) {
				minY = this.column.getSurfaceHeight(x, z);
				//fill area above height with light
				for (y = minY; y <= maxY; y++) {
					this.setBlockSkyLight(x, y, z, (byte) 15, world);
				}
			}
		}

		this.setLightDirty(true);
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
	public void preSnapshot() {
		Map<Entity, Integer> observerSnapshot = observers.get();
		Map<Entity, Integer> observerLive = observers.getLive();

		//If we are observed and not populated, queue population
		if (!isPopulated() && observers.getLive().size() > 0) {
			parentRegion.queueChunkForPopulation(this);
		}

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
				//Player Network sync
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
		for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
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
		x = (x & BASE_MASK) + this.getBlockX();
		y = (y & BASE_MASK) + this.getBlockY();
		z = (z & BASE_MASK) + this.getBlockZ();
		return new SpoutBlock(this.getWorld(), x, y, z, this, source);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, BlockFullState expect, short data) {
		return this.blockStore.compareAndSetBlock(x & BASE_MASK, y & BASE_MASK, z & BASE_MASK, expect.getId(), expect.getData(), expect.getId(), data);
	}

	@Override
	public DefaultedMap<String, Serializable> getDataMap() {
		return dataMap;
	}
	
	public BiomeManager getBiomeManager() {
		return biomes;
	}

	/**
	 * True if the chunk is uniform air blocks
	 * 
	 * @return air
	 */
	public boolean isAir() {
		return false;
	}
}
