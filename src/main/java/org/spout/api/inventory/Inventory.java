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

import org.spout.api.material.source.MaterialSource;

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
		this(new ItemStack[size]);
	}

	public Inventory(ItemStack[] contents) {
		this.contents = contents;
		this.currentSlot = 0;
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
	 * Gets the contents of this Inventory<br>
	 * Note that the items still reference back into this Inventory
	 * 
	 * @return the contents
	 */
	public ItemStack[] getContents() {
		return contents;
	}

	/**
	 * Gets the contents of this Inventory<br>
	 * The Item Stacks no longer reference back in this Inventory
	 * 
	 * @return the cloned contents
	 */
	public ItemStack[] getClonedContents() {
		ItemStack[] cloned = new ItemStack[this.contents.length];
		for (int i = 0; i < cloned.length; i++) {
			cloned[i] = this.contents[i] == null ? null : this.contents[i].clone();
		}
		return cloned;
	}

	/**
	 * Gets the item at a given slot index
	 * 
	 * @param slot index to get at
	 * @return the item at the index, or null if there is no item
	 */
	public ItemStack getItem(int slot) {
		if (contents[slot] == null) {
			return null;
		} else {
			return contents[slot].clone();
		}
	}

	/**
	 * Sets the item at a given slot index<br>
	 * The item is cloned before adding
	 * 
	 * @param slot index to set at
	 * @param item to set to
	 */
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
	 * Checks if the item at the slot given matches the material
	 * @param slot of the item
	 * @param material to compare with
	 * @return True if the item matches the material or both are null
	 */
	public boolean isItem(int slot, MaterialSource material) {
		ItemStack item = this.getItem(slot);
		if (item == null) {
			return material == null;
		} else {
			return item.getMaterial().equals(material.getMaterial()) && item.getData() == material.getData();
		}
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
	 * Adds a certain amount of the item at the slot given<br>
	 * You can add a negative amount to subtract
	 * @param slot of the item
	 * @param amount to add
	 * @return True if successful, which means the item was not null and could add the amount
	 */
	public boolean addItemAmount(int slot, int amount) {
		ItemStack item = this.getItem(slot);
		if (item != null) {
			int newamount = item.getAmount() + amount;
			if (newamount == 0) {
				this.setItem(slot, null);
				return true;
			} else if (newamount > 0 && newamount <= item.getMaxStackSize()) {
				this.setItem(slot, item.clone().setAmount(newamount));
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds an item to this Inventory<br>
	 * The input item amount will get affected<br>
	 * If True is returned, the input item amount is 0, else it is the amount that didn't get added.<br><br>
	 * 
	 * It will try to stack the item first, then it will fill empty slots<br>
	 * 
	 * @param item to add
	 * @return True if the addition was successful, False if not
	 */
	public boolean addItem(ItemStack item) {
		return this.addItem(item, false);
	}

	/**
	 * Adds an item to this Inventory<br>
	 * The input item amount will get affected<br>
	 * If True is returned, the input item amount is 0, else it is the amount that didn't get added.
	 * 
	 * @param item to add
	 * @param toFirstOpenSlot whether to add the item to the first available slot it finds
	 * @return True if the addition was successful, False if not
	 */
	public boolean addItem(ItemStack item, boolean toFirstOpenSlot) {
		if (toFirstOpenSlot) {
			for (int i = 0; i < contents.length; ++i) {
				if (hidden.contains(i)) {
					continue;
				}
				if (contents[i] == null || contents[i].isEmpty()) {
					contents[i] = item.limitStackSize();
				} else if (contents[i].equalsIgnoreSize(item)) {
					contents[i].stack(item);
				} else {
					continue;
				}
				for (InventoryViewer viewer : viewers) {
					viewer.onSlotSet(this, i, contents[i]);
				}
				if (item.isEmpty()) {
					return true;
				}
			}
			return false;
		} else {
			return this.addItem(item, true, true);
		}
	}

	/**
	 * Adds the item to this Inventory<br>
	 * The input item amount will get affected<br>
	 * If True is returned, the input item amount is 0, else it is the amount that didn't get added.
	 * 
	 * @param item to add
	 * @param stackItem whether to stack the item to other items
	 * @param toEmptySlot whether to add the item to empty slots
	 * @return True if the addition was successful, False if not
	 */
	public boolean addItem(ItemStack item, boolean stackItem, boolean toEmptySlot) {
		if (stackItem && toEmptySlot) {
			return this.addItem(item, true, false) || this.addItem(item, false, true);
		} else {
			for (int i = 0; i < contents.length; ++i) {
				if (hidden.contains(i)) {
					continue;
				}
				if (stackItem) {
					if (contents[i] == null || !contents[i].equalsIgnoreSize(item)) {
						continue;
					}
					contents[i].stack(item);
				} else if (toEmptySlot) {
					if (contents[i] != null && !contents[i].isEmpty()) {
						continue;
					}
					contents[i] = item.limitStackSize();
				}
				for (InventoryViewer viewer : viewers) {
					viewer.onSlotSet(this, i, contents[i]);
				}
				if (item.isEmpty()) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Gets the size of this Inventory
	 * 
	 * @return the size of this Inventory
	 */
	public int getSize() {
		return contents.length;
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
	 * Gets the amount of the item that can be added to this Inventory.
	 * 
	 * @param item to compare with
	 * @return the amount of the item that can be added
	 */
	public int getAddableAmount(ItemStack item) {
		int amount = 0;
		int maxsize = item.getMaxStackSize();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null) {
				amount += maxsize;
			} else if (contents[i].equalsIgnoreSize(item)) {
				amount += maxsize - contents[i].getAmount();
			}
		}
		return amount;
	}

	/**
	 * Gets the amount of the item contained in this Inventory<br>
	 * 
	 * @param item to compare to
	 * @return the amount of the item
	 */
	public int getItemAmount(ItemStack item) {
		int amount = 0;
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null && contents[i].equalsIgnoreSize(item)) {
				amount += contents[i].getAmount();
			}
		}
		return amount;
	}

	/**
	 * Checks if this precise item stack is contained in this Inventory.<br>
	 * All item stack properties have to match for this function to return True.
	 * 
	 * @param item to find
	 * @return whether it is contained
	 */
	public boolean containsExactly(ItemStack item) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null && contents[i].equals(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if this Inventory contains the item stack given<br>
	 * The item properties, except amount, are compared to the items in this Inventory<br>
	 * If the amount of the item stack or more is found, True is returned.
	 * 
	 * @param item to check
	 * @return True if it is contained, False if not
	 */
	public boolean contains(ItemStack item) {
		if (containsExactly(item)) {
			return true;
		}
		return this.getItemAmount(item) >= item.getAmount();
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
