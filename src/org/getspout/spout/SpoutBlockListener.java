package org.getspout.spout;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockListener;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutBlockListener extends BlockListener {
	
	private final SimpleMaterialManager mm;
	
	public SpoutBlockListener() {
		mm = (SimpleMaterialManager)SpoutManager.getMaterialManager();
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		super.onBlockBreak(event);
		
		if (event.isCancelled()) {
			return;
		}
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (block.getType() != Material.STONE && block.getType() != Material.GLASS) {
			return;
		}
		
		SpoutPlayer player = (SpoutPlayer)event.getPlayer();
		
		if (block.isCustomBlock()) {
			CustomBlock material = block.getCustomBlock();
			material.onBlockDestroyed(block.getWorld(), block.getX(), block.getY(), block.getZ(), player);
			if (material.getItemDrop() != null) {
				if (player.getGameMode() == GameMode.SURVIVAL) {
					block.getWorld().dropItem(block.getLocation(), material.getItemDrop());
				}
				block.setTypeId(0);
				event.setCancelled(true);
			}
			mm.removeBlockOverride(block);
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

	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		super.onBlockPistonExtend(event);
		
		if (event.isCancelled()) {
			return;
		}
		
		List<Block> eventBlocks = event.getBlocks();
		ListIterator<Block> itr = eventBlocks.listIterator(eventBlocks.size());
		while(itr.hasPrevious()) {
			pistonBlockMove((SpoutBlock)itr.previous(), event.getDirection());
		}
	}
		
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		super.onBlockPistonRetract(event);
		
		if (event.isCancelled() || !event.isSticky()) {
			return;
		}
		
		pistonBlockMove((SpoutBlock)event.getBlock().getRelative(event.getDirection(), 2), event.getDirection().getOppositeFace());
	}
	
	private void pistonBlockMove(SpoutBlock block, BlockFace blockFace) {
		if (block.getType() != Material.STONE && block.getType() != Material.GLASS) {
			return;
		}
				
		if (block.isCustomBlock()) {
			SpoutBlock targetBlock = block.getRelative(blockFace);
			CustomBlock material = block.getCustomBlock();
			mm.removeBlockOverride(block);
			mm.overrideBlock(targetBlock.getWorld(), targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), material);
		}
	}
}
