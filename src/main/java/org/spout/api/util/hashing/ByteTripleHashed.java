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
package org.spout.api.util.hashing;

public class ByteTripleHashed {
	/**
	 * Packs the first 8 most significant bits of each byte into an <code>int</code>
	 *
	 * @param x an <code>byte</code> value
	 * @param y an <code>byte</code> value
	 * @param z an <code>byte</code> value
	 * @return The first 8 most significant bits of each byte packed into an <code>int</code>
	 */
	public static final int key(int x, int y, int z) {
		return (x & 0xFF) << 16 | (z & 0xFF) << 8 | y & 0xFF;
	}

	/**
	 * Gets the first 8-bit integer value from an int key
	 * 
	 * @param key to get from
	 * @return the first 8-bit integer value in the key
	 */
	public static final byte key1(int key) {
		return (byte) (key >> 16 & 0xFF);
	}

	/**
	 * Gets the second 8-bit integer value from an int key
	 * 
	 * @param key to get from
	 * @return the second 8-bit integer value in the key
	 */
	public static final byte key2(int key) {
		return (byte) (key & 0xFF);
	}

	/**
	 * Gets the third 8-bit integer value from an int key
	 * 
	 * @param key to get from
	 * @return the third 8-bit integer value in the key
	 */
	public static final byte key3(int key) {
		return (byte) (key >> 8 & 0xFF);
	}
}
