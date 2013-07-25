/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.util.hashing;

public class Int10TripleHashed {
	private int bx;
	private int by;
	private int bz;

	public Int10TripleHashed() {
	}

	public Int10TripleHashed(int bx, int by, int bz) {
		this.bx = bx;
		this.by = by;
		this.bz = bz;
	}

	/**
	 * Sets the base of the hash to the given values
	 */
	public final void setBase(int bx, int by, int bz) {
		this.bx = bx;
		this.by = by;
		this.bz = bz;
	}

	/**
	 * Packs given x, y, z coordinates.  The coords must represent a point within a 1024 sized cuboid with the base at the (bx, by, bz)
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return the packed int
	 */
	public final int key(int x, int y, int z) {
		return (((x - bx) & 0x3FF) << 22) | (((y - by) & 0x3FF) << 11) | ((z - bz) & 0x3FF);
	}

	/**
	 * Gets the x coordinate value from the int key
	 *
	 * @param key to get from
	 * @return the x coord
	 */
	public final int keyX(int key) {
		return bx + ((key >> 22) & 0x3FF);
	}

	/**
	 * Gets the y coordinate value from the int key
	 *
	 * @param key to get from
	 * @return the y coord
	 */
	public final int keyY(int key) {
		return by + ((key >> 11) & 0x3FF);
	}

	/**
	 * Gets the y coordinate value from the int key
	 *
	 * @param key to get from
	 * @return the y coord
	 */
	public final int keyZ(int key) {
		return bz + (key & 0x3FF);
	}
}
