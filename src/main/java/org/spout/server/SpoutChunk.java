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

import gnu.trove.set.hash.TByteHashSet;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.basic.blocks.BlockFullState;
import org.spout.api.datatable.Datatable;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.geo.cuboid.Blockm;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.MaterialData;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.HashUtil;
import org.spout.api.util.map.concurrent.AtomicBlockStore;
import org.spout.api.util.map.concurrent.AtomicShortArray;
import org.spout.server.entity.SpoutEntity;
import org.spout.server.util.thread.snapshotable.SnapshotManager;
import org.spout.server.util.thread.snapshotable.SnapshotableBoolean;
import org.spout.server.util.thread.snapshotable.SnapshotableHashMap;
import org.spout.server.util.thread.snapshotable.SnapshotableHashSet;

public class SpoutChunk extends Chunk {

	/**
	 * Storage for block ids, data and auxiliary data. For blocks with data = 0
	 * and auxiliary data = null, the block is stored as a short.
	 */
	private AtomicBlockStore<DatatableMap> blockStore;

	/**
	 * Indicates that the chunk should be saved if unloaded
	 */
	private final AtomicReference<SaveState> saveState = new AtomicReference<SaveState>(SaveState.NONE);

	/**
	 * The parent region that manages this chunk
	 */
	private final SpoutRegion parentRegion;

	/**
	 * Holds if the chunk is populated
	 */
	private SnapshotableBoolean populated;

	/**
	 * Snapshot Manager
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * A set of all entities who are observing this chunk
	 */
	private final SnapshotableHashMap<Entity, Integer> observers = new SnapshotableHashMap<Entity, Integer>(snapshotManager);

	/**
	 * A set of entities contained in the chunk
	 */
	// Hash set should return "dirty" list
	private final SnapshotableHashSet<Entity> entities = new SnapshotableHashSet<Entity>(snapshotManager);

	/**
	 * Stores a short value of the sky light
	 */
	//TODO: short? WHY?
	private final AtomicShortArray skyLight;
	private final AtomicShortArray blockLight;
	
	/**
	 * Stores queued column updates for skylight to be processed at the next tick
	 */
	private final TByteHashSet skyLightQueue;
	/**
	 * Stores queued column updates for block light to be processed at the next tick
	 */
	private final TByteHashSet blockLightQueue;

	/**
	 * The mask that should be applied to the x, y and z coords
	 */
	private final int coordMask;

