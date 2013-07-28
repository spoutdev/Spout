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
package org.spout.api.util.map;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WeakValueHashMapTest {
	private final static boolean RUN_TEST = true;
	private final static int ARRAY_SIZE = 10 * 1024 * 1024;

	@Test
	public void test() {

		if (!RUN_TEST) {
			return;
		}

		WeakValueHashMap<Integer, byte[]> map = new WeakValueHashMap<>();

		HashMap<Integer, WeakValueHashMap<Integer, byte[]>.KeyReference> internalMap = map.map;

		byte[][] hardArray = new byte[10][];

		for (int i = 0; i < 100; i++) {
			byte[] arr = new byte[ARRAY_SIZE];
			if (i < 10) {
				hardArray[i] = arr;
			}
			map.put(i, arr);
		}

		for (int j = 0; j < 20 && internalMap.size() > 10; j++) {
			for (int i = 0; i < 20; i++) {
				System.gc();
				map.flushKeys();
			}
		}

		for (int i = 0; i < 10; i++) {
			assertTrue("Hard linked array lost " + i, Arrays.equals(map.get(i), hardArray[i]));
		}

		assertTrue("Weak references weren't completely garbage collected, exp 10, got " + internalMap.size(), internalMap.size() < 15);
	}
}
