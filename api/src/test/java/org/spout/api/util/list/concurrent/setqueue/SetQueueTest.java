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
package org.spout.api.util.list.concurrent.setqueue;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SetQueueTest {
	private final int SET_SIZE = 50;
	private final int OPERATIONS = 300;

	public void testFull() {
		SetQueue<Integer> queue = new SetQueue<>(10);

		IntegerSetQueueElement[] elements = new IntegerSetQueueElement[10];
		for (int i = 0; i < 20; i++) {
			elements[i] = new IntegerSetQueueElement(queue, i);
		}

		for (int i = 0; i < 10; i++) {
			elements[i].add();
		}

		for (int i = 10; i < 20; i++) {
			verifyFullAdding(elements, i);
		}
	}

	@Test
	public void testInvalid() {

		SetQueue<Integer> queue = new SetQueue<>(5);

		IntegerSetQueueElement[] elements = new IntegerSetQueueElement[10];
		for (int i = 0; i < 10; i++) {
			elements[i] = new IntegerSetQueueElement(queue, i);
		}

		for (int i = 0; i < 5; i++) {
			elements[i].add();
		}

		verifyFullAdding(elements, 5);

		elements[3].setInvalid();

		elements[5].add();

		for (int i = 0; i < 5; i++) {
			elements[i].setInvalid();
		}

		for (int j = 0; j < 10; j++) {
			for (int i = 5; i < 10; i++) {
				elements[i].add();
			}
		}

		elements[8].setInvalid();

		HashSet<Integer> set = new HashSet<>();

		Integer i;
		while ((i = queue.poll()) != null) {
			assertTrue("Element in queue out of range, " + i, i >= 5 && i < 10 && i != 8);
			set.add(i);
		}

		assertTrue("Elements missing from queue", set.size() == 4);
	}

	@Test
	public void testQueueAsSet() {

		SetQueue<Integer> queue = new SetQueue<>(SET_SIZE);

		IntegerSetQueueElement[] elements = new IntegerSetQueueElement[SET_SIZE];
		for (int i = 0; i < SET_SIZE; i++) {
			elements[i] = new IntegerSetQueueElement(queue, i);
		}

		HashSet<Integer> queued = new HashSet<>();

		Random r = new Random();

		for (int c = 0; c < OPERATIONS; c++) {
			if (r.nextInt(5) == 0) {
				Integer i = queue.poll();
				if (i == null) {
					assertTrue("Unable to read element from non-empty queue", queued.isEmpty());
				} else {
					assertTrue("Unknown element removed from queue " + i, queued.remove(i));
				}
			} else {
				int i = (r.nextInt() & 0x7FFFFFFF) % SET_SIZE;
				queued.add(i);
				elements[i].add();
			}
		}

		Integer i;
		while ((i = queue.poll()) != null) {
			assertTrue("Unknown element removed from queue " + i, queued.remove(i));
		}

		assertTrue("All elements not removed from queue, " + queued.size() + " elements remaining", queued.isEmpty());
	}

	private static void verifyFullAdding(SetQueueElement<Integer>[] elements, int i) {
		boolean thrown = false;
		try {
			elements[i].add();
		} catch (SetQueueFullException f) {
			thrown = true;
		}
		assertTrue("DirtyQueueFullException was not thrown", thrown);
	}

	private static class IntegerSetQueueElement extends SetQueueElement<Integer> {
		private AtomicBoolean valid = new AtomicBoolean(true);

		public IntegerSetQueueElement(SetQueue<Integer> queue, Integer value) {
			super(queue, value);
		}

		public void setInvalid() {
			this.valid.set(false);
		}

		@Override
		protected boolean isValid() {
			return valid.get();
		}
	}
}
