package org.getspout.spout.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
	
	@Override
	public Block getLeftSide() {
		if ((this.getDirection() == BlockFace.WEST) || (this.getDirection() == BlockFace.NORTH)) {
			return top;
		} else {
			return bottom;
		}
	}

	@Override
	public Block getRightSide() {
		if (this.getLeftSide().equals(top)) {
			return bottom;
		} else {
			return top;
		}
	}
	
	@Override
	public BlockFace getDirection() {
		if (top.getLocation().getBlockX() == bottom.getLocation().getBlockX()) {
			return this.isReversed(BlockFace.SOUTH) ? BlockFace.NORTH : BlockFace.SOUTH;
		} else {
			return this.isReversed(BlockFace.WEST) ? BlockFace.EAST : BlockFace.WEST;
		}
	}

	private boolean isReversed(BlockFace primary) {
		BlockFace secondary = primary.getOppositeFace();	
		if (isSolid(top.getRelative(secondary)) || isSolid(bottom.getRelative(secondary))) {
			return false;
		} else {
			return isSolid(top.getRelative(primary)) || isSolid(bottom.getRelative(primary));
		}
	}
	
	private static boolean isSolid(Block block) {
		// o[]: If block type is completely solid.
		// This should really be part of Spout or Bukkit, but for now it's here.
		return net.minecraft.server.Block.o[block.getTypeId()];  
	}

}
