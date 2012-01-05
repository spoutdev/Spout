package org.getspout.server;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.material.BlockMaterial;
import org.getspout.api.material.MaterialData;
import org.getspout.api.player.Player;
import org.getspout.api.util.cuboid.CuboidShortBuffer;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableByteArray;
import org.getspout.server.util.thread.snapshotable.SnapshotableShortArray;

public class SpoutChunk extends Chunk {

	/**
	 * Internal representation of block ids.
	 */
	private final SnapshotableShortArray blockIds;

	/**
	 * Internal representation of block data.
	 */
	private final SnapshotableByteArray blockData;

	/**
	 * The snapshot manager for the region that this chunk is located in.
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();
	
	/**
	 * Indicates that the chunk should be saved if unloaded
	 */
	private final AtomicReference<SaveState> saveState = new AtomicReference<SaveState>();
	
	/**
	 * The parent region that manages this chunk
	 */
	private final Region parentRegion;
	
	/**
	 * A set of all players who are observing this chunk
	 */
	private final HashSet<Player> observers = new HashSet<Player>();
	
	public SpoutChunk(World world, Region region, float x, float y, float z, short[] blockIds, byte[] data) {
		super(world, x, y, z);
		this.parentRegion = region;
		if (blockIds != null) {
			this.blockIds = new SnapshotableShortArray(snapshotManager, blockIds);
		}
		else {
			this.blockIds = new SnapshotableShortArray(snapshotManager, new short[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE]);
		}
		if (data != null) {
			this.blockData = new SnapshotableByteArray(snapshotManager, data);
		}
		else {
			this.blockData = new SnapshotableByteArray(snapshotManager, new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE]);
		}
	}
	
	public SpoutChunk(World world, Region region, float x, float y, float z) {
		this(world, region, x, y, z, null, null);
	}

	@Override
	public BlockMaterial setBlockMaterial(int x, int y, int z, BlockMaterial material) {
		setBlockId(x, y, z, (short) material.getRawId());
		return getBlockMaterial(x, y, z);
	}

	@Override
	public short setBlockId(int x, int y, int z, short id) {
		return blockIds.set((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF, id);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		short id = getBlockId(x, y, z);
		byte data = getBlockData(x, y, z);
		return MaterialData.getBlock(id, data);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z, boolean live) {
		short id = getBlockId(x, y, z, live);
		byte data = getBlockData(x, y, z, live);
		return MaterialData.getBlock(id, data);
	}

	@Override
	public short getBlockId(int x, int y, int z) {
		return blockIds.get((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF);
	}

	@Override
	public short getBlockId(int x, int y, int z, boolean live) {
		return live ? blockIds.getLive((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF) : blockIds.get((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF);
	}

	@Override
	public byte getBlockData(int x, int y, int z) {
		return blockData.get((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF);
	}

	@Override
	public byte getBlockData(int x, int y, int z, boolean live) {
		return live ? blockData.getLive((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF) : blockData.get((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF);
	}

	@Override
	public byte setBlockData(int x, int y, int z, byte data) {
		return blockData.set((x & 0xF) << 8 | (z & 0xF) << 4 | y & 0xF, data);
	}

	@Override
	public void unload(boolean save) {
		if (save) {
			saveState.set(SaveState.UNLOAD_SAVE);
		} else {
			saveState.set(SaveState.UNLOAD_DONT_SAVE);
		}
		((SpoutServer)getWorld().getServer()).markChunkForSave(this);
	}
	
	public SaveState getAndSetSaveState(SaveState newState) {
		return saveState.getAndSet(newState);
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
		int x = getX() << Chunk.CHUNK_SIZE_BITS;;
		int y = getY() << Chunk.CHUNK_SIZE_BITS;;
		int z = getZ() << Chunk.CHUNK_SIZE_BITS;
		CuboidShortBuffer snapshot = new CuboidShortBuffer(getWorld(), x, y, z, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);
		
		if (y < 0) {
			snapshot.flood((short)1);
		}
		
		return snapshot;
	}

	@Override
	public boolean addObserver(Player player) {
		return observers.add(player);
	}

	@Override
	public boolean removeObserver(Player player) {
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
	
	public static enum SaveState {
		UNLOAD_SAVE,
		UNLOAD_DONT_SAVE,
		SAVE,
		NONE;
		
		public boolean isSave() {
			return this == SAVE || this == UNLOAD_SAVE;
		}
		
		public boolean isUnload() {
			return this == UNLOAD_SAVE || this == UNLOAD_DONT_SAVE;
		}
	}
	
	@Override
	public Region getRegion() {
		return parentRegion;
	}

}
