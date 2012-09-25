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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.spout.api.inventory.shape.Cube;

import org.spout.api.inventory.shape.Grid;
import org.spout.api.inventory.util.InventoryIterator;
import org.spout.api.material.Material;

/**
 * Represents a collection of {@link ItemStack} implemented as a 
 * {@link List<ItemStack>}
 */
public class Inventory implements Serializable, Cloneable, List<ItemStack> {
	/**
	 * The serial version.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A set of {@link InventoryViewer} to send updates every time a slot is 
	 * set.
	 */
	private final Set<InventoryViewer> viewers = new HashSet<InventoryViewer>();
	/**
	 * An array of {@link ItemStack}s that act as a mapping between slots and 
	 * items.
	 */
	private final ItemStack[] contents;

	/**
	 * Constructs a new Inventory with an initial capacity.
	 *
	 * @param size the initial capacity
	 */
	public Inventory(int size) {
		this(new ItemStack[size]);
	}

	/**
	 * Constructs a new Inventory with an initial slot to {@link ItemStack} 
	 * mapping
	 *
	 * @param contents array of the contents of the inventory
	 */
	public Inventory(ItemStack... contents) {
		this.contents = contents;
	}

	/**
	 * Gets the set of {@link InventoryViewer} viewing the inventory.
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
	 * Sets the data of an {@link ItemStack} at the specified slot to the 
	 * specified data.
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
	 * Adds data of an {@link ItemStack} at the specified slot to the specified 
	 * data
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
	 * Sets the amount of an {@link ItemStack} at the specified slot to the 
	 * specified amount
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
	 * Adds the amount of an {@link ItemStack} at the specified slot to the 
	 * specified amount
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
	 * Gets the amount of the specified {@link Material} in the inventory.
	 * @param material to check
	 * @return amount of specified material
	 */
	public int getAmount(Material material) {
		int amount = 0;
		for (ItemStack item : contents) {
			if (item == null) {
				continue;
			}
			if (item.getMaterial().equals(material)) {
				amount += item.getAmount();
			}
		}
		return amount;
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
	public void update(int slot) {
		ItemStack item = get(slot);
		onSlotChanged(slot, item);
		for (InventoryViewer viewer : viewers) {
			viewer.onSlotSet(this, slot, item);
		}
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
	public Grid grid(int length) {
		return new Grid(length, contents.length / length);
	}
	
	/**
	 * Constructs a new {@link Cube} with the specified row length and the 
	 * specified face height
	 * 
	 * @param length of faces
	 * @param height of faces
	 * @return new cube
	 */
	public Cube cube(int length, int height) {
		return new Cube(length, height, contents.length / length * height);
	}

	/**
	 * Whether the inventory contains at least the amount of the specified
	 * {@link Material}.
	 *
	 * @param material to check
	 * @param amount to check
	 * @return true if inventory contains at least the specified amount of the
	 * specified material
	 */
	public boolean contains(Material material, int amount) {
		return getAmount(material) >= amount;
	}

	/**
	 * Whether the inventory contains exactly the specified amount of the specified
	 * {@link Material}.
	 *
	 * @param material to check
	 * @param amount to check
	 * @return true if contains the specified amount exactly
	 */
	public boolean containsExactly(Material material, int amount) {
		return getAmount(material) == amount;
	}

	/**
	 * Attempts to empty the specified {@link ItemStack} into the inventory
	 * starting at the specified slot. While iterating through the inventories
	 * slots, if a slot is null the remainder of the ItemStack will be put in
	 * the null slot, if a slot is not null but the
	 * {@link org.spout.api.material.Material} of the two ItemStacks do not
	 * match, the iteration will continue to the next slot, and if the slot is
	 * not null and the Material of the two ItemStacks do match the two
	 * ItemStacks will attempt to be 'stacked'. After
	 * {@link ItemStack#stack(org.spout.api.inventory.ItemStack)} has been
	 * called, if the specified ItemStack is empty the call will return,
	 * otherwise it will continue iterating through the slots until either the
	 * stack is empty, or the iteration has ended.
	 *
	 * @param firstSlot slot to start iteration at
	 * @param lastSlot slot to end iteration at
	 * @param item to attempt to add to the inventory
	 */
	public void add(int firstSlot, int lastSlot, ItemStack item) {
		for (int index = firstSlot; index < lastSlot; index++) {
			ItemStack slot = get(index);
			if (slot == null) {
				set(index, item);
				return;
			}
			if (!slot.equalsIgnoreSize(item)) {
				continue;
			}
			slot.stack(item);
			set(index, slot);
			if (item.isEmpty()) {
				return;
			}
		}
	}

	/**
	 * Returns the number of slots in the inventory including slots that may or
	 * may not have a null item.
	 * 
	 * @return number of slots
	 */
	@Override
	public int size() {
		return contents.length;
	}

	/**
	 * Whether the inventory contains any {@link ItemStack} elements in it's
	 * contents.
	 * 
	 * @return true if the inventory contains a not null slot. 
	 */
	@Override
	public boolean isEmpty() {
		for (ItemStack item : contents) {
			if (item != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Whether the inventory contains the specified {@link ItemStack}. If the 
	 * {@link Object} provided is not an ItemStack or a {@link Material} it
	 * will return false. If the object passed in is a Material, true will be
	 * returned if an ItemStack with that material is found with at least 1
	 * amount.
	 * 
	 * @param o ItemStack whose presence is to be tested in this test
	 * @return true if specified ItemStack is present 
	 */
	@Override
	public boolean contains(Object o) {
		for (ItemStack item : contents) {
			if (item == null) {
				continue;
			}
			if (item.equals(o) || item.getMaterial().equals(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an {@link InventoryIterator} to iterate over the inventory in the
	 * correct sequence.
	 * 
	 * @return iterator 
	 */
	@Override
	public InventoryIterator iterator() {
		return new InventoryIterator(this);
	}

	/**
	 * Returns an array whose indexes are represented as slots which can contain
	 * a null or not null {@link ItemStack}.
	 * 
	 * @return an array containing the slot to item mapping of the inventory 
	 */
	@Override
	public Object[] toArray() {
		return contents;
	}

	/**
	 * Returns an array whose indexes are represented as slots which can contain
	 * a null or not null {@link ItemStack}. The array returned is casted to the
	 * given type which must be an array of ItemStacks to avoid a
	 * {@link ClassCastException}.
	 * 
	 * @param <T> type of array to be casted
	 * @param a array of ItemStacks
	 * @return an array containing the slot to item mapping of the inventory
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) contents;
	}

	/**
	 * Attempts to empty the specified {@link ItemStack} into the inventory
	 * starting at the specified slot. While iterating through the inventories
	 * slots, if a slot is null the remainder of the ItemStack will be put in
	 * the null slot, if a slot is not null but the
	 * {@link org.spout.api.material.Material} of the two ItemStacks do not
	 * match, the iteration will continue to the next slot, and if the slot is
	 * not null and the Material of the two ItemStacks do match the two
	 * ItemStacks will attempt to be 'stacked'. After
	 * {@link ItemStack#stack(org.spout.api.inventory.ItemStack)} has been
	 * called, if the specified ItemStack is empty the call will return,
	 * otherwise it will continue iterating through the slots until either the
	 * stack is empty, or the iteration has ended.
	 *
	 * @param item to attempt to add to the inventory
	 * @return true
	 */
	@Override
	public boolean add(ItemStack item) {
		add(0, item);
		return true;
	}

	/**
	 * Sets the slot of the first occurrence of this {@link ItemStack}
	 * or {@link Material} to null.
	 * 
	 * @param o ItemStack to be removed from the inventory
	 * @return true if the inventory contained the specified ItemStack
	 */
	@Override
	public boolean remove(Object o) {
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = get(i);
			if (item == null) {
				continue;
			}
			if (item.equals(o) || item.getMaterial().equals(o)) {
				set(i, null);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the inventory contains all the elements of the specified
	 * collection.
	 * 
	 * @param objects collection to be checked for containment in the list
	 * @return true if the inventory contained all elements in the collection
	 */
	@Override
	public boolean containsAll(Collection<?> objects) {
		Iterator<?> i = objects.iterator();
		while (i.hasNext()) {
			if (!contains(i.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Appends all of the elements in the specified collection to the first
	 * possible slot in the inventory in the order specified by the collections
	 * {@link Iterator}.
	 * 
	 * @param items to be added
	 * @return true
	 */
	@Override
	public boolean addAll(Collection<? extends ItemStack> items) {
		Iterator<? extends ItemStack> i = items.iterator();
		while (i.hasNext()) {
			add(i.next());
		}
		return true;
	}

	/**
	 * Appends all of the elements in the specified collection to the first
	 * possible slot in the inventory starting at the specified index in the
	 * order specified by the collections {@link Iterator}.
	 * 
	 * @param i slot to try to add each item in the collection to
	 * @param items to add
	 * @return true 
	 */
	@Override
	public boolean addAll(int i, Collection<? extends ItemStack> items) {
		Iterator<? extends ItemStack> iter = items.iterator();
		while (iter.hasNext()) {
			add(i, iter.next());
		}
		return true;
	}

	/**
	 * Sets the slot of each element in the specified collection to null.
	 * 
	 * @param objects to remove
	 * @return true 
	 */
	@Override
	public boolean removeAll(Collection<?> objects) {
		Iterator<?> iter = objects.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			for (int i = 0; i < contents.length; i++) {
				ItemStack item = get(i);
				if (item == null) {
					continue;
				}
				if (item.equals(o) || item.getMaterial().equals(o)) {
					set(i, null);
				}
			}
		}
		return true;
	}

	/**
	 * Retains only the elements in this inventory that are contained in the
	 * specified collection; removes all items that are not contained within
	 * the specified collection.
	 * 
	 * @param objects
	 * @return true 
	 */
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

	/**
	 * Sets every slot in the inventory to null. Every slot in this inventory
	 * will be null after this call.
	 */
	@Override
	public void clear() {
		for (int i = 0; i < contents.length; i++) {
			set(i, null);
		}
	}

	/**
	 * Returns the {@link ItemStack} at the specified slot in this inventory.
	 * 
	 * @param i slot to get the item from
	 * @return item in the slot
	 */
	@Override
	public ItemStack get(int i) {
		return contents[i];
	}

	/**
	 * Replaces the {@link ItemStack} at the specified slot in this inventory
	 * with the specified ItemStack.
	 * 
	 * @param i slot to set the item at
	 * @param item to set in the specified slot
	 * @return the item previously at the slot 
	 */
	@Override
	public ItemStack set(int i, ItemStack item) {
		ItemStack old = get(i);
		contents[i] = item;
		update(i);
		return old;
	}

	/**
	 * Attempts to empty the specified {@link ItemStack} into the inventory
	 * starting at the specified slot. While iterating through the inventories
	 * slots, if a slot is null the remainder of the ItemStack will be put in
	 * the null slot, if a slot is not null but the
	 * {@link org.spout.api.material.Material} of the two ItemStacks do not
	 * match, the iteration will continue to the next slot, and if the slot is
	 * not null and the Material of the two ItemStacks do match the two
	 * ItemStacks will attempt to be 'stacked'. After
	 * {@link ItemStack#stack(org.spout.api.inventory.ItemStack)} has been
	 * called, if the specified ItemStack is empty the call will return, 
	 * otherwise it will continue iterating through the slots until either the
	 * stack is empty, or the iteration has ended.
	 * 
	 * @param i slot to start iteration at
	 * @param item to attempt to add to the inventory 
	 */
	@Override
	public void add(int i, ItemStack item) {
		add(i, contents.length, item);
	}

	/**
	 * Sets the specified slot of the inventory to null.
	 * 
	 * @param i slot to set to null
	 * @return the {@link ItemStack} previously at the slot 
	 */
	@Override
	public ItemStack remove(int i) {
		return set(i, null);
	}

	/**
	 * Returns the slot of the first occurrence of the specified element, or -1
	 * if this inventory does not contain the element.
	 * 
	 * @param o element to search for
	 * @return the slot of the first occurrence of the specified element 
	 */
	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = get(i);
			if (item == null) {
				continue;
			}
			if (item.equals(o) || item.getMaterial().equals(o)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the slot of the last occurrence of the specified element, or -1
	 * if this inventory does not contain the element.
	 * 
	 * @param o element to search for
	 * @return the slot of the last occurrence of the specified element 
	 */
	@Override
	public int lastIndexOf(Object o) {
		for (int i = contents.length - 1; i > -1; i--) {
			ItemStack item = get(i);
			if (item == null) {
				continue;
			}
			if (item.equals(o) || item.getMaterial().equals(o)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns an {@link InventoryIterator} to iterate over the inventory in the
	 * proper order.
	 * 
	 * @return new inventory iterator 
	 */
	@Override
	public InventoryIterator listIterator() {
		return new InventoryIterator(this);
	}

	/**
	 * Returns an {@link InventoryIterator} to iterate over the inventory in the
	 * proper order starting at the specified slot.
	 * 
	 * @param i slot to start iterating from
	 * @return new inventory iterator 
	 */
	@Override
	public InventoryIterator listIterator(int i) {
		return new InventoryIterator(this, i);
	}

	/**
	 * Returns a view of the portion of this inventory between the specified 
	 * first slot and the specified last slot.
	 * 
	 * @param firstSlot slot to start list at
	 * @param lastSlot slot to end list at
	 * @return view of the range between the specified first slot and the 
	 * specified last slot.
	 */
	@Override
	public Inventory subList(int firstSlot, int lastSlot) {
		ItemStack[] newContents = new ItemStack[lastSlot - firstSlot + 1];
		int index = 0;
		for (int i = firstSlot; i <= lastSlot; i++) {
			newContents[index++] = get(i);
		}
		return new Inventory(newContents);
	}

	/**
	 * Creates and returns a new inventory with the current slot to
	 * {@link ItemStack} array. With the indexes of the array representing the
	 * slots and the contents representing the slot's contents.
	 * 
	 * @return new inventory with the same contents  
	 */
	@Override
	public Inventory clone() {
		return new Inventory(contents.clone());
	}
}
