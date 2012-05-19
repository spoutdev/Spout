/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

public class TInt21TripleHashed {
	/**
	 * Packs the most significant and the twenty least significant of each int into a <code>long</code>
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return the most significant and the twenty least significant of each int packed into a <code>long</code>
	 */
	public static final long key(int x, int y, int z) {
		return ((long) ((x >> 11) & 0x100000 | x & 0xFFFFF)) << 42 | ((long) ((y >> 11) & 0x100000 | y & 0xFFFFF)) << 21 | ((z >> 11) & 0x100000 | z & 0xFFFFF);
	}

	/**
	 * Gets the first 21-bit integer value from a long key
	 * 
	 * @param key to get from
	 * @return the first 21-bit integer value in the key
	 */
	public static final int key1(long key) {
		return keyInt((key >> 42) & 0x1FFFFF);
	}

	/**
	 * Gets the second 21-bit integer value from a long key
	 * 
	 * @param key to get from
	 * @return the second 21-bit integer value in the key
	 */
	public static final int key2(long key) {
		return keyInt((key >> 21) & 0x1FFFFF);
	}

	/**
	 * Gets the third 21-bit integer value from a long key
	 * 
	 * @param key to get from
	 * @return the third 21-bit integer value in the key
	 */
	public static final int key3(long key) {
		return keyInt(key & 0x1FFFFF);
	}

	private static final int keyInt(long key) {
		return (int) (key - ((key & 0x100000) << 1));
	}
}
