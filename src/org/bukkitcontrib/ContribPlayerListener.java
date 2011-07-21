package org.bukkitcontrib;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkitcontrib.event.bukkitcontrib.BukkitContribSPEnable;
import org.bukkitcontrib.inventory.SimpleItemManager;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;
import org.bukkitcontrib.player.SimpleAppearanceManager;
import org.bukkitcontrib.player.SimpleSkyManager;
import org.bukkitcontrib.packet.PacketWorldSeed;

public class ContribPlayerListener extends PlayerListener{
	public PlayerManager manager = new PlayerManager();
	@Override
	public void onPlayerJoin(final PlayerJoinEvent event) {
		ContribCraftPlayer.updateNetServerHandler(event.getPlayer());
		ContribCraftPlayer.updateBukkitEntity(event.getPlayer());
		updatePlayerEvent(event);
		BukkitContrib.sendBukkitContribVersionChat(event.getPlayer());
		manager.onPlayerJoin(event.getPlayer());
	}

	@Override
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getFrom() == null || event.getTo() == null || event.getFrom().getWorld() == null || event.getTo().getWorld()== null) {
			return;
		}
		if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
			ContribCraftPlayer ccp = (ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(event.getPlayer());
			if(ccp.getVersion() > 16) {
				long newSeed = event.getTo().getWorld().getSeed();
				ccp.sendPacket(new PacketWorldSeed(newSeed));
			}
			Runnable update = new Runnable() {
				public void run() {
					ContribCraftPlayer.updateBukkitEntity(event.getPlayer());
					((SimpleAppearanceManager)BukkitContrib.getAppearanceManager()).onPlayerJoin((ContribPlayer)event.getPlayer());
				}
			};
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BukkitContrib.getInstance(), update, 5);
		}
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		ContribCraftPlayer player = (ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(event.getPlayer());
		if (event.getClickedBlock() != null) {
			Material type = event.getClickedBlock().getType();
			if (type == Material.CHEST || type == Material.DISPENSER || type == Material.WORKBENCH || type == Material.FURNACE) {
				player.getNetServerHandler().activeLocation = event.getClickedBlock().getLocation();
			}
		}
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		/*if (event.isCancelled()) {
			return;
		}*/
		ContribCraftPlayer player = (ContribCraftPlayer)ContribCraftPlayer.getContribPlayer(event.getPlayer());
		if (player.isBukkitContribEnabled()) {
			return;
		}
		if (event.getMessage().split("\\.").length == 3) {
			player.setVersion(event.getMessage().substring(1));
			if (player.isBukkitContribEnabled()) {
				event.setCancelled(true);
				((SimpleAppearanceManager)BukkitContrib.getAppearanceManager()).onPlayerJoin(player);
				manager.onBukkitContribSPEnable(player);
				((SimpleItemManager)BukkitContrib.getItemManager()).onPlayerJoin(player);
				((SimpleSkyManager)BukkitContrib.getSkyManager()).onPlayerJoin(player);
				System.out.println("[BukkitContrib] Successfully authenticated " + player.getName() + "'s BukkitContrib client. Running client version: " + player.getVersion());
				Bukkit.getServer().getPluginManager().callEvent(new BukkitContribSPEnable(player));
			}
		}
	}
	
	private void updatePlayerEvent(PlayerEvent event) {
		try {
			Field player = PlayerEvent.class.getDeclaredField("player");
			player.setAccessible(true);
			player.set(event, ((ContribCraftPlayer)((CraftPlayer)event.getPlayer()).getHandle().getBukkitEntity()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
