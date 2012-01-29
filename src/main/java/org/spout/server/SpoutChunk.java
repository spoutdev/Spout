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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.basic.blocks.BlockFullState;
import org.spout.api.datatable.Datatable;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.entity.Entity;
import org.spout.api.generator.Populator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Blockm;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.MaterialData;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.api.util.map.concurrent.AtomicBlockStore;
import org.spout.server.entity.SpoutEntity;
import org.spout.server.util.thread.snapshotable.SnapshotManager;
import org.spout.server.util.thread.snapshotable.SnapshotableBoolean;
import org.spout.server.util.thread.snapshotable.SnapshotableHashMap;
import org.spout.server.util.thread.snapshotable.SnapshotableHashSet;

public class SpoutChunk extends Chunk {

	/**
	 * Storage for block ids, data and auxiliary data.  
	 * For blocks with data = 0 and auxiliary data = null, the block is stored as a short.
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
	 * A set of all players who are observing this chunk
	 */
	private final SnapshotableHashMap<Player, Integer> observers = new SnapshotableHashMap<Player, Integer>(snapshotManager);
	
	/**
	 * A set of entities contained in the chunk
	 */
	// Hash set should return "dirty" list
	private final SnapshotableHashSet<Entity> entities = new SnapshotableHashSet<Entity>(snapshotManager);

	/**
	 * The mask that should be applied to the x, y and z coords
	 */
	private final int coordMask;
	
	public SpoutChunk(World world, SpoutRegion region, float x, float y, float z, short[] initial) {
		super(world, x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE);
		coordMask = Chunk.CHUNK_SIZE - 1;
		this.parentRegion = region;
		this.blockStore = new AtomicBlockStore<DatatableMap>(Chunk.CHUNK_SIZE_BITS);
		this.populated = new SnapshotableBoolean(snapshotManager, false);
		int i = 0;
		for (int xx = 0; xx < Chunk.CHUNK_SIZE; xx++) {
			for (int zz = 0; zz < Chunk.CHUNK_SIZE; zz++) {
				for (int yy = 0; yy < Chunk.CHUNK_SIZE; yy++) {
					blockStore.setBlock(xx, yy, zz, initial[i++], (short)0, null);
				}
			}
		}
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, Source source) {
		if (material == null) throw new NullPointerException("Material can not be null");
		return setBlockIdAndData(x, y, z, material.getId(), (short)0, true, source);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, boolean updatePhysics, Source source) {
		if (material == null) throw new NullPointerException("Material can not be null");
		return setBlockIdAndData(x, y, z, material.getId(), material.getData(), updatePhysics, source);
	}

	@Override
	public boolean setBlockId(int x, int y, int z, short id, Source source) {
		return setBlockIdAndData(x, y, z, id, (short)0, true, source);
	}
	
	@Override
	public boolean setBlockId(int x, int y, int z, short id, boolean updatePhysics, Source source) {
		return setBlockIdAndData(x, y, z, id, (short)0, updatePhysics, source);
	}
	
	@Override
	public boolean setBlockIdAndData(int x, int y, int z, short id, short data, Source source) {
		return setBlockIdAndData(x, y, z, id, data, true, source);
	}
	
	@Override
	public boolean setBlockIdAndData(int x, int y, int z, short id, short data, boolean updatePhysics, Source source) {
		if (source == null) throw new NullPointerException("Source can not be null");
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
		return (short)blockStore.getBlockId(x & coordMask, y & coordMask, z & coordMask);
	}


	@Override
	public short getBlockData(int x, int y, int z) {
		checkChunkLoaded();
		return (short)blockStore.getData(x & coordMask, y & coordMask, z & coordMask);
	}
	
	@Override
	public void updatePhysics(int x, int y, int z) {
		checkChunkLoaded();
		parentRegion.queuePhysicsUpdate(x, y, z);
	}
	
	@Override
	public boolean compareAndRemove(int x, int y, int z, BlockFullState<DatatableMap> expect, String key, Datatable auxData){
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
				nextState = SaveState.UNLOAD_SAVE; break;
			case UNLOAD: 
				nextState = save ? SaveState.UNLOAD_SAVE : SaveState.UNLOAD; break;
			case SAVE: 
				nextState = SaveState.UNLOAD_SAVE; break;
			case NONE: 
				nextState = save ? SaveState.UNLOAD_SAVE : SaveState.UNLOAD; break;
			case UNLOADED:
				nextState = SaveState.UNLOADED; break;
			default: throw new IllegalStateException("Unknown save state: " + state);
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
		((SpoutRegion)parentRegion).markForSaveUnload(this);
	}
	
