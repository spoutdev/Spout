package org.bukkitcontrib.inventory;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class ContribCraftItemStack extends CraftItemStack{

	public ContribCraftItemStack(int type, int amount, short damage) {
		super(type, amount, damage);
	}
	
	public net.minecraft.server.ItemStack getHandle() {
		return this.item;
	}
	
	public static ContribCraftItemStack fromItemStack(net.minecraft.server.ItemStack item) {
		if (item == null) return null;
		return new ContribCraftItemStack(item.id, item.count, (short) item.damage);
	}
	
	public static ContribCraftItemStack getContribCraftItemStack(org.bukkit.inventory.ItemStack item) {
		if (item == null) return null;
		if (item instanceof ContribCraftItemStack) {
			return (ContribCraftItemStack)item;
		}
		return new ContribCraftItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
	}

}
