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
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.range.EffectRange;

public interface DynamicMaterial {
	
	/**
	 * Gets the maximum effect range associated with this dynamic material
	 * 
	 * @return the effect range
	 */
	public EffectRange getDynamicRange();
	
	/**
	 * This method is called during the DYNAMIC_BLOCKS or GLOBAL_DYNAMIC_BLOCKS tick stage. <br>
	 * <br>
	 * World updates must NOT make changes outside the Region that contains the block.<br>
	 * 
	 * @param b the block
	 * @param r the region that contains the block
	 * @param currentTime the age of the world
	 */
	public void onPlacement(Block b, Region r, long currentTime);
	
	/**
	 * This method is called during the DYNAMIC_BLOCKS or GLOBAL_DYNAMIC_BLOCKS tick stage. <br>
	 * <br>
	 * World updates must NOT make updates outside of the cuboid defined by the maxRange method.<br>
	 * 
	 * @param b the block
	 * @param r the region that contains the block
	 * @param updateTime the time the update was intended to happen
	 * @param queuedTime the time this update was queued
	 * @param data persistent data for the update
	 * @param hint non-persistent hint that may help with the update
	 */
	public void onDynamicUpdate(Block b, Region r, long updateTime, long queuedTime, int data, Object hint);

}
