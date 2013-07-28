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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UnprotectedCopyOnUpdateArrayTest {
	@Test
	public void testList() {

		UnprotectedCopyOnUpdateArray<Integer> testArray = new UnprotectedCopyOnUpdateArray<>(Integer.class);
		ArrayList<Integer> refArray = new ArrayList<>();

		Random r = new Random();
		int size = r.nextInt(20) + 20;

		for (int i = 0; i < size; i++) {
			int e = r.nextInt(10);
			testArray.add(e);
			refArray.add(e);
		}

		assertTrue("Arrays mismatch after init setup", testEqual(refArray, testArray));

		Integer v = refArray.get(refArray.size() - 1);
		boolean removed = true;
		while (removed) {
			removed = refArray.remove(v);
			testArray.remove(v);

			assertTrue("Arrays mismatch after removing last element", testEqual(refArray, testArray));
		}

		v = refArray.get(0);
		removed = true;
		while (removed) {
			removed = refArray.remove(v);
			testArray.remove(v);

			assertTrue("Arrays mismatch after removing first element", testEqual(refArray, testArray));
		}

		for (int i = 0; i < 50; i++) {
			v = r.nextInt(10);
			if (r.nextInt(5) == 0) {
				v = null;
			}
			boolean add = r.nextBoolean();
			if (add) {
				testArray.add(v);
				refArray.add(v);
			} else {
				testArray.remove(v);
				refArray.remove(v);
			}
			String op = add ? "adding" : "removing";
			assertTrue("Arrays mismatch after " + op + " element " + v, testEqual(refArray, testArray));
		}

		Integer[] test = testArray.toArray(new Integer[0]);
		Integer[] ref = testArray.toArray(new Integer[0]);

		assertTrue("Array length mismatch after call to .toArray()", test.length == ref.length);
		for (int i = 0; i < test.length; i++) {
			assertTrue("Arrays returned from .toArray() do not match", isEquals(test[i], ref[i]));
		}
	}

	@Test
	public void testSet() {

		UnprotectedCopyOnUpdateArray<Integer> testArray = new UnprotectedCopyOnUpdateArray<>(Integer.class, true);
		HashSet<Integer> refArray = new HashSet<>();

		Random r = new Random();
		int size = r.nextInt(20) + 20;

		for (int i = 0; i < size; i++) {
			int e = r.nextInt(10);
			boolean testAdded = testArray.add(e);
			boolean refAdded = refArray.add(e);
			assertTrue("added mismatch, test " + testAdded + ", ref " + refAdded, testAdded == refAdded);
		}

		assertTrue("Arrays mismatch after init setup", testEqualSet(refArray, testArray));

		Integer v;
		for (int i = 0; i < 50; i++) {
			v = r.nextInt(10);
			if (r.nextInt(5) == 0) {
				v = null;
			}
			boolean add = r.nextBoolean();
			if (add) {
				boolean testAdded = testArray.add(v);
				boolean refAdded = refArray.add(v);
				assertTrue("added mismatch, test " + testAdded + ", ref " + refAdded, testAdded == refAdded);
			} else {
				boolean testRemoved = testArray.remove(v);
				boolean refRemoved = refArray.remove(v);
				assertTrue("removed mismatch, test " + testRemoved + ", ref " + refRemoved, testRemoved == refRemoved);
			}
			String op = add ? "adding" : "removing";
			assertTrue("Arrays mismatch after " + op + " element " + v, testEqualSet(refArray, testArray));
		}
	}

	@Test
	public void testGenerics() {

		UnprotectedCopyOnUpdateArray<AtomicReference<Integer>> testArray = new UnprotectedCopyOnUpdateArray<>(AtomicReference.class, true);

		AtomicReference<Integer> one = new AtomicReference<>(1);

		testArray.add(one);

		AtomicReference<Integer>[] arr = testArray.toArray();

		Integer i = arr[0].get();

		assertTrue("Unexcepted value, exp 1, got " + i, Integer.valueOf(1).equals(i));
	}

	private <T> boolean testEqual(Collection<T> c1, Collection<T> c2) {

		Iterator<T> i1 = c1.iterator();
		Iterator<T> i2 = c2.iterator();

		//System.out.println("Testing match");

		while (i1.hasNext() && i2.hasNext()) {
			T v1 = i1.next();
			T v2 = i2.next();
			//System.out.println("Comparing: " + v1 + " and " + v2);
			if (!isEquals(v1, v2)) {
				return false;
			}
		}

		return !(i1.hasNext() || i2.hasNext());
	}

	private <T> boolean testEqualSet(Collection<T> c1, Collection<T> c2) {

		assertTrue("Set sizes are not equal, " + c1.size() + ", " + c2.size(), c1.size() == c2.size());

		for (T t : c1) {
			if (!c2.contains(t)) {
				System.out.print(t + " not in c2");
				return false;
			}
		}

		for (T t : c2) {
			if (!c1.contains(t)) {
				System.out.print(t + " not in c1");
				return false;
			}
		}

		return true;
	}

	private static boolean isEquals(Object o1, Object o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}
}
