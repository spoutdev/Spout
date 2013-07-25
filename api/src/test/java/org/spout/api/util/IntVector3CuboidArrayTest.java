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

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Random;

import org.junit.Test;
import org.spout.api.math.IntVector3;

public class IntVector3CuboidArrayTest {
	
	private final int LENGTH = 5;
	private final int SIZE = 32;
	private final int MASK = SIZE - 1;
	
	@Test
	public void test() {
		
		Random r = new Random();
		
		boolean[][][] grid = new boolean[SIZE + 1][SIZE + 1][SIZE + 1];
		
		int[] bx = randomArray(LENGTH, MASK, r);
		int[] by = randomArray(LENGTH, MASK, r);
		int[] bz = randomArray(LENGTH, MASK, r);
		
		int[] tx = randomArray(LENGTH, MASK, r);
		int[] ty = randomArray(LENGTH, MASK, r);
		int[] tz = randomArray(LENGTH, MASK, r);
		
		int size = 0;
		
		for (int i = 0; i < LENGTH; i++) {
			if (tx[i] <= bx[i]) {
				tx[i] = bx[i] + 1;
			}
			if (ty[i] <= by[i]) {
				ty[i] = by[i] + 1;
			}
			if (tz[i] <= bz[i]) {
				tz[i] = bz[i] + 1;
			}
			size += (tx[i] - bx[i]) * (ty[i] - by[i]) * (tz[i] - bz[i]);
		}
		
		IntVector3CuboidArray arr = new IntVector3CuboidArray(bx, by, bz, tx, ty, tz, LENGTH);
		
		Iterator<IntVector3> itr = arr.iterator();
		
		int l = 0;
		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			grid[v.getX()][v.getY()][v.getZ()] = true;
			l++;
		}
		
		assertTrue("Iterator has incorrect length, " + l + "!=" + size, l == size);
		
		for (int i = 0; i < LENGTH; i++) {
			for (int xx = bx[i]; xx < tx[i]; xx++) {
				for (int yy = by[i]; yy < ty[i]; yy++) {
					for (int zz = bz[i]; zz < tz[i]; zz++) {
						assertTrue("Location missed " + xx + ", " + yy + ", " + zz, grid[xx][yy][zz]);
					}
				}
			}
		}
		
	}
	
	
	private static int[] randomArray(int length, int mask, Random r) {
		int [] a = new int[length];
		for (int i = 0; i < length; i++) {
			a[i] = r.nextInt() & mask;
		}
		return a;
	}

}
