/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.chunkcache.ChunkCache;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spout.player.SimpleAppearanceManager;
import org.getspout.spout.player.SimplePlayerManager;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutPlayerListener extends PlayerListener{
	public PlayerManager manager = new PlayerManager();
	@Override
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (!event.getPlayer().getClass().equals(SpoutCraftPlayer.class)) {
			SpoutCraftPlayer.updateNetServerHandler(event.getPlayer());
			SpoutCraftPlayer.updateBukkitEntity(event.getPlayer());
			updatePlayerEvent(event);
			Spout.getInstance().authenticate(event.getPlayer());
		}
		((SimplePlayerManager)SpoutManager.getPlayerManager()).onPlayerJoin(event.getPlayer());
		manager.onPlayerJoin(event.getPlayer());
		Spout.getInstance().getEntityTrackingManager().onEntityJoin(event.getPlayer());
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getPlayer() instanceof SpoutCraftPlayer) {
			SpoutCraftPlayer player = (SpoutCraftPlayer)event.getPlayer();
			if (event.getReason().equals("You moved too quickly :( (Hacking?)")) {
				if (player.canFly()) {
					event.setCancelled(true);
				}
				System.out.println(System.currentTimeMillis() < player.velocityAdjustmentTime);
				if (System.currentTimeMillis() < player.velocityAdjustmentTime) {
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (!(event.getPlayer() instanceof SpoutPlayer)) {
			updatePlayerEvent(event);
		}
		if (event.isCancelled()) {
			return;
		}
		
		Runnable update = null;
		final SpoutCraftPlayer scp = (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(event.getPlayer());
		
		if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			update = new PostTeleport(scp);
		}
		if (update != null) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Spout.getInstance(), update, 2);
		}
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!(event.getPlayer() instanceof SpoutPlayer)) {
			updatePlayerEvent(event);
		}
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(event.getPlayer());
		if (event.getClickedBlock() != null) {
			Material type = event.getClickedBlock().getType();
			boolean action = false;
			if (type == Material.CHEST || type == Material.DISPENSER || type == Material.WORKBENCH || type == Material.FURNACE) {
				player.getNetServerHandler().activeLocation = event.getClickedBlock().getLocation();
				action = true;
			}
			
			if (event.hasItem() && !action) {
				SpoutBlock block = (SpoutBlock)event.getClickedBlock().getRelative(event.getBlockFace());
				
				ItemStack item = event.getItem();
				int damage = item.getDurability();
				if(item.getType() == Material.FLINT && damage != 0 && !action) {
					
					SimpleMaterialManager mm = (SimpleMaterialManager)SpoutManager.getMaterialManager();

					int newBlockId = mm.getItemBlock(damage);
					short newMetaData = (short) mm.getItemMetaData(damage);
					
					if (newBlockId != 0 ) {
						if (!player.getEyeLocation().getBlock().equals(block) && !player.getLocation().getBlock().equals(block)) {
						
							CustomBlock cb = MaterialData.getCustomBlock(damage);
							BlockState oldState = block.getState();
							block.setTypeIdAndData(cb.getBlockId(), (byte)(newMetaData & 0xF), true);
							mm.overrideBlock(block, cb);
							
							if (canPlaceAt(block, oldState, (SpoutBlock)event.getClickedBlock(), item, player)) {
								// Yay, take the item from inventory
								if (player.getGameMode() == GameMode.SURVIVAL) {
									if(item.getAmount() == 1) {
										event.getPlayer().setItemInHand(null);
									} else {
										item.setAmount(item.getAmount() - 1);
									}
								}
								player.updateInventory();
							} else {
								// Event cancelled or can't build
								mm.removeBlockOverride(block);
								block.setTypeIdAndData(oldState.getTypeId(), oldState.getRawData(), true);
							}
						}
					}
				}
			}
		}
	}
	
	//TODO: canBuild should be set properly, CraftEventFactory.canBuild() would do this... 
	//       but it's private so... here it is >.>
	private boolean canPlaceAt(SpoutBlock result, BlockState oldState, SpoutBlock clicked, ItemStack item, SpoutPlayer player) {
		int spawnRadius = Bukkit.getServer().getSpawnRadius();
		boolean canBuild = false;
		if (spawnRadius <= 0 || player.isOp()) { // Fast checks
			canBuild = true;
		} else {
			Location spawn = clicked.getWorld().getSpawnLocation();
			if (Math.max(Math.abs(result.getX()-spawn.getBlockX()), Math.abs(result.getZ()-spawn.getBlockZ())) > spawnRadius) { // Slower check
				canBuild = true;
			}
		}
		
		BlockPlaceEvent placeEvent = new BlockPlaceEvent(result, oldState, clicked, item, player, canBuild);
		Bukkit.getPluginManager().callEvent(placeEvent);
		
		return !placeEvent.isCancelled() && placeEvent.canBuild();
	}
	
	private void updatePlayerEvent(PlayerEvent event) {
		try {
			Field player = PlayerEvent.class.getDeclaredField("player");
			player.setAccessible(true);
			player.set(event, (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(event.getPlayer()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!(event.getPlayer() instanceof SpoutPlayer)) {
			updatePlayerEvent(event);
		} else {
			event.setCancelled(event.isCancelled()||!((SpoutPlayer)event.getPlayer()).isPreCachingComplete());
		}
		if(event.isCancelled()) {
			return;
		}
		
		SpoutCraftPlayer player = (SpoutCraftPlayer)event.getPlayer();
		SpoutNetServerHandler netServerHandler = player.getNetServerHandler();

		Location loc = event.getTo();

		int cx = ((int)loc.getX()) >> 4;
		int cz = ((int)loc.getZ()) >> 4;

		netServerHandler.setPlayerChunk(cx, cz);
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		int id = event.getPlayer().getEntityId();
		ChunkCache.playerQuit(id);
		MapChunkThread.removeId(id);
		if (event.getPlayer() instanceof SpoutCraftPlayer) {
			SpoutCraftPlayer player = (SpoutCraftPlayer)event.getPlayer();
			SpoutNetServerHandler netServerHandler = player.getNetServerHandler();
			if (netServerHandler.activeInventory) {
				Inventory inventory = netServerHandler.getActiveInventory();
	
				InventoryCloseEvent closeEvent = new InventoryCloseEvent((Player) player, inventory, netServerHandler.getDefaultInventory(), netServerHandler.activeLocation);
				Bukkit.getServer().getPluginManager().callEvent(closeEvent);
			}
			
			
		}
		Spout.getInstance().getEntityTrackingManager().untrack(event.getPlayer());
	}

}

class PostTeleport implements Runnable {
	SpoutCraftPlayer player;
	public PostTeleport(SpoutCraftPlayer player){
		this.player = player;
	}
	
	@Override
	public void run() {
		((SimpleAppearanceManager)SpoutManager.getAppearanceManager()).onPlayerJoin(player);
	}
}
