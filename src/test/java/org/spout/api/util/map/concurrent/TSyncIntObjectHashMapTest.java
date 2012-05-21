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
package org.spout.api.util.map.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.junit.Before;
import org.junit.Test;

public class TSyncIntObjectHashMapTest {
	private final static int LENGTH = 10000;
	private final static int THREADS = 20;
	private final static int TEST_COUNT = 2;
	private final static int mask = 0x7F;
	private final static int invMask = -1 - mask;

	private final static boolean PRINT_AVERAGES = false;
	private final static boolean PRINT_ALL_TESTS = false;

	public final static int[] readBuffer = new int[LENGTH];
	public final static int[] writeBuffer = new int[LENGTH];
	public final static int[] writeData = new int[LENGTH];
	public final static int[] readData = new int[LENGTH];

	@Before
	public void setUp() {
		Random rand = new Random();

		for (int count = 0; count < LENGTH; count++) {
			writeBuffer[count] = rand.nextInt();
			writeData[count] = rand.nextInt();
			int readIndex = count == 0 ? 0 : ((rand.nextInt(count)) & (invMask));
			readBuffer[count] = writeBuffer[readIndex];
			readData[count] = writeData[readIndex];
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
				threads[t] = new TroveMap(t);
			}

			trove += runJoin(threads, "TroveMap");

			for (int t = 0; t < THREADS; t++) {
				threads[t] = new JavaMap(t);
			}

			java += runJoin(threads, "JavaMap");

			for (int t = 0; t < THREADS; t++) {
				threads[t] = new ReferenceMap(t);
			}

			reference += runJoin(threads, "ReferenceMap");

			for (int t = 0; t < THREADS; t++) {
				threads[t] = new TroveReadWriteMap(t);
			}

			troverw += runJoin(threads, "TroveRW");
		}

		if (PRINT_AVERAGES) {
			System.out.println("--- Averages ---");
			System.out.println("Trove: " + (trove / 1000000.0)/TEST_COUNT + "ms");
			System.out.println("TroveRW: " + (troverw / 1000000.0)/TEST_COUNT + "ms");
			System.out.println("Java: " + (java / 1000000.0)/TEST_COUNT + "ms");
			System.out.println("Reference: " + (reference / 1000000.0)/TEST_COUNT + "ms");
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

	public static class TroveMap extends Thread {
		public static TIntObjectMap<Integer> map;

		public static AtomicInteger count = new AtomicInteger(LENGTH);

		public int[] buffer = new int[LENGTH];
		public Integer[] buffer2 = new Integer[LENGTH];

		Random rand;

		public TroveMap(long seed) {
			rand = new Random(seed);
			count.set(LENGTH - 1);
			map = TCollections.synchronizedMap(new TIntObjectHashMap<Integer>(LENGTH / 128));
		}

		public void run() {
			int localCount = count.getAndDecrement();

			while (localCount > 0) {
				if ((localCount & mask) == 0) {
					map.put(writeBuffer[localCount], writeData[localCount]);
				} else {
					buffer2[localCount] = map.get(readBuffer[localCount]);
					if (buffer2[localCount] != null) {
						assertTrue("Map error for Trove standard map", buffer2[localCount] == readData[localCount]);
					}
				}

				localCount = count.getAndDecrement();
			}
		}
	}

	public static class TroveReadWriteMap extends Thread {
		public static TSyncIntObjectHashMap<Integer> map;

		public static AtomicInteger count = new AtomicInteger(LENGTH);

		public int[] buffer = new int[LENGTH];
		public Integer[] buffer2 = new Integer[LENGTH];

		Random rand;

		public TroveReadWriteMap(long seed) {
			rand = new Random(seed);
			count.set(LENGTH - 1);
			map = new TSyncIntObjectHashMap<Integer>(LENGTH / 128);
		}

		public void run() {
			int localCount = count.getAndDecrement();

			while (localCount > 0) {
				if ((localCount & mask) == 0) {
					map.put(writeBuffer[localCount], writeData[localCount]);
				} else {
					buffer2[localCount] = map.get(readBuffer[localCount]);
					if (buffer2[localCount] != null) {
						assertTrue("Map error for Trove RW map", buffer2[localCount] == readData[localCount]);
					}
				}
				localCount = count.getAndDecrement();
			}
		}
	}

	public static class JavaMap extends Thread {
		public static ConcurrentHashMap<Integer, Integer> map;

		public static AtomicInteger count = new AtomicInteger(LENGTH);

		public int[] buffer = new int[LENGTH];
		public Integer[] buffer2 = new Integer[LENGTH];

		Random rand;

		public JavaMap(long seed) {
			rand = new Random(seed);
			map = new ConcurrentHashMap<Integer, Integer>(LENGTH / 128);
			count.set(LENGTH - 1);
		}

		public void run() {
			int localCount = count.getAndDecrement();

			while (localCount > 0) {
				if ((localCount & mask) == 0) {
					map.put(writeBuffer[localCount], writeData[localCount]);
				} else {
					buffer2[localCount] = map.get(readBuffer[localCount]);
					if (buffer2[localCount] != null) {
						assertTrue("Map error for Java map", buffer2[localCount] == readData[localCount]);
					}
				}

				localCount = count.getAndDecrement();
			}
		}
	}

	public static class ReferenceMap extends Thread {
		public static AtomicInteger count = new AtomicInteger(LENGTH);

		public int[] buffer = new int[LENGTH];
		public Integer[] buffer2 = new Integer[LENGTH];

		Random rand;

		public ReferenceMap(long seed) {
			rand = new Random(seed);
			count.set(LENGTH - 1);
		}

		public void run() {
			int localCount = count.getAndDecrement();

			while (localCount > 0) {
				if ((localCount & mask) == 0) {
					//map.put(writeBuffer[localCount], writeData[localCount]);
				} else {
					buffer2[localCount] = readData[localCount];
					if (buffer2[localCount] != null) {
						assertTrue("Map error for Reference map", buffer2[localCount] == readData[localCount]);
					}
				}
				localCount = count.getAndDecrement();
			}
		}
	}
}
