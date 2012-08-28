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

import java.util.Iterator;

/**
 * The iterator used in the Inventories<br>
 * Remove will set the item to null and will not cause the Inventory to change size
 */
public class InventoryIterator implements Iterator<ItemStack> {

	private final InventoryBase inventory;
	private int index;

	public InventoryIterator(InventoryBase inventory) {
		this.inventory = inventory;
		this.index = 0;
	}

	@Override
	public boolean hasNext() {
		return this.index < this.inventory.getSize();
	}

	@Override
	public ItemStack next() {
		return this.inventory.getItem(this.index++);
	}

	@Override
	public void remove() {
		this.inventory.setItem(this.index, null);
	}
}
