package org.bukkitcontrib.event.inventory;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryOpenEvent extends InventoryEvent {
    private Inventory other;
    private static final long serialVersionUID = -5638814678689580398L;

    public InventoryOpenEvent(Player player, Inventory inventory, Inventory other) {
        super("InventoryOpenEvent", player, inventory);
        this.other = other;
    }

    public InventoryOpenEvent(Player player, Inventory inventory, Inventory other, Location location) {
        super("InventoryOpenEvent", player, inventory, location);
        this.other = other;
    }
    
    /**
     * Get's the top (or main) inventory that was opened
     * @return inventory opened
     */
    public Inventory getInventory() {
        return this.inventory;
    }
    
    /**
     * Get's the second, bottom inventory that was opened. 
     * @return bottom inventory opened or null if there was no second inventory opened
     */
    public Inventory getBottomInventory() {
        return this.other;
    }
}
