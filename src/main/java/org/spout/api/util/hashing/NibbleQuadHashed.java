package org.spout.api.util.hashing;
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

public class NibbleQuadHashed {
	/**
	 * Packs the first 4 least significant bits of each byte into a <code>short</code>
	 *
	 * @param key1 a <code>byte</code> value
	 * @param key2 a <code>byte</code> value
	 * @param key3 a <code>byte</code> value
	 * @param key4 a <code>byte</code> value
	 * @return The first 4 most significant bits of each byte packed into a <code>short</code>
	 */
	public static final short key(int key1, int key2, int key3, int key4) {
		return (short) ((key1 & 0xF) << 12 | (key2 & 0xF) << 8 | (key3 & 0xF) << 4 | key4 & 0xF);
	}

	/**
	 * Gets the first 4-bit integer value from a short key
	 * 
	 * @param key to get from
	 * @return the first 4-bit integer value in the key
	 */
	public static byte key1(int key) {
		return (byte) ((key >> 12) & 0xF);
	}

	/**
	 * Gets the second 4-bit integer value from a short key
	 * 
	 * @param key to get from
	 * @return the second 4-bit integer value in the key
	 */
	public static byte key2(int key) {
		return (byte) ((key >> 8) & 0xF);
	}

	/**
	 * Gets the third 4-bit integer value from a short key
	 * 
	 * @param key to get from
	 * @return the third 4-bit integer value in the key
	 */
	public static byte key3(int key) {
		return (byte) ((key >> 4) & 0xF);
	}

	/**
	 * Gets the fourth 4-bit integer value from a short key
	 * 
	 * @param key to get from
	 * @return the fourth 4-bit integer value in the key
	 */
	public static byte key4(int key) {
		return (byte) (key & 0xF);
	}
}
