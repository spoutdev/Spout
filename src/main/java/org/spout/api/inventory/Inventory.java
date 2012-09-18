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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spout.api.inventory.util.Grid;
import org.spout.api.inventory.util.InventoryIterator;

/**
 * Represents a collection of {@link ItemStack}s
 */
public class Inventory implements Serializable, Cloneable, List<ItemStack> {
	/**
	 * The serial version for serializing.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A set of {@link InventoryViewer} to send updates every time a slot is set.
	 */
	private final Set<InventoryViewer> viewers = new HashSet<InventoryViewer>();
	/**
	 * An array of {@link ItemStack}s that act as a mapping between slots and items.
	 */
	private final ItemStack[] contents;

	/**
	 * Constructs a new Inventory with an initial capacity.
	 *
	 * @param size capacity
	 */
	public Inventory(int size) {
		this(new ItemStack[size]);
	}

	/**
	 * Constructs a new Inventory with an initial slot to {@link ItemStack} mapping
	 *
	 * @param contents of the Inventory
	 */
	public Inventory(ItemStack... contents) {
		this.contents = contents;
	}

	/**
	 * Gets the set of {@link InventoryViewer}s viewing the inventory.
	 *
	 * @return set of viewers
	 */
	public Set<InventoryViewer> getViewers() {
		return viewers;
	}

	/**
	 * Adds a new {@link InventoryViewer} to be updated when a slot is set.
	 *
	 * @param viewer to update
	 * @return true if the set contained the viewer
	 */
	public boolean addViewer(InventoryViewer viewer) {
		return viewers.add(viewer);
	}

	/**
	 * Removes an {@link InventoryViewer} from the set of viewers
	 *
	 * @param viewer to remove
	 * @return true if the set contained the viewer
	 */
	public boolean removeViewer(InventoryViewer viewer) {
		return viewers.remove(viewer);
	}

	/**
	 * Sets the data of an {@link ItemStack} at the specified slot to the specified data.
	 *
	 * @param slot to set item at
	 * @param data to set to
	 */
	public void setData(int slot, int data) {
		ItemStack item = get(slot);
		if (item != null) {
			if (data < 1) {
				item = null;
			} else {
				item.setData(Math.min(data, item.getMaxData()));
			}
			set(slot, item);
		}
	}

	/**
	 * Adds data of an {@link ItemStack} at the specified slot to the specified data
	 *
	 * @param slot to set item at
	 * @param amount to add to current data
	 * @return whether the data was added
	 */
	public boolean addData(int slot, int amount) {
		ItemStack item = get(slot);
		if (item != null) {
			setData(slot, item.getData() + amount);
			return true;
		}
		return false;
	}

	/**
	 * Sets the amount of an {@link ItemStack} at the specified slot to the specified amount
	 *
	 * @param slot to set item at
	 * @param amount to set to
	 */
	public void setAmount(int slot, int amount) {
		ItemStack item = get(slot);
		if (item != null) {
			if (amount < 1) {
				item = null;
			} else {
				item.setAmount(Math.min(amount, item.getMaxStackSize()));
			}
			set(slot, item);
		}
	}

	/**
	 * Adds the amount of an {@link ItemStack} at the specified slot to the specified amount
	 *
	 * @param slot to set item at
	 * @param amount to add to
	 * @return true if amount was added
	 */
	public boolean addAmount(int slot, int amount) {
		ItemStack item = get(slot);
		if (item != null) {
			setAmount(slot, item.getAmount() + amount);
			return true;
		}
		return false;
	}

	/**
	 * Before the {@link InventoryViewer}s are notified of a slot change
	 *
	 * @param slot that's being set
	 * @param item that the slot is being set to
	 */
	public void onSlotChanged(int slot, ItemStack item) {
	}

	/**
	 * Updates the slot to the current item in the slot and notifies all viewers
	 *
	 * @param slot to update
	 * @return {@link ItemStack} at the slot
	 */
	public ItemStack update(int slot) {
		ItemStack item = get(slot);
		onSlotChanged(slot, item);
		for (InventoryViewer viewer : viewers) {
			viewer.onSlotSet(this, slot, item);
		}
		return item;
	}

	/**
	 * Updates all slots in the inventory for all viewers
	 */
	public void updateAll() {
		for (int slot = 0; slot < contents.length; slot++) {
			update(slot);
		}
	}

	/**
	 * Constructs a new {@link Grid} with the specified row length
	 *
	 * @param length of grid rows
	 * @return new grid
	 */
	public Grid getGrid(int length) {
		return new Grid(length, contents.length / length);
	}

	@Override
	public int size() {
		return contents.length;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack item : contents) {
			if (item != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean contains(Object o) {
		for (ItemStack item : contents) {
			if (item == null) {
				continue;
			}
			if (item.equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public InventoryIterator iterator() {
		return new InventoryIterator(this);
	}

	@Override
	public Object[] toArray() {
		return contents;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) contents;
	}

	@Override
	public boolean add(ItemStack item) {
		add(0, item);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = get(i);
			if (item != null && item.equals(o)) {
				contents[i] = null;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> objects) {
		for (Object o : objects) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends ItemStack> items) {
		for (ItemStack item : items) {
			add(item);
		}
		return true;
	}

	@Override
	public boolean addAll(int i, Collection<? extends ItemStack> items) {
		for (ItemStack item : items) {
			add(i, item);
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> objects) {
		for (Object o : objects) {
			for (int i = 0; i < contents.length; i++) {
				ItemStack item = contents[i];
				if (item != null && item.equals(o)) {
					contents[i] = null;
				}
			}
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> objects) {
		for (ItemStack item : contents) {
			if (item == null) {
				continue;
			}
			if (!objects.contains(item)) {
				remove(item);
			}
		}
		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < contents.length; i++) {
			contents[i] = null;
		}
	}

	@Override
	public ItemStack get(int i) {
		return contents[i];
	}

	@Override
	public ItemStack set(int i, ItemStack item) {
		contents[i] = item;
		return update(i);
	}

	@Override
	public void add(int i, ItemStack item) {
		for (int index = i; index < contents.length; index++) {
			ItemStack slot = get(index);
			if (slot == null) {
				set(index, item);
				return;
			}
			if (!slot.equalsIgnoreSize(item)) {
				continue;
			}
			slot.stack(item);
			set(index, item);
			if (item.isEmpty()) {
				return;
			}
		}
	}

	@Override
	public ItemStack remove(int i) {
		return contents[i] = null;
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = get(i);
			if (item != null && item.equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = contents.length - 1; i > -1; i--) {
			ItemStack item = get(i);
			if (item != null && item.equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public InventoryIterator listIterator() {
		return new InventoryIterator(this);
	}

	@Override
	public InventoryIterator listIterator(int i) {
		return new InventoryIterator(this, i);
	}

	@Override
	public List<ItemStack> subList(int firstSlot, int lastSlot) {
		ItemStack[] newContents = new ItemStack[lastSlot - firstSlot + 1];
		int index = 0;
		for (int i = firstSlot; i <= lastSlot; i++) {
			newContents[index++] = get(i);
		}
		return new Inventory(newContents);
	}

	@Override
	public Inventory clone() {
		return new Inventory(contents);
	}
}
