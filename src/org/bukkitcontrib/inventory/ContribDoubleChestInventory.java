package org.bukkitcontrib.inventory;

import net.minecraft.server.IInventory;

import org.bukkit.block.Block;

public class ContribDoubleChestInventory extends ContribCraftInventory implements DoubleChestInventory{
    protected Block left;
    protected Block right;
    public ContribDoubleChestInventory(IInventory inventory, Block left, Block right) {
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
