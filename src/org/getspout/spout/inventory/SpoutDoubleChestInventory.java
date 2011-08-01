package org.getspout.spout.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.block.Block;
import org.getspout.spoutapi.inventory.DoubleChestInventory;

public class SpoutDoubleChestInventory extends SpoutCraftInventory implements DoubleChestInventory{
	protected Block left;
	protected Block right;
	public SpoutDoubleChestInventory(IInventory inventory, Block left, Block right) {
		super(inventory);
		this.left = left;
		this.right = right;
	}

	@Override
	public Block getLeftSide() {
		return left;
	}

	@Override
	public Block getRightSide() {
		return right;
	}

}
