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

public class SignedTenBitTripleHashed {
	private final static int mask = 0xFFDFFBFF;
	private final static int[] shiftMask = new int[16];

	static {
		for (int i = 0; i < 16; i++) {
			int single = 0x3FF >> i;
			shiftMask[i] = (single << 22) | (single << 11) | single;
		}
	}

	/**
	 * Packs the first 8 most significant bits of each byte into an <code>int</code>
	 *
	 * @param x an <code>byte</code> value
	 * @param y an <code>byte</code> value
	 * @param z an <code>byte</code> value
	 * @return The first 8 most significant bits of each byte packed into an <code>int</code>
	 */
	public static int key(int x, int y, int z) {
		return (x & 0x3FF) << 22 | (z & 0x3FF) << 11 | y & 0x3FF;
	}

	/**
	 * Gets the first signed 10-bit integer value from an int key
	 *
	 * @param key to get from
	 * @return the first 8-bit integer value in the key
	 */
	public static int key1(int key) {
		return key >> 22;
	}

	/**
	 * Gets the second signed 10-bit integer value from an int key
	 *
	 * @param key to get from
	 * @return the second 8-bit integer value in the key
	 */
	public static int key2(int key) {
		return (key << 22) >> 22;
	}

	/**
	 * Gets the third signed 10-bit integer value from an int key
	 *
	 * @param key to get from
	 * @return the third 8-bit integer value in the key
	 */
	public static int key3(int key) {
		return (key << 11) >> 22;
	}

	/**
	 * Adds the given offset to the packed key
	 *
	 * @param key the base key
	 * @param x the x offset
	 * @param y the y offset
	 * @param z the z offset
	 * @return the new key
	 */
	public static int add(int key, int x, int y, int z) {
		int offset = key(x, y, z);
		return (key + offset) & mask;
	}

	/**
	 * Shifts the given key to the right.<br> <br> This method only works for keys if all 3 sub-keys are positive
	 *
	 * @param key the key
	 * @param shift the right shift
	 */
	public static int positiveRightShift(int key, int shift) {
		int single = 0x3FF >> shift;
		int shiftMask = (single << 22) | (single << 11) | single;
		return shiftMask & (key >> shift);
	}
}
