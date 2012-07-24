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

/**
 * Represents an inventory, usually owned by an entity. In a grid-style
 * inventory, slot ordering starts in the lower-left corner at zero, going
 * left-to-right for each row.
 */
public class Inventory extends InventoryBase {

	private static final long serialVersionUID = 0L;
	private final ItemStack[] contents;

	public Inventory(int size) {
		this(new ItemStack[size]);
	}

	public Inventory(ItemStack[] contents) {
		this.contents = contents;
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
		this.checkSlotRange(slot);
		if (contents[slot] == null) {
			return null;
		}

		return contents[slot].clone();
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		this.checkSlotRange(slot);
		contents[slot] = item == null || item.getAmount() == 0 ? null : item.clone();
		if (this.getNotifyViewers()) {
			this.notifyViewers(slot, item);
		}
		this.onSlotChanged(slot, item);
	}

	@Override
	public void setContents(ItemStack[] contents) {
		for (int i = 0; i < this.contents.length; i++) {
			this.contents[i] = contents[i];
			this.onSlotChanged(i, contents[i]);
		}
		if (this.getNotifyViewers()) {
			this.notifyViewers();
		}
	}
}
