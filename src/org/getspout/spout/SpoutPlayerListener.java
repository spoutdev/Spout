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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.getspout.spout.chunkcache.ChunkCache;
import org.getspout.spout.player.SimpleAppearanceManager;
import org.getspout.spout.player.SimplePlayerManager;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketWorldSeed;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutPlayerListener extends PlayerListener{
	public PlayerManager manager = new PlayerManager();
	@Override
	public void onPlayerJoin(final PlayerJoinEvent event) {
		SpoutCraftPlayer.updateNetServerHandler(event.getPlayer());
		SpoutCraftPlayer.updateBukkitEntity(event.getPlayer());
		updatePlayerEvent(event);
		Spout.getInstance().authenticate(event.getPlayer());
		((SimplePlayerManager)SpoutManager.getPlayerManager()).onPlayerJoin(event.getPlayer());
		manager.onPlayerJoin(event.getPlayer());
	}

	@Override
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (!(event.getPlayer() instanceof SpoutPlayer)) {
			updatePlayerEvent(event);
		}
		if (event.isCancelled()) {
			return;
		}
		if (event.getFrom() == null || event.getTo() == null || event.getFrom().getWorld() == null || event.getTo().getWorld()== null) {
			return;
		}
		Runnable update = null;
		final SpoutPlayer scp = SpoutCraftPlayer.getPlayer(event.getPlayer());
		if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
			if(scp.isSpoutCraftEnabled()) {
				long newSeed = event.getTo().getWorld().getSeed();
				scp.sendPacket(new PacketWorldSeed(newSeed));
			}
			update = new Runnable() {
				public void run() {
					SpoutCraftPlayer.updateBukkitEntity(event.getPlayer());
					((SimpleAppearanceManager)SpoutManager.getAppearanceManager()).onPlayerJoin(scp);
				}
			};
		}
		else {
			update = new Runnable() {
				public void run() {
					((SimpleAppearanceManager)SpoutManager.getAppearanceManager()).onPlayerJoin(scp);
				}
			};
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Spout.getInstance(), update, 2);
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
			if (type == Material.CHEST || type == Material.DISPENSER || type == Material.WORKBENCH || type == Material.FURNACE) {
				player.getNetServerHandler().activeLocation = event.getClickedBlock().getLocation();
			}
		}
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
	}

}
