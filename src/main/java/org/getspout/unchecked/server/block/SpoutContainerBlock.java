package org.getspout.unchecked.server.block;

import org.bukkit.block.ContainerBlock;

import org.getspout.unchecked.server.inventory.SpoutInventory;

public abstract class SpoutContainerBlock extends SpoutBlockState implements ContainerBlock {
	protected SpoutInventory inventory;

	public SpoutContainerBlock(SpoutBlock block, SpoutInventory inventory) {
		super(block);
		this.inventory = inventory;
	}

	@Override
	public void destroy() {
		//inventory.removeViewer();
	}

	@Override
	public SpoutInventory getInventory() {
		return inventory;
	}
}
