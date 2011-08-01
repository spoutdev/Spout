package org.getspout.spout.inventory;

import java.util.Collection;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.InventoryBuilder;

public class SpoutInventoryBuilder implements InventoryBuilder {

	@Override
	public Inventory construct(ItemStack[] items, String name) {
		return new CustomInventory(items, name);
	}

	@Override
	public Inventory construct(Collection<ItemStack> items, String name) {
		return new CustomInventory(items, name);
	}

	@Override
	public Inventory construct(int size, String name) {
		return new CustomInventory(size, name);
	}

}