	public void saveNoMark() {
		boolean success = false;
		while (!success) {
			SaveState state = saveState.get();
			SaveState nextState;
			switch (state) {
				case UNLOAD_SAVE: 
					nextState = SaveState.UNLOAD_SAVE; break;
				case UNLOAD: 
					nextState = SaveState.UNLOAD_SAVE; break;
				case SAVE: 
					nextState = SaveState.SAVE; break;
				case NONE: 
					nextState = SaveState.SAVE; break;
				case UNLOADED:
					nextState = SaveState.UNLOADED; break;
				default: throw new IllegalStateException("Unknown save state: " + state);
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
	
	public CuboidShortBuffer getBlockCuboidBufferLive() {
		checkChunkLoaded();
		int x = getX() << Chunk.CHUNK_SIZE_BITS;
		int y = getY() << Chunk.CHUNK_SIZE_BITS;
		int z = getZ() << Chunk.CHUNK_SIZE_BITS;
		CuboidShortBuffer snapshot = new CuboidShortBuffer(getWorld(), x, y, z, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, blockStore.getBlockIdArray());
		
		return snapshot;
	}

	@Override
	public boolean refreshObserver(Player player) {
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
		int distance = (int)player.getEntity().getChunkLive().getBase().getDistance(this.getBase());
		Integer oldDistance = observers.put(player, distance);
		if(oldDistance == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeObserver(Player player) {
		checkChunkLoaded();
		TickStage.checkStage(TickStage.FINALIZE);
		Integer oldDistance = observers.remove(player);
		if (oldDistance != null) {
			if (observers.isEmptyLive()) {
				this.unload(true);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public Set<Player> getObserversLive() {
		return observers.getLive().keySet();
	}
	
	public Set<Player> getObservers() {
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
	public boolean isUnloaded() {
		return saveState.get() == SaveState.UNLOADED;
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
	
	public static class ChunkAccessException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ChunkAccessException(String message) {
			super(message);
		}
		
		
	}

	@Override
	public void populate() {
		populate(false);
	}

	@Override
	public void populate(boolean force) {
		if(isPopulated() && !force) {
			return;
		}
		for (Populator populator:getWorld().getGenerator().getPopulators()) {
			try {
				populator.populate(this);
			} catch(Exception e) {
				Spout.getGame().getLogger().log(Level.SEVERE, "Could not populate Chunk with "+populator.toString());
				e.printStackTrace();
			}
		}
		populated.set(true);
		if(getRegion() instanceof SpoutRegion) {
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
	
	public Set<Entity> getEntities() {
		return entities.get();
	}
	
	public Set<Entity> getLiveEntities() {
		return entities.getLive();
	}
	
	// Handles network updates for all entities that were
	// - in the chunk at the last snapshot
	// - were not in a chunk at the last snapshot and are now in this chunk
	public void preSnapshot() {
		Map<Player, Integer> observerSnapshot = observers.get();
		Map<Player, Integer> observerLive = observers.getLive();
		
		Set<Entity> entitiesSnapshot = entities.get();
		Set<Entity> entitiesLive = entities.getLive();
		
		// Changed means entered/left the chunk
		List<Entity> changedEntities = entities.getDirtyList();
		List<Player> changedPlayer = observers.getDirtyList();

		if (entitiesSnapshot.size() > 0) {
			for (Player p : changedPlayer) {
				Integer playerDistanceOld = observerSnapshot.get(p);
				if (playerDistanceOld == null) {
					playerDistanceOld = Integer.MAX_VALUE;
				}
				Integer playerDistanceNew = observerLive.get(p);
				if (playerDistanceNew == null) {
					playerDistanceNew = Integer.MAX_VALUE;
				}
				NetworkSynchronizer n = p.getNetworkSynchronizer();
				for (Entity e : entitiesSnapshot) {
					if (p.getEntity().equals(e)) {
						continue;
					}
					Integer entityViewDistanceOld = e.getViewDistance();
					Integer entityViewDistanceNew = e.getViewDistanceLive();

					if (playerDistanceOld <= entityViewDistanceOld && playerDistanceNew > entityViewDistanceNew) {
						n.destroyEntity(e);
					} else if (playerDistanceNew <= entityViewDistanceNew && playerDistanceOld > entityViewDistanceOld) {
						n.spawnEntity(e);
					}
				}
			}
		}
		
		for (Entity e : changedEntities) {
			SpoutChunk oldChunk = ((SpoutChunk)e.getChunk());
			if (((SpoutEntity)e).justSpawned()) {
				oldChunk = null;
			}
			SpoutChunk newChunk = ((SpoutChunk)e.getChunkLive());
			if (!(oldChunk != null && oldChunk.equals(this)) && !(((SpoutEntity)e).justSpawned())) {
				continue;
			}
			for (Player p : observerLive.keySet()) {
				if (p.getEntity().equals(e)) {
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
				Integer entityViewDistanceOld = e.getViewDistance();
				Integer entityViewDistanceNew = e.getViewDistanceLive();
				
				NetworkSynchronizer n = p.getNetworkSynchronizer();
				
				if (playerDistanceOld <= entityViewDistanceOld && playerDistanceNew > entityViewDistanceNew) {
					n.destroyEntity(e);
				} else if (playerDistanceNew <= entityViewDistanceNew && playerDistanceOld > entityViewDistanceOld) {
					n.spawnEntity(e);
				}
			}
		}

		// Update all entities that are in the chunk
		// TODO - should have sorting based on view distance
		for (Map.Entry<Player, Integer> entry : observerLive.entrySet()) {
			Player p = entry.getKey();
			NetworkSynchronizer n = p.getNetworkSynchronizer();
			if (n != null) {
				int playerDistance = entry.getValue();
				Entity playerEntity = p.getEntity();
				for (Entity e : entitiesSnapshot) {
					if (playerEntity != e) {
						if (playerDistance <= e.getViewDistance()) {
							if (e.getController() != e.getLiveController()) {
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
					} else if (((SpoutEntity)e).justSpawned()) {
						if (playerEntity != e) {
							if (playerDistance <= e.getViewDistance()) {
								if (e.getController() != e.getLiveController()) {
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

	private String listObservers(Set<Player> players) {
		StringBuilder sb = new StringBuilder("{ ");
		for (Player p : players) {
			sb.append(p.getName() + " ");
		}
		sb.append("}");
		return sb.toString();
	}
	
}
