package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.unchecked.api.material.BlockMaterial;

/**
 * Represents a cube containing 16x16x16 Blocks
 */
public abstract class Chunk extends Cube {

	private final static double EDGE = 16.0;

	public Chunk(World world, double x, double y, double z) {
		super(new Point(world, x, y, z), EDGE);
	}
	
	/**
	 * Sets the block at (x, y, z) to the given material type and returns the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	@DelayedWrite
	public abstract BlockMaterial setBlockMaterial(int x, int y, int z, BlockMaterial material);
	
	/**
	 * Sets the id for the block at (x, y, z) to the given id and returns the snapshot value
	 * 
	 * For ids greater than 255, the id must represent a value custom id
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	@DelayedWrite
	public abstract short setBlockId(int x, int y, int z, short id);
	
	/**
	 * Gets the snapshot material for the block at (x, y, z)
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	public abstract BlockMaterial getBlockMaterial(int x, int y, int z);
	
	/**
	 * Gets the snapshot id for the block at (x, y, z)
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	public abstract short getBlockId(int x, int y, int z);
	
	/**
	 * Gets the live material for the block at (x, y, z)
	 * 
	 * Note: this may have a negative performance impact, relative to reading the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@LiveRead
	public abstract BlockMaterial getLiveBlockMaterial(int x, int y, int z);
	
	/**
	 * Gets the live id for the block at (x, y, z)
	 * 
	 * Note: this may have a negative performance impact, relative to reading the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@LiveRead
	public abstract BlockMaterial getLiveBlockId(int x, int y, int z);
	
}