	public SpoutChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, short[] initial) {
		super(world, x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE);
		coordMask = Chunk.CHUNK_SIZE - 1;
		parentRegion = region;
		blockStore = new AtomicBlockStore<DatatableMap>(Chunk.CHUNK_SIZE_BITS, initial);
		populated = new SnapshotableBoolean(snapshotManager, false);
		skyLight = new AtomicShortArray(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE);
		blockLight = new AtomicShortArray(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE);
		skyLightQueue = new TByteHashSet();
		blockLightQueue  = new TByteHashSet();
		
		//Recalculate lighting
		for (int dx = CHUNK_SIZE - 1; dx >= 0; --dx) {
			for (int dz = CHUNK_SIZE - 1; dz >= 0; --dz) {
				recalculateLighting(dx, dz, true, true);
			}
		}
	}

	public SpoutWorld getWorld() {
		return (SpoutWorld) super.getWorld();
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, Source source) {
		if (material == null) {
			throw new NullPointerException("Material can not be null");
		}
		return setBlockIdAndData(x, y, z, material.getId(), material.getData(), true, source);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, boolean updatePhysics, Source source) {
		if (material == null) {
			throw new NullPointerException("Material can not be null");
		}
		return setBlockIdAndData(x, y, z, material.getId(), material.getData(), updatePhysics, source);
	}

	@Override
	public boolean setBlockId(int x, int y, int z, short id, Source source) {
		return setBlockIdAndData(x, y, z, id, (short) 0, true, source);
	}

	@Override
	public boolean setBlockId(int x, int y, int z, short id, boolean updatePhysics, Source source) {
		return setBlockIdAndData(x, y, z, id, (short) 0, updatePhysics, source);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Source source) {
		return setBlockData(x, y, z, data, true, source);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, boolean updatePhysics, Source source) {
		return setBlockIdAndData(x, y, z, (short) blockStore.getBlockId(x & coordMask, y & coordMask, z & coordMask), data, updatePhysics, source);
	}

	@Override
	public boolean setBlockIdAndData(int x, int y, int z, short id, short data, Source source) {
		return setBlockIdAndData(x, y, z, id, data, true, source);
	}

	@Override
	public boolean setBlockIdAndData(int x, int y, int z, short id, short data, boolean updatePhysics, Source source) {
		if (source == null) {
			throw new NullPointerException("Source can not be null");
		}
		checkChunkLoaded();
		blockStore.setBlock(x & coordMask, y & coordMask, z & coordMask, id, data, null);

		//do neighbor updates
		if (updatePhysics) {
			updatePhysics(x, y, z);

			//South and North
			updatePhysics(x + 1, y, z);
			updatePhysics(x - 1, y, z);

			//West and East
			updatePhysics(x, y, z + 1);
			updatePhysics(x, y, z - 1);

			//Above and Below
			updatePhysics(x, y + 1, z);
			updatePhysics(x, y - 1, z);
		}
		updateLighting(x, y, z);
		return true;
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		checkChunkLoaded();
		BlockFullState<DatatableMap> fullState = blockStore.getFullData(x & coordMask, y & coordMask, z & coordMask);
		short id = fullState.getId();
		short data = fullState.getData();
		DatatableMap auxData = fullState.getAuxData();
		return MaterialData.getBlock(id, data, auxData);
	}

	@Override
	public short getBlockId(int x, int y, int z) {
		checkChunkLoaded();
		return (short) blockStore.getBlockId(x & coordMask, y & coordMask, z & coordMask);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		checkChunkLoaded();
		return (short) blockStore.getData(x & coordMask, y & coordMask, z & coordMask);
	}

	@Override
	public short getSkyLight(int x, int y, int z) {
		checkChunkLoaded();
		return skyLight.get(toIndex(x & coordMask, y & coordMask, z & coordMask));
	}

	@Override
	public short getBlockLight(int x, int y, int z) {
		checkChunkLoaded();
		return blockLight.get(toIndex(x & coordMask, y & coordMask, z & coordMask));
	}

	@Override
	public void updatePhysics(int x, int y, int z) {
		checkChunkLoaded();
		SpoutRegion region = parentRegion.getWorld().getRegionFromBlock(x, y, z);
		if (region != null) {
			region.queuePhysicsUpdate(x, y, z);
		}
	}

	private int toIndex(int x, int y, int z) {
		return (y & coordMask) << 8 | (z & coordMask) << 4 | (x & coordMask);
	}
	
	/**
	 * Recalculates the lighting for the blocks in this column
	 * 
	 * May queue more lighting updates in chunks underneath.
	 */
	private void recalculateLighting(int x, int z, boolean sky, boolean block) {
		if (sky) {
			recalculateSkyLighting(x, z);
		}
		if (block) {
			recalculateBlockLighting(x, z);
		}
	}
	
	/**
	 * Recalculates the sky light in the x, z column.
	 * 
	 * May queue more lighting updates in chunks underneath.
	 * 
	 * @param x coordinate
	 * @param z coordinate
	 */
	private void recalculateSkyLighting(int x, int z) {
		SpoutChunk aboveChunk = getWorld().getChunk(getX(), getY() + 1, getZ(), false);
		short prevValue;
		if (aboveChunk != null) {
			//find the sunlight shining through the bottom of the chunk above us
			prevValue = aboveChunk.getSkyLight(x, 0, z);
		} else {
			//assume the sun is shining through ungenerated air
			prevValue = 0xF;
		}
		for (int y = CHUNK_SIZE - 1; y >= 0; --y) {
			//Don't check the opacity unless there is some light
			if (prevValue > 0) {
				BlockMaterial type = getBlockMaterial(x, y, z);
				prevValue -= type.getOpacity();
				if (prevValue < 0) {
					prevValue = 0;
				}
			}
			skyLight.set(toIndex(x, y, z), prevValue);
		}

		SpoutChunk belowChunk = getWorld().getChunk(getX(), getY() - 1, getZ(), false);
		if (belowChunk != null) {
			if (belowChunk.getSkyLight(x, CHUNK_SIZE - 1, z) != prevValue) {
				belowChunk.queueLightUpdate(x, z, true, false); 
			}
		}
	}
	
	/**
	 * Recalculates the block light in the x, z column
	 * 
	 * May queue more lighting updates in neighbor chunks.
	 * 
	 * @param x coordinate
	 * @param z coordinate
	 */
	private void recalculateBlockLighting(int x, int z) {
		//TODO: this is wrong
		//Block light has to spread out from the source
		//And decay 1 for each block away it is from the source
		//But this does not do that
		//for (int y = CHUNK_SIZE - 1; y >= 0; --y) {
		//	BlockMaterial type = getBlockMaterial(x, y, z);
		//	blockLight.set(toIndex(x, y, z), type.getLightLevel());
		//}
	}
	
	private void queueLightUpdate(int x, int z, boolean sky, boolean block) {
		if (sky) {
			synchronized(skyLightQueue) {
				skyLightQueue.add(HashUtil.nibbleToByte(x & 0xF, z & 0xF));
			}
		}
		if (block) {
			synchronized(blockLightQueue) {
				blockLightQueue.add(HashUtil.nibbleToByte(x & 0xF, z & 0xF));
			}
		}
	}

	protected void updateLighting(int x, int y, int z) {
		final int index = toIndex(x, y, z);
		SpoutChunk aboveChunk = getWorld().getChunk(getX(), getY() + 1, getZ(), false);
		short oldSkyLight;
		if (aboveChunk == null) {
			oldSkyLight = skyLight.get(index);
		}  else {
			oldSkyLight = aboveChunk.getSkyLight(x, 0, z);
		}

		for (int dy = y; dy >= 0; --dy) {
			oldSkyLight = (short)Math.max(0, oldSkyLight - getBlockMaterial(x, dy, z).getOpacity());
			skyLight.set(toIndex(x, dy, z), oldSkyLight);
			if (oldSkyLight == 0) {
				if (dy == 0) {
					break;
				} else {
					return;
				}
			}
		}

		SpoutChunk belowChunk = getWorld().getChunk(getX(), getY() - 1, getZ(), false);
		if (belowChunk != null) {
			belowChunk.queueLightUpdate(x, z, true, false);
		}
	}

	@Override
	public boolean compareAndRemove(int x, int y, int z, BlockFullState<DatatableMap> expect, String key, Datatable auxData) {
		throw new UnsupportedOperationException("TBD");
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, BlockFullState<DatatableMap> expect, short data) {
		throw new UnsupportedOperationException("TBD");
		//return blockStore.compareAndSetData(x & coordMask, y & coordMask, z & coordMask, expect, data);
	}

	@Override
	public boolean compareAndPut(int x, int y, int z, BlockFullState<DatatableMap> expect, String key, Datatable auxData) {
		throw new UnsupportedOperationException("TBD");
		/*while (true) {
			BlockFullState<DatatableMap> fullState = blockStore.getFullData(x & coordMask, y & coordMask, z & coordMask);
			if (!fullState.equals(expect)) {
				return false;
			}
			BlockFullState<DatatableMap> newFullState = fullState.shallowClone();
			if (newFullState.getAuxData() == null) {
				newFullState.setAuxData(new SpoutDatatableMap());
			}
		}*/
	}

	@Override
	public void unload(boolean save) {
		checkChunkLoaded();
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

	public void copySnapshotRun() throws InterruptedException {
		snapshotManager.copyAllSnapshots();
	}

	// Saves the chunk data - this occurs directly after a snapshot update
	public void syncSave() {
		// TODO
	}

	@Override
	public ChunkSnapshot getSnapshot() {
		return getSnapshot(true);
	}

	@Override
	public ChunkSnapshot getSnapshot(boolean entities) {
		checkChunkLoaded();
		return new SpoutChunkSnapshot(this, blockStore.getBlockIdArray(), blockStore.getDataArray(), blockLight.getArray(), skyLight.getArray(), entities);
	}

	@Override
	public boolean refreshObserver(Entity entity) {
		if(!entity.isObserver()) throw new IllegalArgumentException("Cannot add an entity that isn't marked as an observer!");
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
		int distance = (int) ((SpoutEntity)entity).getChunkLive().getBase().getDistance(getBase());
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

	public boolean isDirty() {
		return blockStore.isDirty();
	}

	public boolean isDirtyOverflow() {
		return blockStore.isDirtyOverflow();
	}

	public Blockm getDirtyBlock(int i, Blockm blockm) {
		return blockStore.getDirtyBlock(i, blockm);
	}

	public void resetDirtyArrays() {
		blockStore.resetDirtyArrays();
	}

	@Override
	public boolean isLoaded() {
		return saveState.get() != SaveState.UNLOADED;
	}

	public void setUnloaded() {
		saveState.set(SaveState.UNLOADED);
		blockStore = null;
	}

	private void checkChunkLoaded() {
		if (saveState.get() == SaveState.UNLOADED) {
			throw new ChunkAccessException("Chunk has been unloaded");
		}
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
		if (isPopulated() && !force) {
			return;
		}

		final Random random = new Random(WorldGeneratorUtils.getSeed(getWorld(), getX(), getY(), getZ(), 42));

		for (Populator populator : getWorld().getGenerator().getPopulators()) {
			try {
				populator.populate(this, random);
			} catch(Exception e) {
				Spout.getGame().getLogger().log(Level.SEVERE, "Could not populate Chunk with " + populator.toString());
				e.printStackTrace();
			}
		}

		populated.set(true);
		if (getRegion() instanceof SpoutRegion) {
			((SpoutRegion) getRegion()).onChunkPopulated(this);
		}
	}

	@Override
	public boolean isPopulated() {
		return populated.get();
	}

	public boolean addEntity(SpoutEntity entity) {
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
		return entities.add(entity);
	}

	public boolean removeEntity(SpoutEntity entity) {
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
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
		if(!isPopulated() && observers.getLive().size() > 0){
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
				if(p.getController() instanceof PlayerController){
					Player player = ((PlayerController)p.getController()).getPlayer();

					NetworkSynchronizer n = player.getNetworkSynchronizer();
					for (Entity e : entitiesSnapshot) {
						if (player.getEntity().equals(e)) {
							continue;
						}
						int entityViewDistanceOld = ((SpoutEntity)e).getPrevViewDistance();
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
			SpoutChunk newChunk = (SpoutChunk) ((SpoutEntity)e).getChunkLive();
			if (!(oldChunk != null && oldChunk.equals(this)) && !((SpoutEntity) e).justSpawned()) {
				continue;
			}
			for (Entity p : observerLive.keySet()) {
				if (p == null || p.equals(e)) {
					continue;
				}
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
				int entityViewDistanceOld = ((SpoutEntity)e).getPrevViewDistance();
				int entityViewDistanceNew = e.getViewDistance();

				if(p.getController() instanceof PlayerController){
					Player player = ((PlayerController) p.getController()).getPlayer();
					NetworkSynchronizer n = player.getNetworkSynchronizer();

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
			if(p.getController() instanceof PlayerController){
				Player player = ((PlayerController)p.getController()).getPlayer();
				NetworkSynchronizer n = player.getNetworkSynchronizer();
				if (n != null) {
					int playerDistance = entry.getValue();
					Entity playerEntity = p;
					for (Entity e : entitiesSnapshot) {
						if (playerEntity != e) {
							if (playerDistance <= e.getViewDistance()) {
								if (((SpoutEntity)e).getPrevController() != e.getController()) {
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
									if (((SpoutEntity)e).getPrevController() != e.getController()) {
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

}
