/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api;

import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.api.util.thread.Threadsafe;

public interface World {
	
	/**
	 * Gets the name of the world
	 * 
	 * @return the name of the world
	 */
	@SnapshotRead
	public String getName();
	
	/**
	 * Gets the number of ticks since the world was created.  This count cannot be modified, and increments by one on every tick
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
	 * Gets the UID representing the world.  With extremely high probability the UID is unique to each world.
	 * 
	 * @return the name of the world
	 */
	@SnapshotRead
	public String getUID();

}
