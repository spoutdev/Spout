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
package org.spout.api.util;

/**
 * Represents a set of utility functions for squashing and separating primitive datatypes.
 */
public abstract class HashUtil {

	/**
	 * Returns the most significant bits in the long value.
	 * 
	 * @param composite to separate
	 * @return the most significant bits in an integer
	 */
	public static int longToInt1(long composite) {
		return (int) (composite >> 32 & 0xFFFFFFFFL);
	}

	/**
	 * Returns the least significant bits in the long value.
	 * 
	 * @param composite to separate
	 * @return the least significant bits in an integer
	 */
	public static int longToInt2(long composite) {
		return (int) (composite & 0xFFFFFFFFL);
	}

	/**
	 * Squashes 2 short values into 1 int, with the first value in
	 * the most significant bits and the second value in the least
	 * significant bits.
	 * 
	 * @param key1 to sqaush
	 * @param key2 to sqaush
	 * @return squashed int
	 */
	public static int shortToInt(short key1, short key2) {
		return (int) key1 << 16 | key2 & 0xFFFF;
	}

	/**
	 * Squashes 2 integer values into 1 long, with the first value in
	 * the most significant bits and the second value in the least
	 * significant bits.
	 * 
	 * @param key1 to sqaush
	 * @param key2 to sqaush
	 * @return squashed long
	 */
	public static long intToLong(int key1, int key2) {
		return (long) key1 << 32 | key2 & 0xFFFFFFFFL;
	}

	/**
	 * Returns the 4 most significant bits in the byte value.
	 * 
	 * @param composite to separate
	 * @return the 4 most significant bits in a byte
	 */
	public static byte byteToNibble1(int composite) {
		return (byte) ((composite >> 4) & 0xF);
	}

	/**
	 * Returns the 4 least significant bits in the byte  value.
	 * 
	 * @param composite to separate
	 * @return the 4 least significant bits in a byte
	 */
	public static byte byteToNibble2(int composite) {
		return (byte) (composite & 0xF);
	}

	/**
	 * Squashes 2 nible values into 1 byte, with the first value in
	 * the most significant bits and the second value in the least
	 * significant bits.
	 * 
	 * @param key1 to sqaush
	 * @param key2 to sqaush
	 * @return squashed byte
	 */
	public static byte nibbleToByte(int key1, int key2) {
		return (byte) (key1 << 4 | (key2 & 0xF));
	}

}
