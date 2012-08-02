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

import java.util.Random;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Atomic3DObjectReferenceArrayMapTest {
	private final static int EDGEX = 3;
	private final static int EDGEY = 3;
	private final static int EDGEZ = 640;

	private final Atomic3DObjectReferenceArrayMap<FakeObject> map = new Atomic3DObjectReferenceArrayMap<FakeObject>(3);
	
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
	
	private FakeObject[] shuffle(FakeObject[] a) {
		
		Random r = new Random();
		
		for (int i = 0; i < a.length; i++) {
			int pos = r.nextInt(a.length - i) + i;
			FakeObject temp = a[pos];
			a[pos] = a[i];
			a[i] = temp;
		}
		
		return a;
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
