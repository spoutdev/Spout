package org.getspout.server;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;

public class SpoutRegion extends Region{
	public Chunk[][][] chunks = new Chunk[Region.REGION_SIZE][Region.REGION_SIZE][Region.REGION_SIZE];
	
	/**
	 * Coordinates of the lower, left start of the region. Add {@link RegionSource#REGION_SIZE} to the coords to get the upper right end of the region.
	 */
	private final int x, y, z;
	
	/**
	 * The source of this region
	 */
	private final RegionSource source;
	
	/**
	 * Snapshot manager for this region
	 */
	private SnapshotManager snapshotManager = new SnapshotManager();

	public SpoutRegion(World world, float x, float y, float z, RegionSource source) {
		super(world, x, y, z);
		this.x = (int)Math.floor(x);
		this.y = (int)Math.floor(y);
		this.z = (int)Math.floor(z);
		this.source = source;
	}
	
	//TODO region threads?
	public void pulse() {
		snapshotManager.copyAllSnapshots();
	}

	@Override
	public Chunk getChunk(int x, int y, int z) {
		if (x < Region.REGION_SIZE && x > 0 && y < Region.REGION_SIZE && y > 0 && z < Region.REGION_SIZE && z > 0) {
			return chunks[x][y][z];
		}
		return null;
	}

	@Override
	public void unload(boolean save) {
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[x][y][z];
					if (chunk != null) {
						chunk.unload(save);
					}
				}
			}
		}
		//Ensure this region is removed from the source. This may be calling the parent method twice, but is harmless.
		source.unloadRegion(x, y, z, save);
	}

}
