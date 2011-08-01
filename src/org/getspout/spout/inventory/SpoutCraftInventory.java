package org.getspout.spout.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.craftbukkit.inventory.CraftInventory;

public class SpoutCraftInventory extends CraftInventory{
	protected String name = null;
	public SpoutCraftInventory(IInventory inventory) {
		super(inventory);
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
