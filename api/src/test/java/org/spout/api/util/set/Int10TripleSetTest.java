/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.util.set;

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Int10TripleSetTest {
	private static final int COUNT = 1000;
	private static final int COUNT2 = 10;

	@Test
	public void test() {

		Random r = new Random();

		for (int i = 0; i < COUNT; i++) {
			int bx = r.nextInt();
			int by = r.nextInt();
			int bz = r.nextInt();
			TInt10TripleSet set = new TInt10TripleSet(bx, by, bz);
			final int[] xx = new int[COUNT2];
			final int[] yy = new int[COUNT2];
			final int[] zz = new int[COUNT2];
			for (int j = 0; j < COUNT2; j++) {
				int x = bx + r.nextInt(1024);
				int y = by + r.nextInt(1024);
				int z = bz + r.nextInt(1024);
				xx[j] = x;
				yy[j] = y;
				zz[j] = z;
				if (!set.add(x, y, z)) {
					j--;
					continue;
				}
			}

			set.forEach(new TInt10Procedure() {
				@Override
				public boolean execute(int x, int y, int z) {
					assertTrue("Unable to remove " + x + ", " + y + ", " + z, remove(xx, yy, zz, x, y, z));
					return true;
				}
			});

			for (int j = 0; j < COUNT2; j++) {
				assertTrue("X array not zeroed", xx[j] == 0);
				assertTrue("Y array not zeroed", yy[j] == 0);
				assertTrue("Z array not zeroed", zz[j] == 0);
			}
		}
	}

	public boolean remove(int[] xx, int[] yy, int[] zz, int x, int y, int z) {
		int j;
		for (j = 0; j < xx.length; j++) {
			if (xx[j] == x && yy[j] == y && zz[j] == z) {
				break;
			}
		}
		if (j == xx.length) {
			return false;
		}
		int i;
		for (i = j; i < xx.length - 1; i++) {
			xx[i] = xx[i + 1];
			yy[i] = yy[i + 1];
			zz[i] = zz[i + 1];
		}
		xx[i] = 0;
		yy[i] = 0;
		zz[i] = 0;
		return true;
	}
}
