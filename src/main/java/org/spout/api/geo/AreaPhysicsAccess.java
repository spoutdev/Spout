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
package org.spout.api.geo;

import org.spout.api.Source;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.range.EffectRange;
import org.spout.api.util.thread.DelayedWrite;

public interface AreaPhysicsAccess {

	/**
	 * Resets all dynamic material updates queued for the given location. This list is checked during the finalize part of the tick, and will cause the onPlacement method to be called.<br>
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	@DelayedWrite
	public void resetDynamicBlock(int x, int y, int z);
	
	/**
	 * Queues a dynamic material updated for the given location. This list is checked during the finalize part of the tick, and will cause the update method to be called.<br>
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the old update for that block at that time instant, or null if none
	 */
	@DelayedWrite
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z);
	
	/**
	 * Queues a dynamic material updated for the given location. This list is checked during the finalize part of the tick, and will cause the update method to be called.<br>
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param nextUpdate the update time
	 * @return the old update for that block at that time instant, or null if none
	 */
	@DelayedWrite
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate);
	
	/**
	 * Queues a dynamic material updated for the given location. This list is checked during the finalize part of the tick, and will cause the update method to be called.<br>
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param nextUpdate the update time
	 * @param hint a non-persistent hint for the update
	 * @return the old update for that block at that time instant, or null if none
	 */
	@DelayedWrite
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, Object hint);
	
	/**
	 * Queues a dynamic material updated for the given location. This list is checked during the finalize part of the tick, and will cause the update method to be called.<br>
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param nextUpdate the update time
	 * @param data persistent data to be used for the update
	 * @param hint a non-persistent hint for the update
	 * @return the old update for that block at that time instant, or null if none
	 */
	@DelayedWrite
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, Object hint);
	
	
	/**
	 * Queues a physics update for the block at (x, y, z) and all blocks within the given range.  
	 * This is equivalent to changing the block's material or data, and can be called from any thread.
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param source of this physics update
	 */
	public void queueBlockPhysics(int x, int y, int z, EffectRange range, Source source);
	
	/**
	 * Synchronously queues a physics update for the block at (x, y, z).  This can only be called during the 
	 * dynamic update/physics update part of the tick and from the Region thread.
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param source of this physics update
	 */
	public void updateBlockPhysics(int x, int y, int z, Source source);
}
