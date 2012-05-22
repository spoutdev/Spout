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

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.List;

import org.spout.api.material.source.MaterialSource;

/**
 * Represents an inventory, usually owned by an entity. In a grid-style
 * inventory, slot ordering starts in the lower-left corner at zero, going
 * left-to-right for each row.
 */
public class Inventory extends InventoryBase {

	private static final long serialVersionUID = 0L;
	private final ItemStack[] contents;
	private TIntSet hidden = new TIntHashSet();
	private final List<InventoryViewer> viewers = new ArrayList<InventoryViewer>();
	private int currentSlot;

	public Inventory(int size) {
		this(new ItemStack[size]);
	}

	public Inventory(ItemStack[] contents) {
		this.contents = contents;
		this.currentSlot = 0;
	}

	@Override
	public void setHiddenSlot(int slot, boolean add) {
		if (add) {
			hidden.add(slot);
		} else {
			hidden.remove(slot);
		}
	}

	@Override
	public boolean isHiddenSlot(int slot) {
		return hidden.contains(slot);
	}

	/**
	 * Adds a single {@link InventoryViewer} to this Inventory<br>
	 * This viewer will be notified of item changes in this Inventory.
	 * 
	 * @param viewer to add
	 * @return True if the viewer was added, False if not
	 */
	public boolean addViewer(InventoryViewer viewer) {
		if (viewers.contains(viewer)) {
			return false;
		}
		viewers.add(viewer);
		viewer.updateAll(this, contents);
		return true;
	}

	/**
	 * Removes a single {@link InventoryViewer} from this Inventory<br>
	 * This viewer will no longer be notified of item changes in this Inventory.
	 * 
	 * @param viewer to add
	 * @return True if the viewer was removed, False if not
	 */
	public boolean removeViewer(InventoryViewer viewer) {
		return viewers.remove(viewer);
	}

	/**
	 * Gets all the {@link InventoryViewer} of the inventory
	 * 
	 * @return viewers of inventory
	 */
	public List<InventoryViewer> getViewers() {
		return viewers;
	}

	@Override
	public int getSize() {
		return contents.length;
	}

	@Override
	public ItemStack[] getContents() {
		return contents;
	}

	@Override
	public ItemStack getItem(int slot) {
		if (contents[slot] == null) {
			return null;
		} else {
			return contents[slot].clone();
		}
	}

	/**
	 * Adds a given amount to the data of the currently selected item<br><br>
	 * 
	 * If the data becomes negative the item is removed and False is returned<br>
	 * Otherwise True is returned.
	 * @param amount of data to add
	 * @return True if the item data was successfully added
	 */
	public boolean addCurrentItemData(int amount) {
		return this.addItemData(this.getCurrentSlot(), amount);
	}

	/**
	 * Sets the item at the currently selected slot index<br>
	 * The item is cloned before adding
	 * 
	 * @param item to set to
	 */
	public void setCurrentItem(ItemStack item) {
		this.setItem(this.getCurrentSlot(), item);
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		contents[slot] = item == null || item.getAmount() == 0 ? null : item.clone();
		for (InventoryViewer viewer : viewers) {
			viewer.onSlotSet(this, slot, contents[slot]);
		}
	}

	/**
	 * Checks if the currently selected item matches the material
	 * @param material to compare with
	 * @return True if the item matches the material or both are null
	 */
	public boolean isCurrentItem(MaterialSource material) {
		return this.isItem(this.getCurrentSlot(), material);
	}

	/**
	 * Adds a certain amount of the item at the currently selected slot<br>
	 * You can add a negative amount to subtract
	 * @param amount to add
	 * @return True if successful, which means the item was not null and could add the amount
	 */
	public boolean addCurrentItemAmount(int amount) {
		return this.addItemAmount(this.getCurrentSlot(), amount);
	}

	/**
	 * Gets the currently selected item
	 * 
	 * @return the selected item, or null if the slot is empty
	 */
	public ItemStack getCurrentItem() {
		return getItem(currentSlot);
	}

	/**
	 * Gets the currently selected item slot
	 * 
	 * @return the item slot index
	 */
	public int getCurrentSlot() {
		return currentSlot;
	}

	/**
	 * Sets the currently selected item slot
	 * @param slot index to set to
	 */
	public void setCurrentSlot(int slot) {
		if (slot < 0 || slot >= contents.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		currentSlot = slot;
	}

	/**
	 * Checks if all of the items in the inventory can be added to this Inventory
	 * 
	 * @param inventory containing the items to add
	 * @return whether addition is possible
	 */
	public boolean canAddAll(Inventory inventory) {
		return this.canAddAll(inventory.getContents());
	}

	/**
	 * Checks if all of the items can be added to this Inventory
	 * 
	 * @param items to try to add
	 * @return whether addition is possible
	 */
	public boolean canAddAll(ItemStack[] items) {
		Inventory inv = this.clone();
		for (ItemStack item : items) {
			if (!inv.addItem(item)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Clones this Inventory and all of it's contents and hidden slots
	 * Note that viewers are not cloned.
	 * 
	 * @return a clone of this Inventory
	 */
	@Override
	public Inventory clone() {
		Inventory inv = new Inventory(this.getClonedContents());
		inv.hidden.addAll(this.hidden);
		return inv;
	}
}
