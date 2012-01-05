package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.BlockAccess;
import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.player.Player;
import org.getspout.api.util.cuboid.CuboidShortBuffer;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;

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
	
	/**
	 * Mask to convert a block integer coordinate into the chunk's base
	 */
	public final static int BASE_MASK = -CHUNK_SIZE;

	public Chunk(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), CHUNK_SIZE);
	}
	
	/**
	 * Performs the necessary tasks to unload this chunk from the world.
	 * 
	 * @param save whether the chunk data should be saved.
	 */
	public abstract void unload(boolean save);
	
	/**
	 * Performs the necessary tasks to save this chunk.
	 * 
	 * @param save whether the chunk data should be saved.
	 */
	public abstract void save();
	
	/**
	 * Gets a snapshot of the live block id data for the chunk.
	 * 
	 * This process may result in tearing if called during potential updates
	 * 
	 * @return the snapshot
	 */
	@LiveRead
	public abstract CuboidShortBuffer getBlockCuboidBufferLive();
	
	/**
	 * Register a player as observing the chunk.  
	 * 
	 * @param player the player
	 * @return false if the player was already observing the chunk
	 */
	@DelayedWrite
	public abstract boolean addObserver(Player player);
	
	/**
	 * De-register a player as observing the chunk.  
	 * 
	 * @param player the player
	 * @return true if the player was observing the chunk
	 */
	@DelayedWrite
	public abstract boolean removeObserver(Player player);
	
	/**
	 * Gets the region that this chunk is located in
	 * 
	 * @return
	 */
	public abstract Region getRegion();
	
	public static Point pointToBase(Point p) {
		return new Point(p.getWorld(), ((int)p.getX()) & BASE_MASK, ((int)p.getY()) & BASE_MASK, ((int)p.getZ()) & BASE_MASK);
	}
	
}
