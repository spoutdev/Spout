package org.getspout.spout;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.inventory.SimpleItemManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.MaterialData;

public class SpoutBlockListener extends BlockListener {
	
	private final SimpleItemManager i;
	
	public SpoutBlockListener() {
		i = (SimpleItemManager)SpoutManager.getItemManager();
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getBlockPlaced().getType() != Material.STONE && event.getBlockPlaced().getType() != Material.GLASS) {
			return;
		}
		
		ItemStack itemInHand = event.getPlayer().getItemInHand();

		int damage = itemInHand.getDurability();
		
		int newBlockId = i.getItemBlock(damage);
		short newMetaData = i.getItemMetaData(damage);
		
		if (newBlockId != 0 ) {
			Block block = event.getBlockPlaced();
			block.setTypeIdAndData(newBlockId, (byte)(newMetaData & 0xF), true);
			i.overrideBlock(block, MaterialData.getCustomBlock(damage));
		} else if (damage >= 1024) {
			event.setCancelled(true);
		}
		
	}	
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		super.onBlockBreak(event);
		
		if (event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlock();
		if(block instanceof SpoutBlock) {
			SpoutBlock sb = (SpoutBlock) block;
			if (sb.getType() != Material.STONE && sb.getType() != Material.GLASS) {
				return;
			}
			
			if (sb.isCustomBlock()) {
				if(i.hasItemDrop(sb.getCustomBlock())) {
					sb.getWorld().dropItem(sb.getLocation(), i.getItemDrop(sb.getCustomBlock()));
					sb.setTypeId(0);
					event.setCancelled(true);
				}
				i.overrideBlock(sb, null, null);
			}
		}
	}
	
	//This replaces nms functionality that is broken due to 
	//the references in the nms.Block.byId[] no longer matching
	//the static final refernces in Block.
	//Specifically, public boolean a(int i, int j, int k, int l, boolean flag, int i1)
	//in World.java is broken otherwise.
	@Override
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
