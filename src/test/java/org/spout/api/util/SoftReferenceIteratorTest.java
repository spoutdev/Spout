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
package org.spout.api.util;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SoftReferenceIteratorTest {

	@Test
	public void iteratorTest() {
		List<SoftReference<Integer>> items = new ArrayList<SoftReference<Integer>>();
		List<Integer> realItems = new ArrayList<Integer>();
		items.add(new SoftReference<Integer>(12));
		realItems.add(12);
		items.add(new SoftReference<Integer>(32));
		realItems.add(32);
		items.add(new SoftReference<Integer>(555));
		realItems.add(555);
		items.add(new SoftReference<Integer>(131));
		realItems.add(131);
		SoftReferenceIterator<Integer> iter = new SoftReferenceIterator<Integer>(items);
		Iterator<Integer> realIter = realItems.iterator();
		while (iter.hasNext()) {
			assertTrue(realIter.hasNext());
			assertEquals(iter.next(), realIter.next());
			iter.remove();
		}
		iter = new SoftReferenceIterator<Integer>(items.iterator());
		assertFalse(iter.hasNext());
	}
}
