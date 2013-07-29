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

public class ByteShortByteHashed {
	/**
	 * Creates a long key from 2 bytes and a short
	 *
	 * @param key1 a <code>byte</code> value
	 * @param key2 a <code>short</code> value
	 * @param key3 a <code>byte</code> value
	 * @return a long which is the concatenation of key1, key2 and key3
	 */
	public static int key(int key1, int key2, int key3) {
		return (key1 & 0xFF) << 24 | (key3 & 0xFF) << 16 | key2 & 0xFFFF;
	}

	/**
	 * Gets the first 8-bit integer value from a long key
	 *
	 * @param key to get from
	 * @return the first 8-bit integer value in the key
	 */
	public static byte key1(int key) {
		return (byte) (key >> 24);
	}

	/**
	 * Gets the second 16-bit integer value from a long key
	 *
	 * @param key to get from
	 * @return the second 16-bit integer value in the key
	 */
	public static short key2(int key) {
		return (short) key;
	}

	/**
	 * Gets the third 8-bit integer value from a long key
	 *
	 * @param key to get from
	 * @return the third 8-bit integer value in the key
	 */
	public static byte key3(int key) {
		return (byte) (key >> 16);
	}
}
