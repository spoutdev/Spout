/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spout.api.math.IntVector3;

public class FlatIteratorTest {
	
	private final int SIZE = 40;
	
	private final int DIST = SIZE / 3;
	
	private final int[][][] hits = new int[SIZE][SIZE][SIZE];
	
	IntVector3 center = new IntVector3(SIZE / 2, SIZE / 4, SIZE / 2);
	int height = SIZE / 3;
	
	@Test
	public void test() {
		
		FlatIterator itr = new FlatIterator(center.getX(), center.getY(), center.getZ(), height, DIST);
		
		int prev = -1;

		while (itr.hasNext()) {
			IntVector3 next = itr.next();
			int dist = getDistance(center, next);
			assertTrue("Distance readback is incorrect, exp " + dist + ", got " + itr.getDistance(), dist == itr.getDistance());
			assertTrue("Iterator moved inwards", dist >= prev);
			add(next);
			prev = dist;
		}
		
		check();
		
	}
	
	private int getDistance(IntVector3 a, IntVector3 b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getZ() - b.getZ());
		
	}
	
	private void add(IntVector3 v) {
		if (v.getX() < 0 || v.getX() >= SIZE || v.getY() < 0 || v.getY() >= SIZE || v.getZ() < 0 || v.getZ() >= SIZE) {
			return;
		}

		assertFalse("Coordinate hit more than once " + v, (hits[v.getX()][v.getY()][v.getZ()]++) > 0);
	}
	
	private boolean inRange(int distance, int y) {
		if (y >= center.getY() + height || y < center.getY()) {
			return false;
		}
		return distance <= DIST;
	}
	
	private boolean check() {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					int distance = getDistance(center, new IntVector3(x, y, z));
					boolean inRange = inRange(distance, y);
					assertTrue("Location missed " + x + " " + y + " " + z, !inRange || hits[x][y][z] == 1);
					assertTrue("Location out of range hit " + x + " " + y + " " + z, inRange || hits[x][y][z] == 0);
				}
			}
		}
		return true;
	}

}
