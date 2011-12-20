package org.getspout.server.inventory;

public class WorkbenchInventory extends SpoutInventory {

	/**
	 * Initialize the inventory
	 *
	 */
	protected WorkbenchInventory() {
		super((byte) 1, 9);
	}

	@Override
	public String getName() {
		return "Crafting";
	}
}
