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
package org.spout.api.util.set.concurrent;

import gnu.trove.set.TLongSet;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TSyncLongHashSetTest {
	private final static int LENGTH = 10000;
	private final static int THREADS = 20;
	private final static int TEST_COUNT = 2;
	private final static int mask = 0x7F;
	private final static int invMask = -1 - mask;

	private final static boolean PRINT_AVERAGES = false;
	private final static boolean PRINT_ALL_TESTS = false;

	public final static long[] readBuffer = new long[LENGTH];
	public final static long[] writeBuffer = new long[LENGTH];
	
	private int values;

	@Before
	public void setUp() {
		Random rand = new Random();
		
		HashSet<Long> set = new HashSet<Long>();
		
		values = 0;

		for (int count = 0; count < LENGTH; count++) {
			writeBuffer[count] = rand.nextLong();
			if (set.add(writeBuffer[count])) {
				values++;
			}
			int readIndex = count == 0 ? 0 : ((rand.nextInt(count)) & (invMask));
			readBuffer[count] = writeBuffer[readIndex];
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testMap() {
		long trove = 0;
		long troverw = 0;
		long java = 0;
		long reference = 0;

		for (int c = 0; c < TEST_COUNT; c++) {
			Thread[] threads = new Thread[THREADS];

			for (int t = 0; t < THREADS; t++) {
				threads[t] = new TroveReadWriteSet(t);
			}

			troverw += runJoin(threads, "TroveRW");
			
			TLongSet set = TroveReadWriteSet.set;
			
			assertTrue("Incorrect number of entries in set, " + set.size() + ", expected " + values, set.size() == values);
			
			for (int i = 0; i < LENGTH; i++) {
				long value = writeBuffer[i];
				assertTrue("Expected entry, " + value + " at index " + i + " missing", set.contains(value));
			}
			
			set.clear();
		}

		if (PRINT_AVERAGES) {
			System.out.println("--- Averages ---");
			System.out.println("TroveRW: " + (troverw / 1000000.0)/TEST_COUNT + "ms");
		}
		
	}

	private static long runJoin(Thread[] threads, String name) {
		long startTime = System.nanoTime();
		for (int t = 0; t < THREADS; t++) {
			threads[t].start();
		}

		for (int t = 0; t < THREADS; t++) {
			try {
				threads[t].join();
			} catch (InterruptedException e) {
			}
		}
		long time = System.nanoTime() - startTime;

		if (PRINT_ALL_TESTS) {
			System.out.println(name + ": " + (time/1000000.0) + "ms");
		}
		return time;
	}

	public static class TroveReadWriteSet extends Thread {
		public static TSyncLongHashSet set;

		public static AtomicInteger count = new AtomicInteger(LENGTH);

		public TroveReadWriteSet(long seed) {
			count.set(LENGTH - 1);
			set = new TSyncLongHashSet(LENGTH / 128);
		}

		@Override
		public void run() {
			int localCount = count.getAndDecrement();

			while (localCount >= 0) {
				set.add(writeBuffer[localCount]);
				set.contains(readBuffer[localCount]);
				localCount = count.getAndDecrement();
			}
		}
	}
}
