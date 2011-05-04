package org.bukkitcontrib.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.craftbukkit.inventory.CraftInventory;

public class ContribCraftInventory extends CraftInventory{
    public ContribCraftInventory(IInventory inventory) {
        super(inventory);
    }

    public IInventory getHandle() {
        return this.inventory;
    }
}
