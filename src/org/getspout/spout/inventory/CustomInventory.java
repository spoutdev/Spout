package org.getspout.spout.inventory;

import java.util.Collection;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomInventory extends SpoutCraftInventory implements Inventory{
	public CustomInventory(ItemStack[] items, String name) {
		super(new CustomMCInventory(items, name));
	}
	
	public CustomInventory(Collection<ItemStack> items, String name) {
		super(new CustomMCInventory(items, name));
	}
	
	public CustomInventory(int size, String name) {
		super(new CustomMCInventory(size, name));
	}
	
	public CustomMCInventory getHandle() {
		return (CustomMCInventory)getInventory();
	}
	
	public String getName() {
		return this.getHandle().getName();
	}
	
	public void setName(String name) {
		this.getHandle().setName(name);
	}
}
