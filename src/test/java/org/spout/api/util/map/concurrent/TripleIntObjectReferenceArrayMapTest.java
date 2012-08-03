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
import gnu.trove.TCollections;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.Random;

import org.junit.Test;

public class TripleIntObjectReferenceArrayMapTest {
	private final static int EDGEX = 3;
	private final static int EDGEY = 3;
	private final static int EDGEZ = 640;
	
	private final static int SPEED_EDGE = 16;
	private final static int SPEED_LENGTH = 1000;
	
	private final static int THREADS = 20;
	private final static boolean PRINT_ALL_TESTS = false;
	private final static int REPEATS = 5;

	private final TripleIntObjectReferenceArrayMap<FakeObject> map = new TripleIntObjectReferenceArrayMap<FakeObject>(3);
	
	@Test
	public void test() {
		
		FakeObject[] objects = new FakeObject[EDGEX * EDGEY * EDGEZ];
		
		int halfEdgeX = EDGEX >> 1;
		int halfEdgeY = EDGEY >> 1;
		int halfEdgeZ = EDGEZ >> 1;
		
		int i = 0;
		
		for (int x = 0; x < EDGEX; x++) {
			for (int y = 0; y < EDGEY; y++) {
				for (int z = 0; z < EDGEZ; z++) {
					objects[i++] = new FakeObject(x - halfEdgeX, y - halfEdgeY, z - halfEdgeZ);
				}
			}
		}
		
		shuffle(objects);
		
		for (i = 0; i < objects.length; i++) {
			FakeObject f = objects[i];
			map.put(f.getX(), f.getY(), f.getZ(), f);
		}
		
		shuffle(objects);

		for (i = 0; i < objects.length; i++) {
			FakeObject f = objects[i];
			FakeObject m = map.get(f.getX(), f.getY(), f.getZ());
			assertTrue("Map did not contain object got " + m + " expected " + f, m == f);
		}
		
		for (int x = -halfEdgeX; x < halfEdgeX; x++) {
			for (int y = -halfEdgeY; y < halfEdgeY; y++) {
				for (int z = -halfEdgeZ; z < halfEdgeZ; z++) {
					FakeObject m = map.get(x, y, z);
					assertTrue("Map did not contain object with the expected coordinates, " + x + ", " + y + ", " + z + ", got " + m, m.test(x, y, z));
				}
			}
		}
		
		boolean thrown = false;
		try {
			FakeObject f = objects[0];
			map.put(f.getX(), f.getY(), f.getZ(), f);
		} catch (IllegalStateException ise) {
			thrown = true;
		}
		assertTrue("IllegalStateException was not thrown when adding the same element to the map more than once", thrown);

		Random r = new Random();
		
		for (i = 0; i < (objects.length >> 4); i++) {
			FakeObject ref = objects[r.nextInt(objects.length)];
			int x = ref.getX();
			int y = ref.getY();
			int z = ref.getZ();

			FakeObject newObject = new FakeObject(x, y, z);
			
			FakeObject getObject = map.get(x, y, z);
			assertTrue("Location " + x + ", " + y + ", " + z + " expected to have object", getObject != null);

			FakeObject old = map.putIfAbsent(x, y, z, newObject);
			assertTrue("Object added to map with putIfAbsent even though location was already occupied", old == getObject);

			FakeObject oldRemoved = map.remove(x, y, z);
			assertTrue("putIfAbsent did not return the current version of the object", oldRemoved == old);

			FakeObject oldPut = map.put(x, y, z, newObject);
			assertTrue("put did not return null when inserting into an unfilled location", oldPut == null);

			FakeObject newPut = new FakeObject(x, y, z);
			oldPut = map.put(x, y, z, newPut);
			assertTrue("put did not return old object", oldPut == newObject);
			
			boolean success = map.remove(x, y, z, newObject);
			assertTrue("remove(value) removed object when it didn't match", !success);
			
			success = map.remove(x, y, z, newPut);
			assertTrue("remove(value) did not remove an object when value matched", success);
			assertTrue("map get did not return null for empty location", map.get(x, y, z) == null);
			assertTrue("map successfully removed object from empty location", map.remove(x, y, z) == null);

			old = map.putIfAbsent(x, y, z, newObject);
			assertTrue("putIfAbsent did not return null when adding an object", old == null);
		}

	}
	
