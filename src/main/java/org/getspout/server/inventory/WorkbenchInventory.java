package org.getspout.server.inventory;

import org.getspout.server.window.WindowID;

public class WorkbenchInventory extends CraftingInventory {	
	/**
	 * Initialize the inventory
	 *
	 */
	public WorkbenchInventory() {
		super((byte) WindowID.WORKBENCH, 10);
	}

	@Override
	public int getResultSlot() {
		return 9;
	}

	@Override
	public String getName() {
		return "Crafting";
	}
	
	private final static int slotConversion[] = {
		1, 2, 3, 4, 5, 6, 7, 8, 9, 0
	};

	/**
	 * Get the network index from a slot index.
	 * @param itemSlot The index for use with getItem/setItem.
	 * @return The index modified for transfer over the network, or -1 if there is no equivalent.
	 */
	@Override
	public int getNetworkSlot(int itemSlot) {
		if (itemSlot > slotConversion.length) return -1;
		return slotConversion[itemSlot];
	}

	/**
	 * Get the slot index from a network index.
	 * @param networkSlot The index received over the network.
	 * @return The index modified for use with getItem/setItem, or -1 if there is no equivalent.
	 */
	@Override
	public int getItemSlot(int networkSlot) {
		for (int i = 0; i < slotConversion.length; ++i) {
			if (slotConversion[i] == networkSlot) return i;
		}
		return -1;
	}	
}
