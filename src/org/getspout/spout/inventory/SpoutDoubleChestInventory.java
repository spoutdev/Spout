package org.getspout.spout.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.block.Block;
import org.getspout.spoutapi.inventory.DoubleChestInventory;

public class SpoutDoubleChestInventory extends SpoutCraftInventory implements DoubleChestInventory{
	protected Block top;
	protected Block bottom;
	public SpoutDoubleChestInventory(IInventory inventory, Block top, Block bottom) {
		super(inventory);
		this.top = top;
		this.bottom = bottom;
	}

	@Override
	public Block getTopHalf() {
		return top;
	}

	@Override
	public Block getBottomHalf() {
		return bottom;
	}

}
