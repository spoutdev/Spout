/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.util.list.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

public class ConcurrentLongPriorityQueueTest {
	
	private static int LENGTH = 16384;
	private static int COPIES = 256;
	private static int BINSIZE = 4;

	long max = Long.MIN_VALUE;
	
	LongWithPriority[] output = new LongWithPriority[LENGTH];
	int outputIndex;
	
	@Test
	public void test() {
		
		LongWithPriority[] sorted = new LongWithPriority[LENGTH];
		
		for (int i = 0; i < LENGTH; i++) {
			sorted[i] = new LongWithPriority(i / COPIES);
		}
		
		LongWithPriority[] shuffled = shuffle(sorted);
		
		ConcurrentLongPriorityQueue<LongWithPriority> lpq = new ConcurrentLongPriorityQueue<LongWithPriority>(BINSIZE);
		
		for (int i = 0; i < LENGTH; i++) {
			lpq.add(shuffled[i]);
		}
		
		int i = 0;
		
		max = Long.MIN_VALUE;
		
		outputIndex = 0;

		readFromQueue(lpq, 16);
		
		readFromQueue(lpq, Long.MAX_VALUE);
		
		for (i = 0; i < LENGTH; i++) {
			if (output[i] != null) {
				System.out.println(sorted[i].getPriority() + ":" + output[i].getPriority());
			} else {
				System.out.println(sorted[i].getPriority() + ":" + output[i]);
			}
		}
		
	}
	
	private void readFromQueue(ConcurrentLongPriorityQueue<LongWithPriority> lpq, long threshold) {
		ConcurrentLinkedQueue<LongWithPriority> q;

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
		
		for (int i = 0; i < a.length; i++) {
			newArray[i] = a[i];
		}
		
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
	
	private class LongWithPriority implements LongPrioritized {

		private final long priority;
		
		public LongWithPriority(long priority) {
			this.priority = priority;
		}
		
		@Override
		public long getPriority() {
			return priority;
		}
		
	}
	
}
