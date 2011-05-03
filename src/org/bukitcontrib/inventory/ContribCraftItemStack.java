package org.bukitcontrib.inventory;

import net.minecraft.server.ItemStack;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class ContribCraftItemStack extends CraftItemStack{

    public ContribCraftItemStack(int type, int amount, short damage) {
        super(type, amount, damage);
    }
    
    public ItemStack getHandle() {
        return this.item;
    }

}
