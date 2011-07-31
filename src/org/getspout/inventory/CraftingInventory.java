package org.getspout.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.inventory.ItemStack;

public interface CraftingInventory extends ContribInventory{
	/**
	 * Get's the item in the result slot of the crafting table
	 * @return result
	 */
	public ItemStack getResult();
	/**
	 * Get's the matrix of items in the crafting table
	 * @return matrix of items
	 */
	public ItemStack[] getMatrix();
	/**
	 * Set's the result item in the result slot of the crafting table
	 * @param newResult to set
	 */
	public void setResult(ItemStack newResult);
	/**
	 * Set's the matrix of items in the crafting table
	 * @param contents to set
	 */
	public void setMatrix(ItemStack[] contents);

	public IInventory getResultHandle();
	public IInventory getMatrixHandle();
}
