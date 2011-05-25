package org.bukkitcontrib.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.inventory.ItemStack;

public interface CraftingInventory extends ContribInventory{
    /**
     * Get's the item in the result slot of the crafting table
     * @return result
     */
    ItemStack getResult();
    /**
     * Get's the matrix of items in the crafting table
     * @return matrix of items
     */
    ItemStack[] getMatrix();
    /**
     * Set's the result item in the result slot of the crafting table
     * @param newResult to set
     */
    void setResult(ItemStack newResult);
    /**
     * Set's the matrix of items in the crafting table
     * @param contents to set
     */
    void setMatrix(ItemStack[] contents);

    public IInventory getResultHandle();
    public IInventory getMatrixHandle();
}
