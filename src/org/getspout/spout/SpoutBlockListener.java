package org.getspout.spout;

import org.bukkit.Material;
import org.bukkit.block.Block;
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

}