	@Test
	public void speedTest() {
		
		for (int i = 0; i < REPEATS; i++) {
			TripleIntObjectMapTest.testMap(new TripleIntObjectReferenceArrayMap<FakeObject>(3), "AtomicReferenceArrayTree");

			TripleIntObjectMapTest.testMap(new TSyncInt21TripleObjectHashMap<FakeObject>(), "TroveRWHashMap");
			
			TLongObjectMap<FakeObject> m = TCollections.synchronizedMap(new TLongObjectHashMap<FakeObject>());
			TripleIntObjectMapTest.testMap(new TSyncInt21TripleObjectHashMap<FakeObject>(m), "TroveSyncedMap");
			
			TripleIntObjectMapTest.testMap(new ArrayMap(), "3D Array");
			
			System.out.println();
		}
		
	}
	
	private static FakeObject[] shuffle(FakeObject[] a) {
		
		Random r = new Random();
		
		for (int i = 0; i < a.length; i++) {
			int pos = r.nextInt(a.length - i) + i;
			FakeObject temp = a[pos];
			a[pos] = a[i];
			a[i] = temp;
		}
		
		return a;
	}
	
	public static interface Map<T> {
		
		public T get(int x, int y, int z);
		
	}
	
	public static class TripleIntObjectMapTest extends Thread {
		private final TripleIntObjectMap<FakeObject> map;

		private int[] xx;
		private int[] yy;
		private int[] zz;
		
		public TripleIntObjectMapTest(TripleIntObjectMap<FakeObject> map) {
			this.map = map;
			
			xx = new int[1024];
			yy = new int[1024];
			zz = new int[1024];
			
			Random r = new Random();
			
			int speedEdgeDiv2 = SPEED_EDGE >> 1;
			for (int i = 0; i < 1024; i++) {
				xx[i] = r.nextInt(SPEED_EDGE) - speedEdgeDiv2;
				yy[i] = r.nextInt(SPEED_EDGE) - speedEdgeDiv2;
				zz[i] = r.nextInt(SPEED_EDGE) - speedEdgeDiv2;
			}
			
			
		}
		
		public void run() {
			for (int i = 0; i < SPEED_LENGTH; i++) {
				int index = i & 0x3FF;
				int x = xx[index];
				int y = yy[index];
				int z = zz[index];
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
				if (!map.get(x, y, z).test(x, y, z)) {
					throw new IllegalStateException("Map did not return correct object");
				}
			}
		}

		public static void initMap(TripleIntObjectMap<FakeObject> map) {
			int speedEdgeDiv2 = SPEED_EDGE >> 1;

			for (int x = -speedEdgeDiv2; x < speedEdgeDiv2; x++) {
				for (int y = -speedEdgeDiv2; y < speedEdgeDiv2; y++) {
					for (int z = -speedEdgeDiv2; z < speedEdgeDiv2; z++) {
						map.put(x, y, z, new FakeObject(x, y, z));
					}
				}
			}
		}
		
		public static long testMap(TripleIntObjectMap<FakeObject> map, String name) {
			
			initMap(map);
			
			Thread[] threads = new Thread[THREADS];
			
			for (int i = 0; i < THREADS; i++) {
				threads[i] = new TripleIntObjectMapTest(map);
			}
			
			return runJoin(threads, name);
			
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
				System.out.println(name + ": " + (time/1000000.0) + "ms (" + SPEED_LENGTH + "0 get operations)");
			}
			return time;
		}
	}
	
	private static class ArrayMap implements TripleIntObjectMap<FakeObject> {
		
		private final int offset = SPEED_EDGE >> 1;
		
		FakeObject[][][] map = new FakeObject[SPEED_EDGE + 1][SPEED_EDGE + 1][SPEED_EDGE + 1];

		@Override
		public FakeObject get(int x, int y, int z) {
			return map[x + offset][y + offset][z + offset];
		}

		@Override
		public FakeObject remove(int x, int y, int z) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean remove(int x, int y, int z, FakeObject value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public FakeObject put(int x, int y, int z, FakeObject value) {
			map[x + offset][y + offset][z + offset] = value;
			return null;
		}

		@Override
		public FakeObject putIfAbsent(int x, int y, int z, FakeObject value) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class FakeObject {
		
		private int x;
		private int y;
		private int z;
		
		public FakeObject(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public boolean test(int x, int y, int z) {
			return this.x == x && this.y == y && this.z == z;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getZ() {
			return z;
		}
		
	}
}
