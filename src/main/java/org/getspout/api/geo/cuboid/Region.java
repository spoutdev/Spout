package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * Represents a cube containing 16x16x16 Chunks (256x256x256 Blocks)
 */
public abstract class Region extends Cube {
	
	/**
	 * Number of chunks on a side of a region
	 */
	public static final int REGION_SIZE = 16;
	
	/**
	 * Number of bits in {@link #REGION_SIZE}
	 */
	public static final int REGION_SIZE_BITS = 4;

	/**
	 * Number of blocks on a side of a region
	 */
	public final static int EDGE = 256;

	public Region(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), EDGE);
	}
	
	/**
	 * Gets the chunk at (x, y, z)
	 * 
	 * @param x the chunk x coordinate
	 * @param y the chunk y coordinate
	 * @param z the chunk z coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	public abstract Chunk getChunk(int x, int y, int z);
	
	/**
	 * Performs the nessecary tasks to unload this region from the world, and all associated chunks.
	 * 
	 * @param save whether to save the region and associated data.
	 */
	public abstract void unload(boolean save);
}
