package org.bukkitcontrib.inventory;

import org.bukkit.inventory.Inventory;

import net.minecraft.server.IInventory;

public interface ContribInventory extends Inventory{

    /**
     * Get's the IInventory wrapped by this Bukkit Inventory
     * @return handle
     */
    public IInventory getHandle();

    //**
    // * Set's the name for this inventory
    // * @param name to set this inventory to, or null to reset it back to the default name
    // */
    //public void setName(String title);
}
