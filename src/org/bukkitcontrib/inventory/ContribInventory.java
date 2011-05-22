package org.bukkitcontrib.inventory;

import org.bukkit.inventory.Inventory;

import net.minecraft.server.IInventory;

public interface ContribInventory extends Inventory{
    
    public IInventory getHandle();

}
