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

public class IntPairHashed {
	/**
	 * Creates a long key from 2 ints
	 *
	 * @param key1 an <code>int</code> value
	 * @param key2 an <code>int</code> value
	 * @return a long which is the concatenation of key1 and key2
	 */
	public static final long key(int key1, int key2) {
		return (long) key1 << 32 | key2 & 0xFFFFFFFFL;
	}

	/**
	 * Gets the first 32-bit integer value from an long key
	 * 
	 * @param key to get from
	 * @return the first 32-bit integer value in the key
	 */
	public static int key1(long key) {
		return (int) (key >> 32 & 0xFFFFFFFFL);
	}

	/**
	 * Gets the second 32-bit integer value from an long key
	 * 
	 * @param key to get from
	 * @return the second 32-bit integer value in the key
	 */
	public static int key2(long key) {
		return (int) (key & 0xFFFFFFFFL);
	}
}
