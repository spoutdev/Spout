package org.bukkitcontrib.block;

import java.lang.reflect.Field;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.TileEntityChest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkitcontrib.inventory.ContribCraftInventory;
import org.bukkitcontrib.inventory.ContribDoubleChestInventory;
import org.bukkitcontrib.inventory.ContribInventory;
import org.bukkitcontrib.inventory.DoubleChestInventory;

public class ContribCraftChest extends CraftChest implements ContribChest{
    protected TileEntityChest chest;
    public ContribCraftChest(Block block) {
        super(block);
        chest = getTileEntity();
    }

    @Override
    public boolean isDoubleChest() {
        return getOtherSide() != null;
    }
    
    public ContribChest getOtherSide() {
        if (getBlock().getRelative(1, 0, 0).getType() == Material.CHEST) {
            BlockState bs = getBlock().getRelative(1, 0, 0).getState();
            if (bs instanceof ContribChest) {
                return ((ContribChest)bs);
            }
        }
        if (getBlock().getRelative(-1, 0, 0).getType() == Material.CHEST) {
            BlockState bs = getBlock().getRelative(-1, 0, 0).getState();
            if (bs instanceof ContribChest) {
                return ((ContribChest)bs);
            }
        }
        if (getBlock().getRelative(0, 0, 1).getType() == Material.CHEST) {
            BlockState bs = getBlock().getRelative(0, 0, 1).getState();
            if (bs instanceof ContribChest) {
                return ((ContribChest)bs);
            }
        }
        if (getBlock().getRelative(0, 0, -1).getType() == Material.CHEST) {
            BlockState bs = getBlock().getRelative(0, 0, -1).getState();
            if (bs instanceof ContribChest) {
                return ((ContribChest)bs);
            }
        }
        return null;
    }

    @Override
    public DoubleChestInventory getFullInventory() {
        if (isDoubleChest()){
            ContribCraftChest other = (ContribCraftChest)getOtherSide();
            return new ContribDoubleChestInventory(new InventoryLargeChest("Double Chest", chest, other.chest), getBlock(), other.getBlock());
        }
        return null;
    }
    
    @Override
    public ContribInventory getLargestInventory() {
        if (isDoubleChest()){
            return getFullInventory();
        }
        return getInventory();
    }
    
    @Override
    public ContribInventory getInventory() {
        return new ContribCraftInventory(chest);
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
