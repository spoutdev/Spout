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
package org.spout.api.util.list;

import java.util.Random;

import org.junit.Test;

import org.spout.api.math.IntVector3;

import static org.junit.Assert.assertEquals;

public class IntVector3StackTest {
	@Test
	public void test() {
		for (int i = 0; i < 10; i++) {
			testTriple();
			testSingle();
		}
	}

	private void testTriple() {
		int size = 1024;

		IntVector3Stack stack = new IntVector3Stack(size);

		int[] x = new int[size];
		int[] y = new int[size];
		int[] z = new int[size];

		Random r = new Random();

		for (int i = 0; i < size; i++) {
			x[i] = r.nextInt();
			y[i] = r.nextInt();
			z[i] = r.nextInt();
		}

		for (int i = 0; i < size; i++) {
			stack.push(x[i], y[i], z[i]);
		}

		for (int i = size - 1; i >= 0; i--) {
			IntVector3 v = stack.pop();
			assertEquals("X coord mismatch", x[i], v.getX());
			assertEquals("Y coord mismatch", y[i], v.getY());
			assertEquals("Z coord mismatch", z[i], v.getZ());
		}
	}

	private void testSingle() {
		int size = 1024;

		IntVector3Stack stack = new IntVector3Stack(size);

		int[] x = new int[size];
		int[] y = new int[size];
		int[] z = new int[size];

		Random r = new Random();

		for (int i = 0; i < size; i++) {
			x[i] = r.nextInt();
			y[i] = r.nextInt();
			z[i] = r.nextInt();
		}

		for (int i = 0; i < size; i++) {
			stack.push(x[i], y[i], z[i]);
		}

		for (int i = size - 1; i >= 0; i--) {
			assertEquals("X coord mismatch", x[i], stack.popSingle());
			assertEquals("Y coord mismatch", y[i], stack.popSingle());
			assertEquals("Z coord mismatch", z[i], stack.popSingle());
		}
	}
}
