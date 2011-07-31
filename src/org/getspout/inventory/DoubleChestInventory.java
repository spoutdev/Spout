package org.getspout.inventory;

import org.bukkit.block.Block;

public interface DoubleChestInventory extends ContribInventory{
	/**
	 * Gets the left half of the double chest
	 * @return left side
	 */
	public Block getLeftSide();
	
	/**
	 * Gets the right half of the double chest
	 * @return right side
	 */
	public Block getRightSide();

}
