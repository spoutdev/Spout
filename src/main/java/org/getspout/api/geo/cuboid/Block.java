package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.material.BlockMaterial;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * Represents a cube with an edge length of 1.
 */
public abstract class Block extends Cube {

	private final static double EDGE = 1.0;

	public Block(World world, double x, double y, double z) {
		super(new Point(world, x, y, z), EDGE);
	}
	
	/**
	 * Sets the block to the given material type and returns the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	@DelayedWrite
	public abstract BlockMaterial setBlockMaterial(BlockMaterial material);
	
	/**
	 * Sets the id for the block to the given id and returns the snapshot value
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
	public abstract short setBlockId(short id);
	
	/**
	 * Gets the snapshot material for the block
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	public abstract BlockMaterial getBlockMaterial();
	
	/**
	 * Gets the snapshot id for the block
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the x coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	public abstract short getBlockId();
	
	/**
	 * Gets the live material for the block
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
	public abstract BlockMaterial getLiveBlockMaterial();
	
	/**
	 * Gets the live id for the block
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
	public abstract BlockMaterial getLiveBlockId();
}
