package org.getspout.api.geo;

import org.getspout.api.event.EventSource;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.api.util.thread.Threadsafe;

/**
 * Represents a World.
 */
public interface World extends EventSource {

	/**
	 * Gets the name of the world
	 *
	 * @return the name of the world
	 */
	@SnapshotRead
	public String getName();

	/**
	 * Gets the number of ticks since the world was created. This count cannot
	 * be modified, and increments by one on every tick
	 *
	 * @return the world's tick count
	 */
	@SnapshotRead
	public long getAge();

	/**
	 * Gets the current time of day within the world
	 *
	 * This value wraps around every 24000 ticks
	 *
	 * @return the time of day
	 */
	@SnapshotRead
	public int getTime();

	/**
	 * Sets the current time of day within the world
	 *
	 * This value must be positive and less than the day length for the world.
	 *
	 */
	@DelayedWrite
	public int setTime(int time);

	/**
	 * Gets the current day length for the world
	 *
	 * This value wraps around every 24000 ticks
	 *
	 * @return the time of day
	 */
	@SnapshotRead
	public int getDayLength();

	/**
	 * Sets the current day length for the world
	 *
	 * This value must be positive and less than the day length for the world.
	 *
	 */
	@SnapshotRead
	public int setDayLength(int time);

	/**
	 * Gets a Block representing a particular location in the world
	 *
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(int x, int y, int z);
	
	/**
	 * Gets a Block representing a particular point in the world
	 * 
	 * @param point The point
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(Point point);

	/**
	 * Gets the UID representing the world. With extremely high probability the
	 * UID is unique to each world.
	 *
	 * @return the name of the world
	 */
	@SnapshotRead
	public String getUID();
	
	/**
	 * Gets the region at (x, y, z) to the given material type and returns the snapshot value
	 * 
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @param material
	 * @return the chunk
	 */
	@SnapshotRead
	public abstract Chunk getRegion(int x, int y, int z);

}
