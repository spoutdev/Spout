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

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.InventoryBase;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.source.MaterialSource;

/**
 * Points to a single item in another Inventory<br>
 * Can be constructed to contain a single item.
 */
public class InventorySlot extends InventoryRange {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs this Inventory Slot to contain an empty 1-size Inventory
	 */
	public InventorySlot() {
		this(new Inventory(1), 0);
	}

	/**
	 * Constructs this Inventory Slot to contain a single item in a new Inventory
	 * @param item to point to
	 */
	public InventorySlot(ItemStack item) {
		this();
		this.setItem(item);
	}

	/**
	 * Constructs this Inventory Slot to point to an item in another inventory
	 * @param parent the item is in
	 * @param index the item is at
	 */
	public InventorySlot(InventoryBase parent, int index) {
		super(parent, index, 1);
	}

	/**
	 * Sets the item at this slot<br>
	 * The item is cloned before adding
	 * 
	 * @param item to set to
	 */
	public void setItem(ItemStack item) {
		this.setItem(0, item);
	}

	/**
	 * Gets the item at this slot
	 * 
	 * @return the item at this slot, or null if there is no item
	 */
	public ItemStack getItem() {
		return this.getItem(0);
	}

	/**
	 * Adds a given amount to the data of the item in this slot<br><br>
	 * 
	 * If the data becomes negative the item is removed and False is returned<br>
	 * Otherwise True is returned.
	 * @param amount of data to add
	 * @return True if the item data was successfully added
	 */
	public boolean addItemData(int amount) {
		return super.addItemData(0, amount);
	}

	/**
	 * Checks if the item at this slot matches the material
	 * @param slot of the item
	 * @param material to compare with
	 * @return True if the item matches the material or both are null
	 */
	public boolean isItem(MaterialSource material) {
		return super.isItem(0, material);
	}

	/**
	 * Adds a certain amount of the item in this slot<br>
	 * You can add a negative amount to subtract
	 * @param amount to add
	 * @return True if successful, which means the item was not null and could add the amount
	 */
	public boolean addItemAmount(int amount) {
		return super.addItemAmount(0, amount);
	}
}
