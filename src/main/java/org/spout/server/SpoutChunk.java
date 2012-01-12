/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server;

import gnu.trove.TCollections;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.map.hash.TShortShortHashMap;

import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.generator.Populator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.MaterialData;
import org.spout.api.player.Player;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.api.util.map.TNibbleTripleObjectHashMap;
import org.spout.api.util.map.TNibbleTripleShortHashMap;
import org.spout.server.util.thread.snapshotable.SnapshotManager;
import org.spout.server.util.thread.snapshotable.SnapshotableBoolean;
import org.spout.server.util.thread.snapshotable.SnapshotableShortArray;

public class SpoutChunk extends Chunk {

	/**
	 * Internal representation of block ids.
	 */
	private SnapshotableShortArray blockIds;
	
	/**
	 * Represents primitive block data
	 */
	private TNibbleTripleShortHashMap blockData;
	
	/**
	 * Represents complex block data 
	 */
	@SuppressWarnings("unused")
	private TNibbleTripleObjectHashMap<Serializable> complexData;

	/**
	 * The snapshot manager for the region that this chunk is located in.
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();
	
	/**
	 * Indicates that the chunk should be saved if unloaded
	 */
	private final AtomicReference<SaveState> saveState = new AtomicReference<SaveState>(SaveState.NONE);
	
	/**
	 * The parent region that manages this chunk
	 */
	private final Region parentRegion;
	
	/**
	 * Holds if the chunk is populated
	 */
	private SnapshotableBoolean populated;
	
	/**
	 * A set of all players who are observing this chunk
	 */
	private final HashSet<Player> observers = new HashSet<Player>();
	
	public SpoutChunk(World world, Region region, float x, float y, float z, short[] blockIds) {
		super(world, x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE);
		this.parentRegion = region;
		if (blockIds != null) {
			this.blockIds = new SnapshotableShortArray(snapshotManager, blockIds);
		}
		else {
			this.blockIds = new SnapshotableShortArray(snapshotManager, new short[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE]);
		}
		this.populated = new SnapshotableBoolean(snapshotManager, false);
		
		blockData = new TNibbleTripleShortHashMap(TCollections.synchronizedMap(new TShortShortHashMap()));
		complexData = new TNibbleTripleObjectHashMap<Serializable>(TCollections.synchronizedMap(new TShortObjectHashMap<Serializable>()));
	}
	
	public SpoutChunk(World world, Region region, float x, float y, float z) {
		this(world, region, x, y, z, null);
	}

	@Override
	public BlockMaterial setBlockMaterial(int x, int y, int z, BlockMaterial material) {
		checkChunkLoaded();
		setBlockId(x, y, z, (short) material.getId());
		setBlockData(x, y, z, (short) material.getData());
		return getBlockMaterial(x, y, z);
	}

	@Override
	public short setBlockId(int x, int y, int z, short id) {
		checkChunkLoaded();
		return blockIds.set((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF, id);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		checkChunkLoaded();
		short id = getBlockId(x, y, z);
		short data = getBlockData(x, y, z);
		return MaterialData.getBlock(id, data);
	}

	@Override
	public short getBlockId(int x, int y, int z) {
		checkChunkLoaded();
		return blockIds.get((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF);
	}


	@Override
	public short getBlockData(int x, int y, int z) {
		checkChunkLoaded();
		if (blockData.containsKey((byte)x, (byte)y, (byte)z)) {
			return blockData.get((byte)x, (byte)y, (byte)z);
		}
		return (short)0;
	}

	@Override
	public short setBlockData(int x, int y, int z, short data) {
		checkChunkLoaded();
		if (data == 0) {
			return blockData.remove((byte)x, (byte)y, (byte)z);
		}
		return blockData.put((byte)x, (byte)y, (byte)z, data);
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
	
	// TODO - use CuboidBuffer internally ?
	public CuboidShortBuffer getBlockCuboidBufferLive() {
		checkChunkLoaded();
		int x = getX() << Chunk.CHUNK_SIZE_BITS;
		int y = getY() << Chunk.CHUNK_SIZE_BITS;
		int z = getZ() << Chunk.CHUNK_SIZE_BITS;
		CuboidShortBuffer snapshot = new CuboidShortBuffer(getWorld(), x, y, z, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, this.blockIds.getLive());
		
		return snapshot;
	}

	@Override
	public boolean addObserver(Player player) {
		checkChunkLoaded();
		return observers.add(player);
	}

	@Override
	public boolean removeObserver(Player player) {
		checkChunkLoaded();
		boolean success = observers.remove(player);
		if (success) {
			if (observers.size() == 0) {
				this.unload(true);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isUnloaded() {
		return saveState.get() == SaveState.UNLOADED;
	}
	
	public void setUnloaded() {
		saveState.set(SaveState.UNLOADED);
		blockIds = null;
		blockData = null;
		complexData = null;
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
	}

	@Override
	public boolean isPopulated() {
		return populated.get();
	}

}
