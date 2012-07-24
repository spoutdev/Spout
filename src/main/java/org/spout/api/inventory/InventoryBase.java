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
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spout.api.inventory.special.InventoryRange;
import org.spout.api.inventory.special.InventorySlot;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.util.SoftReferenceIterator;

/**
 * Represents a basic inventory, other inventories can extend to supply custom get and set item routines.<br>
 * It supplies the needed utility functions, current item and inventory viewer support.
 */
public abstract class InventoryBase implements Serializable, Iterable<ItemStack> {

	private static final long serialVersionUID = 0L;

	private final List<InventoryViewer> viewers = new ArrayList<InventoryViewer>();
	private final List<SoftReference<InventoryBase>> inventoryViewers = new ArrayList<SoftReference<InventoryBase>>();
	private boolean notify = true;

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

	/**
	 * Gets the iterator to traverse the inventory viewers of this Inventory
	 * @return the Inventory viewer iterator
	 */
	protected Iterator<InventoryBase> getInventoryIterator() {
		return new SoftReferenceIterator<InventoryBase>(this.inventoryViewers);
	}

	/**
	 * Notifies all viewers of a certain change of an item
	 * @param slot index of the item
	 * @param item it got set to, or Null if made empty
	 */
	public void notifyViewers(int slot, ItemStack item) {
		for (InventoryViewer viewer : this.getViewers()) {
			viewer.onSlotSet(this, slot, item);
		}
		Iterator<InventoryBase> iter = this.getInventoryIterator();
		while (iter.hasNext()) {
			iter.next().onParentUpdate(this, slot, item);
		}
	}

	/**
	 * Notifies all viewers of items in this inventory
	 * @param items of this inventory to notify
	 */
	public void notifyViewers(ItemStack[] items) {
		for (int i = 0; i < items.length; i++) {
			for (InventoryViewer viewer : this.getViewers()) {
				viewer.onSlotSet(this, i, items[i]);
			}
			Iterator<InventoryBase> iter = this.getInventoryIterator();
			while (iter.hasNext()) {
				iter.next().onParentUpdate(this, i, items[i]);
			}
		}
	}

	/**
	 * Adds a new Inventory Viewer to this Inventory<br>
	 * Should only be called from within another Inventory constructor<br>
	 * The Inventory is stored in a Soft Reference, so no need to remove it as viewer
	 * 
	 * @param inventory to notify
	 */
	public void addInventoryViewer(InventoryBase inventory) {
		this.inventoryViewers.add(new SoftReference<InventoryBase>(inventory));
	}

	/**
	 * Called when one of the parent inventories of this Inventory is updated<br>
	 * Should only be overridden and used in InventoryBase extensions.
	 * 
	 * @param inventory that got updated
	 * @param slot of the item in the Inventory
	 * @param item that got updated
	 */
	public void onParentUpdate(InventoryBase inventory, int slot, ItemStack item) {}

	/**
	 * Notifies all viewers of all the current items in this inventory
	 */
	public void notifyViewers() {
		this.notifyViewers(this.getContents());
	}

	/**
	 * Gets whether this inventory sends notifications to viewers when items are set
	 * @return True if it sends notifications, False if not
	 */
	public boolean getNotifyViewers() {
		return this.notify;
	}

	/**
	 * Sets whether this inventory sends notifications to viewers when items are set
	 * @param notify
	 */
	public void setNotifyViewers(boolean notify) {
		this.notify = notify;
	}

	/**
	 * Creates a new Inventory Slot pointing to a certain item in this Inventory
	 * @param slot index of the item
	 * @return Inventory Slot pointing to the item
	 */
	public InventorySlot createSlot(int slot) {
		return new InventorySlot(this, slot);
	}

	/**
	 * Creates a new Inventory Range pointing to certain items in this Inventory
	 * @param offset slot index of the range
	 * @param size of the range
	 * @return Inventory Range pointing to the items
	 */
	public InventoryRange createRange(int offset, int size) {
		return new InventoryRange(this, offset, size);
	}

	/**
	 * Checks if the slot index given is contained in this inventory, 
	 * and throws an {@link IndexOutOfBoundsException} if this is not the case.
	 * 
	 * @param slot index to check
	 */
	public void checkSlotRange(int slot) {
		if (slot < 0 || slot >= this.getSize()) {
			throw new IndexOutOfBoundsException("Slot index is out of range");
		}
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new InventoryIterator(this);
	}

	/**
	 * Gets the contents of this Inventory<br>
	 * Note that the items still reference back into this Inventory
	 * 
	 * @return the contents
	 */
	public abstract ItemStack[] getContents();

	/**
	 * Sets the contents of this inventory<br>
	 * Note that the contents still reference back to the items
	 * 
	 * @param contents to put in
	 */
	public abstract void setContents(ItemStack[] contents);

