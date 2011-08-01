package org.getspout.spout.inventory;

import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutPlayerInventory;

public class SpoutCraftInventoryPlayer extends CraftInventoryPlayer implements SpoutPlayerInventory{
	protected SpoutCraftingInventory crafting;
	protected String name = null;
	public SpoutCraftInventoryPlayer(InventoryPlayer inventory, SpoutCraftingInventory crafting) {
		super(inventory);
		this.crafting = crafting;
	}

	public InventoryPlayer getHandle() {
		return (InventoryPlayer)this.inventory;
	}

	public IInventory getMatrixHandle() {
		return this.crafting.getInventory();
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
