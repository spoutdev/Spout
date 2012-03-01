package org.spout.api.inventory;

/**
 * Represents something which can view an inventory.
 */
public interface InventoryViewer {
	
	/**
	 * Inform the viewer that an item has changed.
	 * @param inventory The {@link Inventory} in which a slot has changed.
	 * @param slot The slot number which has changed.
	 * @param item The {@link ItemStack} which the slot has changed to.
	 */
	public void onSlotSet(Inventory inventory, int slot, ItemStack item);

	/**
	 * Inform the viewer that all items have been changed
	 * @param inventory The inventory which contains the slots
	 * @param slots The ItemStacks in the slots which have changed.
	 */
	public void updateAll(Inventory inventory, ItemStack[] slots);
	
}
