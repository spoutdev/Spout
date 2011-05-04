package org.bukkitcontrib.inventory;

import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;

public class ContribCraftInventoryPlayer extends CraftInventoryPlayer{

    public ContribCraftInventoryPlayer(InventoryPlayer inventory) {
        super(inventory);
    }

    public InventoryPlayer getHandle() {
        return (InventoryPlayer)this.inventory;
    }
}
