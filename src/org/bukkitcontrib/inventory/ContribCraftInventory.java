package org.bukkitcontrib.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.craftbukkit.inventory.CraftInventory;

public class ContribCraftInventory extends CraftInventory implements ContribInventory{
	protected String name = null;
	public ContribCraftInventory(IInventory inventory) {
		super(inventory);
	}

	public IInventory getHandle() {
		return this.inventory;
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
	
	
}
