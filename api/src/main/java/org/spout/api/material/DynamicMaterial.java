/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.material;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.range.EffectRange;

/**
 * Represents a material that can trigger updates in the future, updates will be queued through restarts of the server appropriately.
 *
 */
public interface DynamicMaterial {

	/**
	 * Gets the maximum effect range associated with this dynamic material.<br>
	 * <br>
	 * This method is used to determine if the update is localised to a single region.  Otherwise, the update is less parallel.<br>
	 * <br>
	 * Note: Updates may not modify blocks that are not in the current region or one of its neighbours. 
	 * 
	 * @return the effect range
	 */
	public EffectRange getDynamicRange();

	/**
	 * This method is called during the DYNAMIC_BLOCKS or GLOBAL_DYNAMIC_BLOCKS tick stage. <br>
	 * <br>
	 * World updates must NOT make changes outside the Region that contains the block.<br>
	 * 
	 * @param block the block
	 * @param currentTime the age of the world
	 */
	public void onFirstUpdate(Block block, long currentTime);

	/**
	 * This method is called during the DYNAMIC_BLOCKS or GLOBAL_DYNAMIC_BLOCKS tick stage. <br>
	 * <br>
	 * World updates must NOT make updates outside of the cuboid defined by the maxRange method.<br>
	 * 
	 * @param block the block
	 * @param updateTime the time the update was intended to happen
	 * @param data persistent data for the update
	 */
	public void onDynamicUpdate(Block block, long updateTime, int data);
}
