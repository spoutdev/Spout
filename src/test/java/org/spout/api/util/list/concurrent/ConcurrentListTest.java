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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ConcurrentListTest {
	@Test
	public void delayedAddAllTest() {
		ConcurrentList<Integer> list = new ConcurrentList<Integer>();
		list.addAllDelayed(Arrays.asList(2, 3, 5));
		list.sync();
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).intValue(), 2);
		assertEquals(list.get(1).intValue(), 3);
		assertEquals(list.get(2).intValue(), 5);
	}

	@Test
	public void delayedRemoveAllTest() {
		ConcurrentList<Integer> list = new ConcurrentList<Integer>();
		list.addAllDelayed(Arrays.asList(2, 3, 5));
		list.removeAllDelayed(Arrays.asList(5, 2, 3));
		list.sync();
		assertTrue(list.isEmpty());
	}

	@Test
	public void delayedAddTest() {
		ConcurrentList<Integer> list = new ConcurrentList<Integer>();
		list.addDelayed(5);
		list.addDelayed(3);
		list.sync();
		assertEquals(list.size(), 2);
		assertEquals(list.get(0).intValue(), 5);
		assertEquals(list.get(1).intValue(), 3);
	}

	@Test
	public void delayedRemoveTest() {
		ConcurrentList<Integer> list = new ConcurrentList<Integer>();
		list.addDelayed(5);
		list.removeDelayed(5);
		list.sync();
		assertTrue(list.isEmpty());
	}

	@Test
	public void delayedClearTest() {
		ConcurrentList<Integer> list = new ConcurrentList<Integer>();
		list.addAllDelayed(Arrays.asList(2, 3, 5));
		list.clearDelayed();
		list.addDelayed(2);
		list.sync();
		assertEquals(list.size(), 1);
	}

	@Test
	public void orderTest() {
		ConcurrentList<Integer> list = new ConcurrentList<Integer>();
		list.removeAllDelayed(Arrays.asList(2, 3, 5));
		list.addAllDelayed(Arrays.asList(2, 3, 5));
		list.removeDelayed(7);
		list.addDelayed(7);
		list.sync();
		assertTrue(list.isEmpty());
	}
}
