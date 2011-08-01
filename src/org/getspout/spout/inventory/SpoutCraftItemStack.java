package org.getspout.spout.inventory;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class SpoutCraftItemStack extends CraftItemStack{

	public SpoutCraftItemStack(int type, int amount, short damage) {
		super(type, amount, damage);
	}
	
	public net.minecraft.server.ItemStack getHandle() {
		return this.item;
	}
	
	public static SpoutCraftItemStack fromItemStack(net.minecraft.server.ItemStack item) {
		if (item == null) return null;
		return new SpoutCraftItemStack(item.id, item.count, (short) item.damage);
	}
	
	public static SpoutCraftItemStack getContribCraftItemStack(org.bukkit.inventory.ItemStack item) {
		if (item == null) return null;
		if (item instanceof SpoutCraftItemStack) {
			return (SpoutCraftItemStack)item;
		}
		return new SpoutCraftItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
	}

}
