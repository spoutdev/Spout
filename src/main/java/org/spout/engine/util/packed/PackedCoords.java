/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.util.packed;

import org.spout.api.util.hashing.SignedTenBitTripleHashed;

public class PackedCoords extends SignedTenBitTripleHashed {
	
	@SuppressWarnings("unused")
	private final static int mask = 0xFFDFFBFF;

	/**
	 * Gets the int packed location for the given coordinates.  The coordinates must 
	 * be in the region or one of its 26 neighbours for correction operation.
	 * 
	 * @param x the x offset
	 * @param y the y offset
	 * @param z the z offset
	 * @return
	 */
	public static int getPackedCoords(int x, int y, int z) {
		return SignedTenBitTripleHashed.key(x, y, z);
	}
	
	public static int getX(int bx, int packed) {
		return SignedTenBitTripleHashed.key1(packed) + bx;
	}
	
	public static int getY(int by, int packed) {
		return SignedTenBitTripleHashed.key2(packed) + by;
	}
	
	public static int getZ(int bz, int packed) {
		return SignedTenBitTripleHashed.key3(packed) + bz;
	}
	
	/**
	 * Adds the given x, y, z coordinate to this packed coordinate.<br>
	 * <br>
	 * Warning: this method will overflow if the resulting coordinate is outside 
	 * the allowed range
	 * 
	 * @param packed
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static int translate(int packed, int x, int y, int z) {
		return SignedTenBitTripleHashed.add(packed, x, y, z);
	}
	
}
