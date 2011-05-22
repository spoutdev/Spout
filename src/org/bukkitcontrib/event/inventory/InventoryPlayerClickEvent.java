package org.bukkitcontrib.event.inventory;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryPlayerClickEvent extends InventoryClickEvent{
    private static final long serialVersionUID = 9219553850827660981L;

    public InventoryPlayerClickEvent(Player player, Inventory inventory, InventorySlotType type, ItemStack item, ItemStack cursor, int slot, Location location) {
        super(player, inventory, type, item, cursor, slot, location);
    }
}
