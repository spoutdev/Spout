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
package org.spout.api.math;

import org.spout.api.util.StringUtil;

/**
 * A 3-dimensional vector represented by int-precision x,y coordinates
 */
public class IntVector4 extends IntVector3 {

	private int w;
	
	public IntVector4(int w, int x, int y, int z) {
		super(x, y, z);
		this.w = w;
	}

	/**
	 * Sets the W coordinate
	 * @param z
	 */
	public void setW(int w) {
		this.w = w;
	}

	/**
	 * Gets the W coordinate
	 * @return The W coordinate
	 */
	public int getW() {
		return w;
	}

	/**
	 * Sets this vector equal to the given vector
	 * @param other
	 */
	public void set(IntVector4 v) {
		setW(v.getW());
		setX(v.getX());
		setY(v.getY());
		setZ(v.getZ());
	}

	/**
	 * Sets this vector equal to the given coordinates
	 */
	public void set(int w, int x, int y, int z) {
		setW(w);
		setX(x);
		setY(y);
		setZ(z);
	}

	@Override
	public boolean isZero() {
		return super.isZero() && this.w == 0;
	}

	/**
	 * Adds the given vector to this vector
	 * @param other
	 */
	public void add(IntVector4 other) {
		super.add(other);
		w += other.w;
	}

	@Override
	public String toString() {
		return StringUtil.toString(getW(), getX(), getY(), getZ());
	}

	public IntVector4 copy() {
		return new IntVector4(getW(), getX(), getY(), getZ());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof IntVector4)) {
			return false;
		} else {
			IntVector4 other = (IntVector4) o;
			return other.getW() == getW() && other.getX() == getX() && other.getY() == getY() && other.getZ() == getZ();
		}
	}
}
