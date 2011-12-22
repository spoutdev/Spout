package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.unchecked.api.util.thread.SnapshotRead;

/**
 * Represents a cube containing 16x16x16 Chunks (256x256x256 Blocks)
 */
public abstract class Region extends Cube {

	private final static double EDGE = 256.0;

	public Region(World world, double x, double y, double z) {
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
}
