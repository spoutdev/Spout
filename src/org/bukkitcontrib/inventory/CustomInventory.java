package org.bukkitcontrib.inventory;

import org.bukkit.inventory.ItemStack;

public class CustomInventory extends ContribCraftInventory implements ContribInventory{
	public CustomInventory(ItemStack[] items, String name) {
		super(new CustomMCInventory(items, name));
	}
	
	public CustomInventory(int size, String name) {
		super(new CustomMCInventory(size, name));
	}
}
