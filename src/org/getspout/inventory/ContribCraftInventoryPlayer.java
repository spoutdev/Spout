package org.getspout.inventory;

import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.inventory.ItemStack;

public class ContribCraftInventoryPlayer extends CraftInventoryPlayer implements ContribPlayerInventory{
	protected CraftingInventory crafting;
	protected String name = null;
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

	public ItemStack getResult() {
		return crafting.getResult();
	}

	public ItemStack[] getMatrix() {
		return crafting.getMatrix();
	}

	public void setResult(ItemStack newResult) {
		crafting.setResult(newResult);
	}

	public void setMatrix(ItemStack[] contents) {
		crafting.setMatrix(contents);
	}
	
	public String getName() {
		if (name == null) {
			return this.inventory.getName();
		}
		return name;
	}
	
	public void setName(String title) {
		this.name = title;
	}
	
	public int getItemInHandSlot() {
		return this.getHandle().itemInHandIndex;
	}
}
