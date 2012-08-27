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

public class NibblePairHashed {
	/**
	 * Packs the first 4 most significant bits of each byte into a <code>byte</code>
	 *
	 * @param key1 a <code>byte</code> value
	 * @param key2 a <code>byte</code> value
	 * @return The first 4 most significant bits of each byte packed into a <code>byte</code>
	 */
	public static byte key(int key1, int key2) {
		return (byte) (((key1 & 0xF) << 4) | (key2 & 0xF));
	}

	/**
	 * Sets 4 most significant bits in the composite to the 4 least significant bits in the key
	 * @param composite
	 * @param key2
	 * @return
	 */
	public static byte setKey1(int composite, int key1) {
		return (byte) (((key1 & 0xF) << 4) | (composite & 0xF));
	}

	/**
	 * Sets 4 least significant bits in the composite to the 4 least significant bits in the key
	 * @param composite
	 * @param key2
	 * @return
	 */
	public static byte setKey2(int composite, int key2) {
		return (byte) ((composite & 0xF0) | (key2 & 0xF));
	}

	/**
	 * Returns the 4 most significant bits in the byte value.
	 * 
	 * @param composite to separate
	 * @return the 4 most significant bits in a byte
	 */
	public static byte key1(int composite) {
		return (byte) ((composite >> 4) & 0xF);
	}

	/**
	 * Returns the 4 least significant bits in the byte value.
	 * 
	 * @param composite to separate
	 * @return the 4 least significant bits in a byte
	 */
	public static byte key2(int composite) {
		return (byte) (composite & 0xF);
	}
}
