package org.bukkitcontrib.event.inventory;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryCloseEvent extends InventoryEvent {
	private Inventory other;
	private static final long serialVersionUID = 36124458220245924L;
	
	public InventoryCloseEvent(Player player, Inventory inventory, Inventory other) {
		super("InventoryCloseEvent", player, inventory);
		this.other = other;
	}
	
	public InventoryCloseEvent(Player player, Inventory inventory, Inventory other, Location location) {
		super("InventoryCloseEvent", player, inventory, location);
		this.other = other;
	}
	
	/**
	 * Get's the top (or main) inventory that was closed
	 * @return inventory closed
	 */
	public Inventory getInventory() {
		return this.inventory;
	}
	
	/**
	 * Get's the second, bottom inventory that was closed. 
	 * @return bottom inventory closed or null if there was no second inventory closed
	 */
	public Inventory getBottomInventory() {
		return this.other;
	}
}
