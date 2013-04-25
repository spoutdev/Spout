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

public class IntVector3CompositeIteratorTest {
	
	private final int LENGTH = 10;
	private final int SIZE = 32;
	private final int MASK = SIZE - 1;
	
	@Test
	public void test() {
		
		Random r = new Random();
		
		boolean[][][] grid = new boolean[SIZE][SIZE][SIZE];
		
		int[][] x = new int[3][];
		int[][] y = new int[3][];
		int[][] z = new int[3][];
		
		IntVector3Array[] arr = new IntVector3Array[3];
		
		int length = LENGTH;
		
		int lengthSum = 0;
		
		for (int i = 0; i < 3; i++) {
			lengthSum += length;
			
			x[i] = randomArray(length, MASK, r);
			y[i] = randomArray(length, MASK, r);
			z[i] = randomArray(length, MASK, r);
			arr[i] = new IntVector3Array(x[i], y[i], z[i], length);

			length = length >> 1;
		}

		Iterator<IntVector3> itr = new IntVector3CompositeIterator(arr);
		
		int l = 0;
		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			grid[v.getX()][v.getY()][v.getZ()] = true;
			l++;
		}
		
		assertTrue("Iterator has incorrect length, " + l + "!=" + lengthSum, l == lengthSum);
		
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < x[j].length; i++) {
				assertTrue("Location missed " + x[j][i] + ", " + y[j][i] + ", " + z[j][i], grid[x[j][i]][y[j][i]][z[j][i]]);
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
