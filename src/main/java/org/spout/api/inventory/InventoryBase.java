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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spout.api.inventory.special.InventoryRange;
import org.spout.api.inventory.special.InventorySlot;
import org.spout.api.inventory.transfer.TransferMode;
import org.spout.api.inventory.transfer.TransferModes;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.util.SoftReferenceIterator;

/**
 * Represents a basic inventory, other inventories can extend to supply custom get and set item routines.<br>
 * It supplies the needed utility functions, current item and inventory viewer support.
 */
public abstract class InventoryBase implements Serializable, Iterable<ItemStack>, Cloneable {
	private static final long serialVersionUID = 0L;

	private final List<InventoryViewer> viewers = new ArrayList<InventoryViewer>();
	private final List<SoftReference<InventoryBase>> inventoryViewers = new ArrayList<SoftReference<InventoryBase>>();
	private boolean ignore = false;
	private final TIntHashSet dirtySlots = new TIntHashSet();

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
	 * Notifies all viewers and itself of a certain change of an item
	 * 
	 * @param slot index of the item
	 */
	protected void notifyItemChange(int slot) {
		if (ignore) {
			dirtySlots.add(slot);
			return;
		}
		ItemStack item = this.getItem(slot); //TODO: Item is not immutable, viewers can alter it!!!
		this.onSlotChanged(slot, item);
		for (InventoryViewer viewer : this.getViewers()) {
			viewer.onSlotSet(this, slot, item);
		}
		Iterator<InventoryBase> iter = this.getInventoryIterator();
		while (iter.hasNext()) {
			iter.next().onParentSlotSet(this, slot, item);
		}
	}

