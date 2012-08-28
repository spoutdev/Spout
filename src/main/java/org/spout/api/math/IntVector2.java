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
package org.spout.api.math;

import org.spout.api.util.StringUtil;

/**
 * A 2-dimensional vector represented by int-precision x,y coordinates
 */
public class IntVector2 {
	
	private int x;
	private int y;
	
	public IntVector2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the X coordinate
	 * 
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Sets the Y coordinate
	 * 
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Gets the X coordinate
	 *
	 * @return The X coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Gets the Y coordinate
	 *
	 * @return The Y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Gets whether all of the axis in this IntVector are zero
	 * 
	 * @return True if all axis are zero
	 */
	public boolean isZero() {
		return this.x == 0 && this.y == 0;
	}
	
	/**
	 * Adds the given vector to this vector
	 * 
	 * @param other
	 */
	public void add(IntVector2 other) {
		x += other.x;
		y += other.y;
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(getX(), getY());
	}
}
