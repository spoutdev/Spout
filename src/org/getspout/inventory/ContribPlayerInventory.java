package org.getspout.inventory;

import org.bukkit.inventory.PlayerInventory;

public interface ContribPlayerInventory extends PlayerInventory, CraftingInventory, ContribInventory{
	
	public int getItemInHandSlot();

}
