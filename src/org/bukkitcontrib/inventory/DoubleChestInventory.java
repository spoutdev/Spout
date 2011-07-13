package org.bukkitcontrib.inventory;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

public interface DoubleChestInventory extends Inventory{
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
