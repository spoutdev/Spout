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
package org.spout.api.material.range;

import org.spout.api.math.IntVector3;


public interface EffectRange {
	
	public static EffectRange THIS = new CubicEffectRange(0);
	public static EffectRange THIS_AND_BELOW = new ListEffectRange(IntVector3.createList(0, 0, 0, 0, -1, 0));
	public static EffectRange THIS_AND_ABOVE = new ListEffectRange(IntVector3.createList(0, 0, 0, 0, 1, 0));
	public static EffectRange THIS_AND_NEIGHBORS = new DiamondEffectRange(1);
	
	/**
	 * Gets an iterator to iterate over all blocks in the effect range.
	 * 
	 * @return an effect iterator
	 */
	public EffectIterator getEffectIterator();
	
	/**
	 * Configures an iterator to iterate over all blocks in the effect range.
	 * 
	 * @return an effect iterator
	 */
	public void initEffectIterator(EffectIterator reuse);
	
	/**
	 * Checks if the effect is contained within a Region for the given block position
	 * 
	 * @param the x coordinate of the block
	 * @param the y coordinate of the block
	 * @param the z coordinate of the block
	 * @return true if the range is Region specific
	 */
	public boolean isRegionLocal(int x, int y, int z);

	
}
