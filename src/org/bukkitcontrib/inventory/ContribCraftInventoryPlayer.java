package org.bukkitcontrib.inventory;

import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.inventory.ItemStack;

public class ContribCraftInventoryPlayer extends CraftInventoryPlayer implements ContribInventory, CraftingInventory{
    protected CraftingInventory crafting;
    public ContribCraftInventoryPlayer(InventoryPlayer inventory, CraftingInventory crafting) {
        super(inventory);
        this.crafting = crafting;
    }

    public InventoryPlayer getHandle() {
        return (InventoryPlayer)this.inventory;
    }

    public IInventory getMatrixHandle() {
        return this.crafting.getHandle();
    }
    
    public IInventory getResultHandle() {
        return this.crafting.getResultHandle();
    }

    @Override
    public ItemStack getResult() {
        return crafting.getResult();
    }

    @Override
    public ItemStack[] getMatrix() {
        return crafting.getMatrix();
    }

    @Override
    public void setResult(ItemStack newResult) {
        crafting.setResult(newResult);
    }

    @Override
    public void setMatrix(ItemStack[] contents) {
        crafting.setMatrix(contents);
    }
}
