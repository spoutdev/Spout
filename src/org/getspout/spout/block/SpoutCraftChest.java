package org.getspout.spout.block;

import java.lang.reflect.Field;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.TileEntityChest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.inventory.Inventory;
import org.getspout.spout.inventory.SpoutCraftInventory;
import org.getspout.spout.inventory.SpoutDoubleChestInventory;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.inventory.DoubleChestInventory;

public class SpoutCraftChest extends CraftChest implements SpoutChest{
	protected TileEntityChest chest;
	public SpoutCraftChest(Block block) {
		super(block);
		chest = getTileEntity();
	}

	@Override
	public boolean isDoubleChest() {
		return getOtherSide() != null;
	}
	
	public SpoutChest getOtherSide() {
		if (getBlock().getRelative(1, 0, 0).getType() == Material.CHEST) {
			BlockState bs = getBlock().getRelative(1, 0, 0).getState();
			if (bs instanceof SpoutChest) {
				return ((SpoutChest)bs);
			}
		}
		if (getBlock().getRelative(-1, 0, 0).getType() == Material.CHEST) {
			BlockState bs = getBlock().getRelative(-1, 0, 0).getState();
			if (bs instanceof SpoutChest) {
				return ((SpoutChest)bs);
			}
		}
		if (getBlock().getRelative(0, 0, 1).getType() == Material.CHEST) {
			BlockState bs = getBlock().getRelative(0, 0, 1).getState();
			if (bs instanceof SpoutChest) {
				return ((SpoutChest)bs);
			}
		}
		if (getBlock().getRelative(0, 0, -1).getType() == Material.CHEST) {
			BlockState bs = getBlock().getRelative(0, 0, -1).getState();
			if (bs instanceof SpoutChest) {
				return ((SpoutChest)bs);
			}
		}
		return null;
	}

	@Override
	public DoubleChestInventory getFullInventory() {
		if (isDoubleChest()){
			SpoutCraftChest other = (SpoutCraftChest)getOtherSide();
			return new SpoutDoubleChestInventory(new InventoryLargeChest("Double Chest", chest, other.chest), getBlock(), other.getBlock());
		}
		return null;
	}
	
	@Override
	public Inventory getLargestInventory() {
		if (isDoubleChest()){
			return getFullInventory();
		}
		return getInventory();
	}
	
	@Override
	public Inventory getInventory() {
		return new SpoutCraftInventory(chest);
	}
	
	public TileEntityChest getTileEntity() {
		try {
			Field chest = CraftChest.class.getDeclaredField("chest");
			chest.setAccessible(true);
			return (TileEntityChest) chest.get(this);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
