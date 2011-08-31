package org.getspout.spout;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.inventory.SimpleItemManager;
import org.getspout.spoutapi.SpoutManager;

public class SpoutBlockListener extends BlockListener {
	
	private final SimpleItemManager i;
	
	public SpoutBlockListener() {
		i = (SimpleItemManager)SpoutManager.getItemManager();
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getBlockPlaced().getType() != Material.STONE) {
			return;
		}
		
		ItemStack itemInHand = event.getPlayer().getItemInHand();

		int damage = itemInHand.getDurability();
		
		Integer newBlockId = i.getItemBlock(damage);
		Short newMetaData = i.getItemMetaData(damage);
		
		if (newBlockId != null && newMetaData != null) {
			Block block = event.getBlockPlaced();
			block.setTypeIdAndData(newBlockId, (byte)(newMetaData & 0xF), true);
		} else if (damage >= 1024) {
			event.setCancelled(true);
		}
		
	}
	
	//This replaces nms functionality that is broken due to 
	//the references in the nms.Block.byId[] no longer matching
	//the static final refernces in Block.
	//Specifically, public boolean a(int i, int j, int k, int l, boolean flag, int i1)
	//in World.java is broken otherwise.
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		if (event.isBuildable()) {
			return;
		}
		Block block = event.getBlock();
		Material type = block.getType();
		if (type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.FIRE || type == Material.SNOW) {
			if (net.minecraft.server.Block.byId[event.getMaterialId()].canPlace(((CraftWorld)block.getWorld()).getHandle(), block.getX(), block.getY(), block.getZ())) {
				event.setBuildable(true);
			}
		}
	}

}
