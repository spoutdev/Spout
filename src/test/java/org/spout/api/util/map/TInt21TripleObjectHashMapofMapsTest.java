/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TInt21TripleObjectHashMapofMapsTest {

	@Test
	public void test() {
		TInt21TripleObjectHashMapOfMaps<String, Object> map = new TInt21TripleObjectHashMapOfMaps<String, Object>();
		assertTrue(map.isEmpty());
		map.put(0, 0, 0, "One", "One-Value");
		map.put(0, 0, 0, "Two", "Two-Value");
		assertSame(1, map.size());
		assertSame(2, map.get(0, 0, 0).size());
		assertEquals("One-Value", map.get(0, 0, 0, "One"));
		assertEquals("Two-Value", map.get(0, 0, 0, "Two"));
		
		map.put(1, 1, 1, "Three", "Three-Value");
		assertSame(2, map.size());
		assertSame(1, map.get(1, 1, 1).size());
		assertEquals("Three-Value", map.get(1, 1, 1, "Three"));
		assertEquals(null, map.get(1, 1, 1, "Four"));
		
		map.remove(0, 0, 0, "Two");
		assertSame(2, map.size());
		assertSame(1, map.get(0, 0, 0).size());
		assertEquals("One-Value", map.get(0, 0, 0, "One"));
		assertEquals(null, map.get(1, 1, 1, "Two"));
		
		map.remove(0, 0, 0, "One");
		assertSame(1, map.size());
		assertSame(null, map.get(0, 0, 0));
	}
}
