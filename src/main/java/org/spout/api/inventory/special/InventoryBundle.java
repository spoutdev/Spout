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
package org.spout.api.inventory.special;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.spout.api.inventory.InventoryBase;
import org.spout.api.inventory.ItemStack;

/**
 * Represents a bundle of inventories treated as a whole
 */
public class InventoryBundle extends InventoryBase {

	private static final long serialVersionUID = 1L;

	private final List<InventoryBase> inventories;	
	private int size = 0;

	public InventoryBundle() {
		this.inventories = new ArrayList<InventoryBase>();
	}

	public InventoryBundle(InventoryBase... inventories) {
		this.inventories = Arrays.asList(inventories);
		for (InventoryBase inventory : this.inventories) {
			inventory.addInventoryViewer(this);
			this.size += inventory.getSize();
		}
	}

	@Override
	public ItemStack[] getContents() {
		ItemStack[] contents = new ItemStack[this.getSize()];
		int i = 0;
		for (InventoryBase inventory : this.inventories) {
			for (int j = 0; j < inventory.getSize(); j++) {
				contents[i++] = inventory.getItem(j);
			}
		}
		return contents;
	}

	@Override
	public ItemStack getItem(int slot) {
		for (InventoryBase inventory : inventories) {
			if (slot < inventory.getSize()) {
				return inventory.getItem(slot);
			}

			slot -= inventory.getSize();
		}

		throw new IllegalArgumentException("Slot is out of range");
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		for (InventoryBase inventory : inventories) {
			if (slot < inventory.getSize()) {
				inventory.setItem(slot, item);
				return;
			}

			slot -= inventory.getSize();
		}
		throw new IllegalArgumentException("Slot is out of range");
	}

	@Override
	public int getSize() {
		return this.size;
	}

	/**
	 * Removes an Inventory from this Bundle
	 * 
	 * @param inventory to remove
	 * @return True if removed, False if not
	 */
	public boolean removeInventory(InventoryBase inventory) {
		Iterator<InventoryBase> iter = this.inventories.iterator();
		boolean found = false;
		while (iter.hasNext()) {
			if (iter.next() == inventory) {
				iter.remove();
				this.size -= inventory.getSize();
				inventory.removeInventoryViewer(this);
				found = true;
			}
		}
		return found;
	}
	
	/**
	 * Adds a new Inventory to this Bundle
	 * 
	 * @param inventory to add
	 * @return the input Inventory
	 */
	public <T extends InventoryBase> T addInventory(T inventory) {
		this.inventories.add(inventory);
		this.size += inventory.getSize();
		inventory.addInventoryViewer(this);
		return inventory;
	}

	/**
	 * Gets all the inventories contained by this bundle
	 * @return a list of inventories
	 */
	public List<InventoryBase> getInventories() {
		return Collections.unmodifiableList(this.inventories);
	}

	@Override
	public void onParentSlotSet(InventoryBase inventory, int slot, ItemStack item) {
		for (InventoryBase inv : this.inventories) {
			if (inv == inventory) {
				this.notifyItemChange(slot);
				return;
			}
			slot += inv.getSize();
		}
	}

	/**
	 * Gets one of the sub-inventories contained in this bundle using a slot index<br>
	 * An {@link IndexOutOfBoundsException} is thrown if the slot is out of range
	 * 
	 * @param slot that is in the Inventory
	 * @return the Inventory at this slot
	 */
	public InventoryBase getInventory(int slot) {
		for (InventoryBase inventory : this.inventories) {
			if (slot < 0) {
				break;
			} else {
				slot -= inventory.getSize();
				if (slot < 0) {
					return inventory;
				}
			}
		}
		throw new IndexOutOfBoundsException("Slot index is out of range");
	}
}
