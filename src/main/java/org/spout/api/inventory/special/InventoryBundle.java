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

import org.spout.api.inventory.InventoryBase;
import org.spout.api.inventory.InventoryViewer;
import org.spout.api.inventory.ItemStack;

/**
 * Represents a bundle of inventories treated as a whole
 */
public class InventoryBundle extends InventoryBase implements InventoryViewer {

	private static final long serialVersionUID = 1L;

	private final InventoryBase[] inventories;
	private final int size;

	public InventoryBundle(InventoryBase... inventories) {
		this.inventories = inventories;
		int size = 0;
		for (InventoryBase inventory : inventories) {
			size += inventory.getSize();
		}
		this.size = size;
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
			} else {
				slot -= inventory.getSize();
			}
		}
		throw new IllegalArgumentException("Slot is out of range");
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		for (InventoryBase inventory : inventories) {
			if (slot < inventory.getSize()) {
				inventory.setItem(slot, item);
				return;
			} else {
				slot -= inventory.getSize();
			}
		}
		throw new IllegalArgumentException("Slot is out of range");
	}

	@Override
	public int getSize() {
		return this.size;
	}

	/**
	 * Gets all the inventories contained by this bundle
	 * @return an array of inventories
	 */
	public InventoryBase[] getInventories() {
		return this.inventories;
	}

	/**
	 * Notifies this bundle that it has to stop watching all contained inventories for item changes<br>
	 * Consequently, all contained inventories lose this bundle as a viewer.
	 */
	public void stopWatching() {
		for (InventoryBase inventory : this.inventories) {
			inventory.removeViewer(this);
		}
	}

	/**
	 * Notifies this bundle that it has to start watching all contained inventories for item changes<br>
	 * Consequently, all contained inventories get this bundle added as a viewer.
	 */
	public void startWatching() {
		for (InventoryBase inventory : this.inventories) {
			inventory.addViewer(this);
		}
	}

	@Override
	public void onSlotSet(InventoryBase inventory, int slot, ItemStack item) {
		for (InventoryBase inv : this.inventories) {
			if (inv == inventory) {
				this.notifyViewers(slot, item);
				return;
			} else {
				slot += inv.getSize();
			}
		}
	}

	@Override
	public void updateAll(InventoryBase inventory, ItemStack[] slots) {
		this.notifyViewers(this.getContents());
	}
}
