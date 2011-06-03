package org.bukkitcontrib.inventory;

import org.bukkit.inventory.Inventory;

import net.minecraft.server.IInventory;

public interface ContribInventory extends Inventory{

    /**
     * Get's the IInventory wrapped by this Bukkit Inventory
     * @return handle
     */
    public IInventory getHandle();

}
