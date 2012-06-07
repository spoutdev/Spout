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

public class ShortPairHashed {
	/**
	 * Squashes 2 short values into 1 int, with the first value in
	 * the most significant bits and the second value in the least
	 * significant bits.
	 * 
	 * @param key1 to squash
	 * @param key2 to squash
	 * @return squashed int
	 */
	public static int key(short key1, short key2) {
		return key1 << 16 | key2 & 0xFFFF;
	}

	/**
	 * Returns the 16 most significant bits (short) in the int value.
	 * 
	 * @param composite to separate
	 * @return the 16 most significant bits in an int
	 */
	public static short key1(int composite) {
		return (short) ((composite >> 16) & 0xFFFF);
	}

	/**
	 * Returns the 16 least significant bits (short) in the int value.
	 * 
	 * @param composite to separate
	 * @return the 16 least significant bits in an int
	 */
	public static short key2(int composite) {
		return (short) (composite & 0xFFFF);
	}
}
