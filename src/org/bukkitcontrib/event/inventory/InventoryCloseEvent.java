package org.bukkitcontrib.event.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryCloseEvent extends InventoryEvent {
    private static final long serialVersionUID = 36124458220245924L;
    
    public InventoryCloseEvent(Player player, Inventory inventory) {
        super("InventoryCloseEvent", player, inventory);
    }
}
