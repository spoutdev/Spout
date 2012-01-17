/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.spout.api.io.store.MemoryStore;
import org.junit.Before;
import org.junit.Test;

public class StringMapTest {
	private StringMap subject;
	private MemoryStore<Integer> store;

	@Before
	public void setUp() {
		store = new MemoryStore<Integer>();
		subject = new StringMap(null, store, 0, 100);
	}

	@Test
	public void testKeyRegistries() {
		// Check that the store is returning null on an unused key
		assertNull(store.get("key1"));

		// Check if the first registered value is the first possible entry, 0
		subject.register("key1");
		assertEquals(store.get("key1").intValue(), 0);

		// Check if registering multiple times and multiple keys does not interfere with previously
		// registered values.
		subject.register("key1");
		subject.register("key2");
		assertEquals(store.get("key1").intValue(), 0);
		assertEquals(store.get("key2").intValue(), 1);
	}
}
