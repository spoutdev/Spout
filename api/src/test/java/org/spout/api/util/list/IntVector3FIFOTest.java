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

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import org.spout.api.math.IntVector3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntVector3FIFOTest {
	@Test
	public void test() {
		for (int i = 0; i < 10; i++) {
			testTriple();
		}
	}

	private void testTriple() {
		int size = 1024;

		IntVector3FIFO fifo = new IntVector3FIFO(size);

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
			fifo.write(x[i], y[i], z[i]);
		}

		for (int i = 0; i < size; i++) {
			IntVector3 v = fifo.read();
			assertEquals("X coord mismatch", x[i], v.getX());
			assertEquals("Y coord mismatch", y[i], v.getY());
			assertEquals("Z coord mismatch", z[i], v.getZ());
		}

		for (int i = 0; i < size; i++) {
			x[i] = r.nextInt();
			y[i] = r.nextInt();
			z[i] = r.nextInt();
		}

		for (int i = 0; i < size / 2; i++) {
			fifo.write(x[i], y[i], z[i]);
		}

		for (int i = 0; i < size / 2; i++) {
			IntVector3 v = fifo.read();
			assertEquals("X coord mismatch", x[i], v.getX());
			assertEquals("Y coord mismatch", y[i], v.getY());
			assertEquals("Z coord mismatch", z[i], v.getZ());
		}

		assertEquals("Non null when reading empty FIFO", null, fifo.read());

		for (int i = 0; i < size; i++) {
			fifo.write(x[i], y[i], z[i]);
		}

		boolean thrown = false;
		try {
			fifo.write(0, 0, 0);
		} catch (IllegalStateException e) {
			thrown = true;
		}

		assertTrue("Exception not thrown when FIFO full", thrown);
	}

	@Test
	public void testBurst() {

		Queue<IntVector3> input = new ConcurrentLinkedQueue<IntVector3>();
		Queue<IntVector3> output = new ConcurrentLinkedQueue<IntVector3>();

		IntVector3FIFO fifo = new IntVector3FIFO(128);

		Random r = new Random();

		for (int i = 0; i < 20; i++) {

			int burst = r.nextInt(70);

			for (int j = 0; j < burst; j++) {
				addRandom(input, fifo, r);
				addRandom(input, fifo, r);
				readRandom(output, fifo);
			}

			burst = r.nextInt(35);

			for (int j = 0; j < burst; j++) {
				addRandom(input, fifo, r);
				readRandom(output, fifo);
				readRandom(output, fifo);
			}
		}

		while (readRandom(output, fifo)) {
		}

		IntVector3 v;

		while ((v = input.poll()) != null) {
			IntVector3 o = output.poll();
			assertEquals("Input " + v + " does not match output " + o, v, o);
		}
	}

	private void addRandom(Queue<IntVector3> input, IntVector3FIFO fifo, Random r) {
		if (fifo.isFull()) {
			return;
		}
		IntVector3 v = new IntVector3(r.nextInt(), r.nextInt(), r.nextInt());

		input.add(v);
		fifo.write(v.getX(), v.getY(), v.getZ());
	}

	private boolean readRandom(Queue<IntVector3> output, IntVector3FIFO fifo) {
		IntVector3 v = fifo.read();

		if (v == null) {
			return false;
		}

		output.add(v);

		return true;
	}
}
