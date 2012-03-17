/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inventory, usually owned by an entity. In a grid-style
 * inventory, slot ordering starts in the lower-left corner at zero, going
 * left-to-right for each row.
 */
public class Inventory implements Serializable {

	private static final long serialVersionUID = 0L;
	private final ItemStack[] contents;
	private TIntSet hidden = new TIntHashSet();
	private final List<InventoryViewer> viewers = new ArrayList<InventoryViewer>();
	private int currentSlot;

	public Inventory(int size) {
		contents = new ItemStack[size];
		currentSlot = 0;
	}

	public void setHiddenSlot(int slot, boolean add) {
		if (add) {
			hidden.add(slot);
		} else {
			hidden.remove(slot);
		}
	}

	public boolean isHiddenSlot(int slot) {
		return hidden.contains(slot);
	}

	public boolean addViewer(InventoryViewer viewer) {
		if (viewers.contains(viewer)) {
			return false;
		}
		viewers.add(viewer);
		viewer.updateAll(this, contents);
		return true;
	}

	public boolean removeViewer(InventoryViewer viewer) {
		return viewers.remove(viewer);
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public ItemStack getItem(int slot) {
		if (contents[slot] == null) {
			return null;
		} else {
			return contents[slot].clone();
		}
	}

	public void setItem(ItemStack item, int slot) {
		contents[slot] = item == null || item.getAmount() == 0 ? null : item.clone();
		for (InventoryViewer viewer : viewers) {
			viewer.onSlotSet(this, slot, contents[slot]);
		}
	}

	public boolean addItem(ItemStack item) {
		for (int i = 0; i < contents.length; ++i) {
			if (hidden.contains(i)) {
				continue;
			}

			if (contents[i] == null) {
				setItem(item, i);
				return true;
			} else if (contents[i].equalsIgnoreSize(item)) {
				boolean full = false;
				final int combinedSize = contents[i].getAmount() + item.getAmount();
				final int maxSize = item.getMaterial().getMaxStackSize();
				if (combinedSize <= maxSize) {
					contents[i].setAmount(combinedSize);
					full = true;
				} else {
					contents[i].setAmount(maxSize);
					item.setAmount(combinedSize - maxSize);
				}

				for (InventoryViewer viewer : viewers) {
					viewer.onSlotSet(this, i, contents[i]);
				}

				if (full) {
					return true;
				}
			}
		}
		return false;
	}

	public int getSize() {
		return contents.length;
	}

	public ItemStack getCurrentItem() {
		return getItem(currentSlot);
	}

	public int getCurrentSlot() {
		return currentSlot;
	}

	public void setCurrentSlot(int slot) {
		if (slot < 0 || slot >= contents.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		currentSlot = slot;
	}

	public boolean containsExactly(ItemStack item) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].equals(item)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(ItemStack item) {
		if (containsExactly(item)) {
			return true;
		}
		int neededAmount = item.getAmount();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].equalsIgnoreSize(item)) {
				neededAmount -= contents[i].getAmount();
				if (neededAmount <= 0) {
					return true;
				}
			}
		}
		return false;
	}
}
