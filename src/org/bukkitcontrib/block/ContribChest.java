package org.bukkitcontrib.block;

import org.bukkit.block.Chest;
import org.bukkitcontrib.inventory.ContribInventory;
import org.bukkitcontrib.inventory.DoubleChestInventory;

public interface ContribChest extends Chest{
	
	/**
	 * Is true if the chest is part of a larger double chest
	 * @return
	 */
	public boolean isDoubleChest();
	
	/**
	 * Gets the other side of the larger double chest, if a double chest, or null if it is a single chest
	 * @return other chest
	 */
	public ContribChest getOtherSide();
	
	/**
	 * Gets the full double inventory of the double chest, or null if a single chest
	 * @return full inventory
	 */
	public DoubleChestInventory getFullInventory();
	
	/**
	 * Gets the inventory for this single chest block
	 * @return inventory
	 */
	public ContribInventory getInventory();

}
