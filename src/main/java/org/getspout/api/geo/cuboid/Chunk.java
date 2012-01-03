package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.BlockAccess;
import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;

/**
 * Represents a cube containing 16x16x16 Blocks
 */
public abstract class Chunk extends Cube implements BlockAccess {

	/**
	 * Internal size of a side of a chunk
	 */
	public final static int CHUNK_SIZE = 16;
	
	/**
	 * Number of bits on the side of a chunk
	 */
	public final static int CHUNK_SIZE_BITS = 4;

	public Chunk(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), CHUNK_SIZE);
	}
	
	/**
	 * Performs the necessary tasks to unload this chunk from the world.
	 * 
	 * @param save whether the chunk data should be saved.
	 */
	public abstract void unload(boolean save);
	
}
