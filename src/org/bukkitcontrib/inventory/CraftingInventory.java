package org.bukkitcontrib.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.inventory.ItemStack;

public interface CraftingInventory extends ContribInventory{
    ItemStack getResult();
    ItemStack[] getMatrix();
    void setResult(ItemStack newResult);
    void setMatrix(ItemStack[] contents);
    public IInventory getResultHandle();
    public IInventory getMatrixHandle();
}
