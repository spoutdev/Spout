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
package org.spout.api.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.spout.api.material.Material;

public class InventoryTest {
	private int size = 10;
	private Inventory subject = new Inventory(size);
	private static Material parentMaterial = new Material((short) 0x0001, "Parent") {
	};
	private static Material childMaterial = new Material("Child", (short) 0x0001, parentMaterial) {
	};
	private static Material otherMaterial = new Material((short) 0, "Testing2", (short) 0) {
	};

	private ItemStack testing = new ItemStack(parentMaterial, (short) 2, 1);
	private ItemStack testingSubMaterial = new ItemStack(childMaterial, (short) 91, 1);
	private ItemStack testing2 = new ItemStack(otherMaterial, (short) 3, 5);

	@Before
	public void setUp() {
		subject.addItem(testing, true);
		subject.addItem(testingSubMaterial, true);
	}

	@Test
	public void testContains() {
		assertTrue(subject.contains(testing));
		assertTrue(!subject.contains(testing2));
		assertTrue(subject.contains(testingSubMaterial));
	}

	@Test
	public void testIterator() {
		Inventory inventory = new Inventory(5);
		inventory.setItem(0, new ItemStack(parentMaterial, 1));
		inventory.setItem(1, new ItemStack(parentMaterial, 2));
		inventory.setItem(2, new ItemStack(parentMaterial, 3));
		inventory.setItem(3, null);
		inventory.setItem(4, new ItemStack(parentMaterial, 5));
		int counter = 1;
		for (ItemStack item : inventory) {
			if (item == null) {
				assertEquals(counter, 4);
			} else {
				assertEquals(item.getAmount(), counter);
			}
			counter++;
		}
	}
}