	/**
	 * Gets the contents of this Inventory<br>
	 * The Item Stacks no longer reference back in this Inventory
	 * 
	 * @return the cloned contents
	 */
	public ItemStack[] getClonedContents() {
		ItemStack[] cloned = new ItemStack[this.getSize()];
		ItemStack content;
		for (int i = 0; i < cloned.length; i++) {
			content = this.getItem(i);
			cloned[i] = content == null ? null : content.clone();
		}
		return cloned;
	}

	/**
	 * Gets the item at a given slot index
	 * 
	 * @param slot index to get at
	 * @return the item at the index, or null if there is no item
	 */
	public abstract ItemStack getItem(int slot);

	/**
	 * Sets the item at a given slot index<br>
	 * The item is cloned before adding
	 * 
	 * @param slot index to set at
	 * @param item to set to
	 */
	public abstract void setItem(int slot, ItemStack item);

	/**
	 * Adds a given amount to the data of the item at the slot index<br><br>
	 * 
	 * If the data becomes negative or exceeds the maximum the item is removed and False is returned<br>
	 * Otherwise True is returned.
	 * @param slot index of the item
	 * @param amount of data to add
	 * @return True if the item data was successfully added
	 */
	public boolean addItemData(int slot, int amount) {
		ItemStack item = this.getItem(slot);
		if (item != null) {
			short newdata = (short) (item.getData() + amount);
			if (newdata <= 0 || newdata > item.getMaxData()) {
				this.setItem(slot, null);
			} else {
				this.setItem(slot, item.clone().setData(newdata));
				return true;
			}
		}
		return false;
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
		}

		return item.getMaterial().equals(material.getMaterial()) && item.getData() == material.getData();
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
		if (!toFirstOpenSlot) {
			return this.addItem(item, true, true);
		}

		ItemStack content;
		for (int i = 0; i < this.getSize(); ++i) {
			content = this.getItem(i);
			if (content == null || content.isEmpty()) {
				this.setItem(i, item.limitStackSize());
			} else if (content.equalsIgnoreSize(item)) {
				content.stack(item);
				this.setItem(i, content);
			} else {
				continue;
			}
			if (item.isEmpty()) {
				return true;
			}
		}
		return false;
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
		}
		ItemStack content;
		for (int i = 0; i < this.getSize(); ++i) {
			content = this.getItem(i);
			if (stackItem) {
				if (content == null || !content.equalsIgnoreSize(item)) {
					continue;
				}
				content.stack(item);
				this.setItem(i, content);
			} else if (toEmptySlot) {
				if (content != null && !content.isEmpty()) {
					continue;
				}
				this.setItem(i, item.limitStackSize());
			}
			if (item.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if item fits into inventory fully.
	 * 
	 * @param item to add
	 * @param stackItem whether to stack the item to other items
	 * @param toEmptySlot whether to add the item to empty slots
	 * @return True if item can fully fit, false if it can't
	 */
	public boolean canItemFit(ItemStack item, boolean stackItem, boolean toEmptySlot) {
		if (stackItem && toEmptySlot) {
			return this.canItemFit(item, true, false) || this.canItemFit(item, false, true);
		}
		ItemStack content;
		for (int i = 0; i < this.getSize(); ++i) {
			content = this.getItem(i);
			if (stackItem) {
				if (content == null || !content.equalsIgnoreSize(item)) {
					continue;
				}
				content.stack(item);
			} else if (toEmptySlot) {
				if (content != null && !content.isEmpty()) {
					continue;
				}
				return true;
			}
			if (item.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the size of this Inventory
	 * 
	 * @return the size of this Inventory
	 */
	public abstract int getSize();

	/**
	 * Gets the amount of the item that can be added to this Inventory.
	 * 
	 * @param item to compare with
	 * @return the amount of the item that can be added
	 */
	public int getAddableAmount(ItemStack item) {
		int amount = 0;
		int maxsize = item.getMaxStackSize();
		ItemStack content;
		for (int i = 0; i < this.getSize(); i++) {
			content = this.getItem(i);
			if (content == null) {
				amount += maxsize;
			} else if (content.equalsIgnoreSize(item)) {
				amount += maxsize - content.getAmount();
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
		ItemStack content;
		for (int i = 0; i < this.getSize(); i++) {
			content = this.getItem(i);
			if (content != null && content.equalsIgnoreSize(item)) {
				amount += content.getAmount();
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
		ItemStack content;
		for (int i = 0; i < this.getSize(); i++) {
			content = this.getItem(i);
			if (content != null && content.equals(item)) {
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
	 * Called whenever one Item changed in this Inventory<br>
	 * This is called after the contents have already changed
	 * 
	 * @param slot of the item
	 * @param item to set to
	 */
	public void onSlotChanged(int slot, ItemStack item) {}
}
