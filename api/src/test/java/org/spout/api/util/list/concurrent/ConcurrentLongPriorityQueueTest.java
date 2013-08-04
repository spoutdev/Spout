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
package org.spout.api.util.list.concurrent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConcurrentLongPriorityQueueTest {
	private static int LENGTH = 4096;
	private static int COPIES = 256;
	private static int BINSIZE = 4;
	private static int REPEATS = 100;
	long max = Long.MIN_VALUE;
	LongWithPriority[] output = new LongWithPriority[LENGTH];
	int outputIndex;

	@Test
	public void testFunctionality() {

		LongWithPriority[] sorted = new LongWithPriority[LENGTH];

		int i;
		
		for (i = 0; i < LENGTH; i++) {
			sorted[i] = new LongWithPriority(i / COPIES);
		}

		LongWithPriority[] shuffled = shuffle(sorted);

		ConcurrentLongPriorityQueue<LongWithPriority> lpq = new ConcurrentLongPriorityQueue<>(BINSIZE);

		for (i = 0; i < LENGTH; i++) {
			lpq.add(shuffled[i]);
		}

		max = Long.MIN_VALUE;

		outputIndex = 0;

		readFromQueue(lpq, 16);

		readFromQueue(lpq, Long.MAX_VALUE);

		for (i = 0; i < LENGTH; i++) {
			if (output[i] != null) {
				System.out.print("[" + sorted[i].getPriority() + ":" + output[i].getPriority() + "] ");
			} else {
				System.out.print("[" + sorted[i].getPriority() + ":" + output[i] + "] ");
			}
		}
	}

	@Test
	public void testSpeed() {

		LongWithPriority[] sorted = new LongWithPriority[LENGTH];

		for (int i = 0; i < LENGTH; i++) {
			sorted[i] = new LongWithPriority(i / COPIES);
		}

		LongWithPriority[] shuffled = shuffle(sorted);
		LongWithPriority[] output = new LongWithPriority[shuffled.length];

		ConcurrentLongPriorityQueue<LongWithPriority> lpq = new ConcurrentLongPriorityQueue<>(BINSIZE);

		PriorityBlockingQueue<LongWithPriority> pbq = new PriorityBlockingQueue<>();

		Queue<LongWithPriority> q;

		long iteratorTime = 0;
		long pollTime = 0;
		long completeTime = 0;
		long addTime = 0;
		long startLPQ = System.nanoTime();
		for (int r = 0; r < REPEATS; r++) {
			addTime -= System.nanoTime();
			for (int i = 0; i < shuffled.length; i++) {
				lpq.add(shuffled[i]);
			}
			addTime += System.nanoTime();
			int i = 0;
			pollTime -= System.nanoTime();
			while ((q = lpq.poll(Long.MAX_VALUE)) != null) {
				pollTime += System.nanoTime();
				iteratorTime -= System.nanoTime();
				boolean checkRequired = !lpq.isFullyBelowThreshold(q, Long.MAX_VALUE);
				Iterator<LongWithPriority> itr = q.iterator();
				while (itr.hasNext()) {
					LongWithPriority l = itr.next();
					l.getPriority();
					output[i++] = l;
					itr.remove();
				}
				iteratorTime += System.nanoTime();
				completeTime -= System.nanoTime();
				lpq.complete(q, Long.MAX_VALUE);
				completeTime += System.nanoTime();
				pollTime -= System.nanoTime();
			}
			pollTime += System.nanoTime();
		}
		long endLPQ = System.nanoTime();

		System.out.println("LPQ time: " + ((endLPQ - startLPQ) / REPEATS));
		System.out.println("LPQ add time: " + ((addTime) / REPEATS));
		System.out.println("LPQ iterator time: " + ((iteratorTime) / REPEATS));
		System.out.println("LPQ poll time: " + ((pollTime) / REPEATS));
		System.out.println("LPQ complete time: " + ((completeTime) / REPEATS));

		addTime = 0;

		long startPBQ = System.nanoTime();
		for (int r = 0; r < REPEATS; r++) {
			addTime -= System.nanoTime();
			pbq.addAll(Arrays.asList(shuffled));
			addTime += System.nanoTime();
			LongWithPriority l;
			int i = 0;
			while ((l = pbq.poll()) != null) {
				output[i++] = l;
			}
		}
		long endPBQ = System.nanoTime();

		System.out.println("BPQ time: " + ((endPBQ - startPBQ) / REPEATS));
		System.out.println("BPQ add time: " + ((addTime) / REPEATS));
	}

	private void readFromQueue(ConcurrentLongPriorityQueue<LongWithPriority> lpq, long threshold) {
		Queue<LongWithPriority> q;

		while ((q = lpq.poll(threshold)) != null) {
			System.out.println("Reading queue: " + ((LongPrioritized) q).getPriority());
			boolean checkRequired = !lpq.isFullyBelowThreshold(q, threshold);
			Iterator<LongWithPriority> itr = q.iterator();
			while (itr.hasNext()) {
				LongWithPriority l = itr.next();
				if (checkRequired && l.getPriority() > threshold) {
					continue;
				}
				output[outputIndex++] = l;

				long p = l.getPriority();

				if (p > max) {
					max = p;
				}
				assertTrue("Output is not monotonic increasing", p >= (max - BINSIZE + 1));
				assertTrue("Output exceeds threshold: " + p + " > " + threshold, p <= threshold);
				itr.remove();
			}
			if (lpq.complete(q, threshold)) {
				break;
			}
		}
	}

	private LongWithPriority[] shuffle(LongWithPriority[] a) {

		LongWithPriority[] newArray = new LongWithPriority[a.length];
		System.arraycopy(a, 0, newArray, 0, a.length);

		a = newArray;

		Random r = new Random();

		for (int i = 0; i < a.length; i++) {
			int pos = r.nextInt(a.length - i) + i;
			LongWithPriority temp = a[pos];
			a[pos] = a[i];
			a[i] = temp;
		}

		return a;
	}

	private class LongWithPriority implements LongPrioritized, Comparable<LongWithPriority> {
		private final long priority;

		public LongWithPriority(long priority) {
			this.priority = priority;
		}

		@Override
		public long getPriority() {
			return priority;
		}

		@Override
		public int compareTo(LongWithPriority o) {
			return (o.priority < priority) ? 1 :
					o.priority > priority ? -1 : 0;
		}
	}
}
