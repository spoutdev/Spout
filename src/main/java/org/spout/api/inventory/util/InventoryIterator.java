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
	 * The last {@link Operation} performed on this iterator.
	 */
	private Operation lastOperation = Operation.NONE;

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

	@Override
	public boolean hasNext() {
		return index < inventory.size();
	}

	@Override
	public ItemStack next() {
		lastOperation = Operation.NEXT;
		return inventory.get(index++);
	}

	@Override
	public boolean hasPrevious() {
		return index > 0;
	}

	@Override
	public ItemStack previous() {
		lastOperation = Operation.PREVIOUS;
		return inventory.get(--index);
	}

	@Override
	public int nextIndex() {
		return index;
	}

	@Override
	public int previousIndex() {
		return index - 1;
	}

	@Override
	public void remove() {
		switch (lastOperation) {
			case NEXT:
				inventory.remove(index - 1);
				break;
			case PREVIOUS:
				inventory.remove(index);
				break;
			case NONE:
				throw new IllegalStateException("Cannot remove element before first operation.");
		}
	}

	@Override
	public void set(ItemStack item) {
		switch (lastOperation) {
			case NEXT:
				inventory.set(index - 1, item);
				break;
			case PREVIOUS:
				inventory.set(index, item);
				break;
			case NONE:
				throw new IllegalStateException("Cannot set element before first operation.");
		}
	}

	@Override
	public void add(ItemStack item) {
		switch (lastOperation) {
			case NEXT:
				inventory.add(index - 1, item);
				break;
			default:
				inventory.add(index, item);
		}
	}

	/**
	 * Represents an Operation on this iterator
	 */
	public enum Operation {
		/**
		 * Signifies that {@link this#next()} was called.
		 */
		NEXT,
		/**
		 * Signifies that {@link this#previous()} was called.
		 */
		PREVIOUS,
		/**
		 * Signifies that no operation has been called on this iterator.
		 */
		NONE;
	}
}
