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
import org.spout.api.inventory.ItemStack;

/**
 * Points to a range of items in another inventory
 */
public class InventoryRange extends InventoryBase {

	private static final long serialVersionUID = 1L;

	private final int size;
	private final int offset;
	private final InventoryBase parent;

	public InventoryRange(InventoryBase parent, int offset, int size) {
		this.size = size;
		this.parent = parent;
		this.offset = offset;
		this.parent.addInventoryViewer(this);
	}

	@Override
	public ItemStack[] getContents() {
		ItemStack[] contents = new ItemStack[this.getSize()];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = this.getItem(i);
		}
		return contents;
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot < 0 || slot >= this.getSize()) {
			throw new IllegalArgumentException("Slot is out of range");
		}
		return this.parent.getItem(slot + this.offset);
	}

	public InventoryBase getParent() {
		return this.parent;
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		if (slot < 0 || slot >= this.getSize()) {
			throw new IllegalArgumentException("Slot is out of range");
		}
		this.parent.setItem(slot + this.offset, item);
	}

	@Override
	public int getSize() {
		return this.size;
	}

	/**
	 * Gets the offset index relative to this parent
	 * 
	 * @return the offset index of this range relative to this parent
	 */
	public int getOffset() {
		return this.offset;
	}

	@Override
	public void onParentUpdate(InventoryBase inventory, int slot, ItemStack item) {
		if (inventory == this.parent && this.getNotifyViewers()) {
			slot -= this.offset;
			if (slot >= 0 && slot < this.getSize()) {
				this.notifyViewers(slot, item);
			}
		}
	}

	@Override
	public void onParentUpdate(InventoryBase inventory, ItemStack[] slots) {
		if (this.getNotifyViewers()) {
			this.notifyViewers(this.getContents());
		}
	}

	@Override
	public void setContents(ItemStack[] contents) {
		for (int i = 0; i < this.getSize(); i++) {
			this.setItem(i, contents[i]);
		}
	}
}
