/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.material;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.math.Vector3;

public interface DynamicBlockMaterial {
	
	/**
	 * Gets the maximum range, r, for the range of effect of this dynamic block.<br>
	 * <br>
	 * The cuboid is defined by a Vector3 array.<br>
	 * <br>
	 * Assuming rh is the first element of the array, rl is the second, and x, y and z are the coordinates of the dynamic block, 
	 * then all effects are restricted to the cuboid from<br>
	 * <br>
	 * (x - rl.x, y - rl.y, z - rl.z) to (x + rh.x, y + rh.y, z + rh.z),<br>
	 * <br>
	 * inclusive.<br>
	 * <br>
	 * If the array is only a single element long, then rl and rh are assumed to be equal.
	 * <br> 
	 * If the cuboid is not contained within a single region, then the update method will be executed by the main thread.<br>
	 * <br>
	 * 
	 * @return the r vector
	 */
	public Vector3[] maxRange();
	
	/**
	 * Performs a dynamic update of the block.  This occurs during the start of the FINALIZE part of the tick, for Region specific updates, or during TICKSTART, for updates which span multiple regions.<br>
	 * <br>
	 * World updates must NOT make updates outside of the cuboid defined by the maxRange method.<br>
	 * <br>
	 * This method is always called on the first tick after a block is flagged as dynamic, but with the last parameter set to true.<br>
	 * <br>
	 * Note: No updates should be performed during this first update.
	 * 
	 * @param b the block
	 * @param updateTime the time the update was intended to happen
	 * @param lastUpdateTime the last time the block was updated
	 * @param first true the first time the block is flagged as dynamic
	 * @return the next time the dynamic block should be updated, or less than zero for none
	 */
	public long update(Block b, long updateTime, long lastUpdateTime, boolean first);

}
