package org.getspout.server.inventory;


/**
 * Represents an entity which can view an inventory.
 */
public interface InventoryViewer {
	/**
	 * Inform the viewer that an item has changed.
	 * @param inventory The SpoutInventory in which a slot has changed.
	 * @param slot The slot number which has changed.
	 * @param item The ItemStack which the slot has changed to.
	 */
	public void onSlotSet(SpoutInventory inventory, int slot, SpoutItemStack item);
}
