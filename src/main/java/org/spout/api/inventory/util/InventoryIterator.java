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
package org.spout.api.inventory.util;

import java.util.ListIterator;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;

/**
 * Represents an {@link ListIterator} to iterate through an {@link Inventory}
 */
public class InventoryIterator implements ListIterator<ItemStack> {
	/**
	 * The {@link Inventory} to iterate through
	 */
	private final Inventory inventory;
	/**
	 * The current index of the iterator
	 */
	private int index;

	/**
	 * Constructs a new InventoryIterator
	 * @param inventory to iterate through
	 * @param index to start at
	 */
	public InventoryIterator(Inventory inventory, int index) {
		this.inventory = inventory;
		this.index = index;
	}

	/**
	 * Constructs a new InventoryIterator
	 * @param inventory to iterate through
	 */
	public InventoryIterator(Inventory inventory) {
		this(inventory, 0);
	}

	/**
	 * Gets the inventory being iterated through
	 * @return inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Gets the current position of the inventory's index
	 * @return current index position
	 */
	public int getIndex() {
		return index;
	}

	@Override
	public boolean hasNext() {
		return index < inventory.size();
	}

	@Override
	public ItemStack next() {
		return inventory.get(index++);
	}

	@Override
	public boolean hasPrevious() {
		return index > 0;
	}

	@Override
	public ItemStack previous() {
		return inventory.get(index--);
	}

	@Override
	public int nextIndex() {
		int size = inventory.size();
		if (index == size - 1) {
			return size;
		}
		return index + 1;
	}

	@Override
	public int previousIndex() {
		return index - 1;
	}

	@Override
	public void remove() {
		inventory.remove(index);
	}

	@Override
	public void set(ItemStack item) {
		inventory.set(index, item);
	}

	@Override
	public void add(ItemStack item) {
		inventory.add(index, item);
	}
}