	/**
	 * Removes an Inventory viewer from this Inventory<br>
	 * Should only be called by another Inventory when it removes a child Inventory
	 * 
	 * @param inventory to remove
	 * @return True if removed, False if not
	 */
	public boolean removeInventoryViewer(InventoryBase inventory) {
		Iterator<InventoryBase> iter = this.getInventoryIterator();
		boolean found = false;
		while (iter.hasNext()) {
			if (iter.next() == inventory) {
				iter.remove();
				found = true;
			}
		}
		return found;
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
	 * Called when one of the parent inventories of this Inventory changes an Item<br>
	 * Should only be overridden and used in InventoryBase extensions.
	 * 
	 * @param inventory that got updated
	 * @param slot of the item in the Inventory
	 * @param item that got updated
	 */
	protected void onParentSlotSet(InventoryBase inventory, int slot, ItemStack item) {}

	/**
	 * Sets whether item changes are ignored<br>
	 * When ignored, (Inventory) viewers are not notified of item changes and onSlotSet is suppressed<br>
	 * When the ignore state goes from True to False, all pending item changes are flushed<br><br>
	 * 
	 * This method is used to delay item changes, to make sure all changes happen after all items have changed
	 * 
	 * @param ignore state to set to
	 * @return the old ignore state
	 */
	protected boolean setIgnoreChanges(boolean ignore) {
		boolean old = this.ignore;
		this.ignore = ignore;
		if (!this.ignore && !this.dirtySlots.isEmpty()) {
			for (TIntIterator iter = this.dirtySlots.iterator(); iter.hasNext(); this.notifyItemChange(iter.next()));
			this.dirtySlots.clear();
		}
		return old;
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
	 * 
	 * @param offset slot index of the range
	 * @param size of the range
	 * @return Inventory Range pointing to the items
	 */
	public InventoryRange createRange(int offset, int size) {
		return new InventoryRange(this, offset, size);
	}

	/**
	 * Gets a reversed range pointing to the items in this Inventory
	 * 
	 * @return reversed range
	 */
	public InventoryRange reverse() {
		return new InventoryRange(this, 0, this.getSize(), true);
	}

	/**
	 * Creates a new Inventory Range pointing to certain items in this Inventory
	 * 
	 * @param offset slot index of the range
	 * @param size of the range
	 * @param reversed state, True to reverse the range, False to keep the same order
	 * @return Inventory Range pointing to the items
	 */
	public InventoryRange createRange(int offset, int size, boolean reversed) {
		return new InventoryRange(this, offset, size, reversed);
	}
	
	/**
	 * Checks if the slot index given is contained in this inventory, 
	 * and throws an {@link IndexOutOfBoundsException} if this is not the case.
	 * 
	 * @param slot index to check
	 */
	protected void checkSlotRange(int slot) {
		if (slot < 0 || slot >= this.getSize()) {
			throw new IndexOutOfBoundsException("Slot index is out of range");
		}
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new InventoryIterator(this);
	}

	/**
	 * Gets the contents of this Inventory
	 * 
	 * @return the (cloned) contents
	 */
	public ItemStack[] getContents() {
		ItemStack[] cloned = new ItemStack[this.getSize()];
		for (int i = 0; i < cloned.length; i++) {
			cloned[i] = this.getItem(i);
		}
		return cloned;
	}

	/**
	 * Sets the contents of this inventory<br>
	 * The contents will not reference back after setting
	 * 
	 * @param contents to put in (will be cloned)
	 */
	public void setContents(ItemStack[] contents) {
		if (this.getSize() != contents.length) {
			throw new IllegalArgumentException("The contents length is not equal to the size of the Inventory");
		}
		boolean oldi = this.setIgnoreChanges(true);
		for (int i = 0; i < contents.length; i++) {
			this.setItem(i, contents[i]);
		}
		this.setIgnoreChanges(oldi);
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
	 * @return True if this resulted in an Item change, False if not
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
	 * Adds an item to this Inventory<br><br>
	 * Uses the default <b>merge</b> and <b>fill</b> operation
	 * 
	 * @param item to add
	 * @return the remainder of the item after adding the item, null when fully transferred
	 */
	public ItemStack addItem(ItemStack item) {
		return addItem(item, TransferModes.DEFAULT_TRANSFER_MODES);
	}

	/**
	 * Adds an item to this Inventory
	 * 
	 * @param item to add
	 * @param transferModes to use
	 * @return the remainder of the item after adding the item, null when fully transferred
	 */
	public ItemStack addItem(ItemStack item, TransferMode... transferModes) {
		for (TransferMode mode : transferModes) {
			if (item == null || item.isEmpty()) {
				break;
			}
			item = mode.transfer(item, this);
		}
		return item;
	}

	/**
	 * Adds an item to this Inventory<br><br>
	 * <b>The item is fully added, if this is not possible, the operation is cancelled</b><br><br>
	 * Uses the default <b>merge</b> and <b>fill</b> operation
	 * 
	 * @param item to add
	 * @return True if the addition was successful, False if not
	 */
	public boolean addItemFully(ItemStack item) {
		return addItemFully(item, TransferModes.DEFAULT_TRANSFER_MODES);
	}

	/**
	 * Adds an item to this Inventory<br><br>
	 * <b>The item is fully added, if this is not possible, the operation is cancelled</b>
	 * 
	 * @param item to add
	 * @param transferModes to use (if left empty, the default merging mode is used)
	 * @return True if the addition was successful, False if not
	 */
	public boolean addItemFully(ItemStack item, TransferMode... transferModes) {
		InventoryBase source = this.clone();
		if (source.addItem(item, transferModes) != null) {
			this.setContents(source.getContents());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified<br>
	 * Items are removed from this Inventory<br><br>
	 * 
	 * Uses the default <b>merge</b> and <b>fill</b> operation
	 * 
	 * @param target inventory
	 * @return True if all items were transferred, False if not
	 */
	public boolean transfer(InventoryBase target) {
		return transfer(target, TransferModes.DEFAULT_TRANSFER_MODES);
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified<br>
	 * Items are removed from this Inventory
	 * 
	 * @param target inventory
	 * @param transferModes to use
	 * @return True if all items were transferred, False if not
	 */
	public boolean transfer(InventoryBase target, TransferMode... transferModes) {
		return transfer(target, true, transferModes);
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified<br><br>
	 * 
	 * Uses the default <b>merge</b> and <b>fill</b> operation
	 * 
	 * @param target inventory
	 * @param remove True to remove items from this Inventory, False to keep them
	 * @return True if all items were transferred, False if not
	 */
	public boolean transfer(InventoryBase target, boolean remove) {
		return transfer(target, remove, TransferModes.DEFAULT_TRANSFER_MODES);
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified
	 * 
	 * @param target inventory
	 * @param remove True to remove items from this Inventory, False to keep them
	 * @param transferModes to use
	 * @return True if all items were transferred, False if not
	 */
	public boolean transfer(InventoryBase target, boolean remove, TransferMode... transferModes) {
		if (transferModes.length == 0) {
			transferModes = TransferModes.DEFAULT_TRANSFER_MODES;
		}
		ItemStack[] sourceItems = this.getContents();
		for (TransferMode mode : transferModes) {
			for (int i = 0; i < sourceItems.length; i++) {
				if (sourceItems[i] == null || sourceItems[i].isEmpty()) {
					continue;
				}
				sourceItems[i] = mode.transfer(sourceItems[i], target);
			}
		}
		if (remove) {
			this.setContents(sourceItems);
		}
		for (ItemStack item : sourceItems) {
			if (item != null && !item.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified<br>
	 * Items are removed from this Inventory<br><br>
	 * 
	 * <b>All items are transferred, if one item fails, the entire operation is cancelled</b><br><br>
	 * 
	 * Uses the default <b>merge</b> and <b>fill</b> operation
	 * 
	 * @param target inventory
	 * @return True if all items were transferred, False if not
	 */
	public boolean transferFully(InventoryBase target) {
		return transferFully(target, TransferModes.DEFAULT_TRANSFER_MODES);
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified<br>
	 * Items are removed from this Inventory<br><br>
	 * 
	 * <b>All items are transferred, if one item fails, the entire operation is cancelled</b>
	 * 
	 * @param target inventory
	 * @param transferModes to use
	 * @return True if all items were transferred, False if not
	 */
	public boolean transferFully(InventoryBase target, TransferMode... transferModes) {
		return transferFully(target, true, transferModes);
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified<br><br>
	 * 
	 * <b>All items are transferred, if one item fails, the entire operation is cancelled</b><br><br>
	 * 
	 * Uses the default <b>merge</b> and <b>fill</b> operation
	 * 
	 * @param target inventory
	 * @param remove True to remove items from this Inventory, False to keep them
	 * @return True if all items were transferred, False if not
	 */
	public boolean transferFully(InventoryBase target, boolean remove) {
		return transferFully(target, remove, TransferModes.DEFAULT_TRANSFER_MODES);
	}

	/**
	 * Performs all of the transfer operations between this Inventory and the target Inventory specified<br><br>
	 * 
	 * <b>All items are transferred, if one item fails, the entire operation is cancelled</b>
	 * 
	 * @param target inventory
	 * @param remove True to remove items from this Inventory, False to keep them
	 * @param transferModes to use
	 * @return True if all items were transferred, False if not
	 */
	public boolean transferFully(InventoryBase target, boolean remove, TransferMode... transferModes) {
		InventoryBase cloneTarget = target.clone();
		InventoryBase cloneSource = this.clone();
		if (cloneSource.transfer(cloneTarget, transferModes)) {
			this.setContents(cloneSource.getContents());
			if (remove) {
				target.setContents(cloneTarget.getContents());
			}
			return true;
		} else {
			return false;
		}
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

	/**
	 * Tests if this Inventory has (non-null and non-empty) items
	 * 
	 * @return True if this Inventory is empty, False if not
	 */
	public boolean isEmpty() {
		for (ItemStack item : this) {
			if (item != null && !item.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets a clone Inventory containing the cloned contents of this Inventory
	 */
	@Override
	public Inventory clone() {
		return new Inventory(this.getContents());
	}
}
