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
package org.spout.api.util.hashing;

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Int10TripleHashedTest {
	private static final int COUNT = 1000;
	private static final int COUNT2 = 10;

	@Test
	public void test() {

		Random r = new Random();

		for (int i = 0; i < COUNT; i++) {
			int bx = r.nextInt();
			int by = r.nextInt();
			int bz = r.nextInt();
			Int10TripleHashed hashed = new Int10TripleHashed(bx, by, bz);
			for (int j = 0; j < COUNT2; j++) {
				int x = bx + r.nextInt(1024);
				int y = by + r.nextInt(1024);
				int z = bz + r.nextInt(1024);
				int key = hashed.key(x, y, z);
				assertTrue("X coord mismatch", hashed.keyX(key) == x);
				assertTrue("Y coord mismatch", hashed.keyY(key) == y);
				assertTrue("Z coord mismatch", hashed.keyZ(key) == z);
			}
		}
	}
}
