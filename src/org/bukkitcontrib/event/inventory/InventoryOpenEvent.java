package org.bukkitcontrib.event.inventory;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryOpenEvent extends InventoryEvent {
    private static final long serialVersionUID = -5638814678689580398L;
    
    public InventoryOpenEvent(Player player, Inventory inventory) {
        super("InventoryOpenEvent", player, inventory);
    }
    
    public InventoryOpenEvent(Player player, Inventory inventory, Location location) {
        super("InventoryOpenEvent", player, inventory, location);
    }
}
