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

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class PackedCoordsTest {
	
	private final int LENGTH = 1000;
	
	@Test
	public void test() {
		
		Random r = new Random();
		
		for (int i = 0; i < LENGTH; i++) {
			int rx = r.nextInt() & 0xFFFFFF00;
			int ry = r.nextInt() & 0xFFFFFF00;
			int rz = r.nextInt() & 0xFFFFFF00;
			int x1 = r.nextInt(768) - 256;
			int y1 = r.nextInt(768) - 256;
			int z1 = r.nextInt(768) - 256;
			int x2 = r.nextInt(768) - 256;
			int y2 = r.nextInt(768) - 256;
			int z2 = r.nextInt(768) - 256;
			int packed1 = PackedCoords.getPackedCoords(x1, y1, z1);
			int packed2 = PackedCoords.getPackedCoords(x2, y2, z2);
			testPacked("X1", rx, x1, packed1, PackedCoords.getX(rx, packed1));
			testPacked("Y1", ry, y1, packed1, PackedCoords.getY(ry, packed1));
			testPacked("Z1", rz, z1, packed1, PackedCoords.getZ(rz, packed1));
			testPacked("X2", rx, x2, packed2, PackedCoords.getX(rx, packed2));
			testPacked("Y2", ry, y2, packed2, PackedCoords.getY(ry, packed2));
			testPacked("Z2", rz, z2, packed2, PackedCoords.getZ(rz, packed2));
			
			int ox = (x1 - x2);
			int oy = (y1 - y2);
			int oz = (z1 - z2);
			
			int translated = PackedCoords.translate(packed2, ox, oy, oz);
				
			assertTrue("Packed coords did not match after translation", translated == packed1);
		}
		
		
	}
	
	private void testPacked(String name, int r, int o, int packed, int packedGet) {
		boolean match = packedGet == r + o;
		String failure = name + " decoded correctly, " + Integer.toHexString(r) + " + " + Integer.toHexString(o) + 
				" != " + Integer.toHexString(packedGet) + ", packed = " + Integer.toHexString(packed);
		assertTrue(failure, match);
	}

}
