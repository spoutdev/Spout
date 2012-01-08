/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.util.map;

/**
 * A simplistic map that supports (byte, short, byte) keys, using a trove int *
 * hashmap in the backend.
 *
 */

public class TByteShortByteKeyedMap {

	public static final int key(int x, int y, int z) {
		return (x & 0xFF) << 24 | (z & 0xFF) << 16 | y & 0xFFFF;
	}

	public static byte getXFromKey(int key) {
		return (byte) (key >> 24);
	}

	public static short getYFromKey(int key) {
		return (short) key;
	}

	public static byte getZFromKey(int key) {
		return (byte) (key >> 16);
	}

}
