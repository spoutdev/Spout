package org.getspout;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.event.bukkitcontrib.BukkitContribSPEnable;
import org.getspout.inventory.SimpleItemManager;
import org.getspout.packet.PacketWorldSeed;
import org.getspout.player.ContribCraftPlayer;
import org.getspout.player.ContribPlayer;
import org.getspout.player.SimpleAppearanceManager;
import org.getspout.player.SimpleSkyManager;

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
		Runnable update = null;
		if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
			ContribCraftPlayer ccp = (ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(event.getPlayer());
			if(ccp.getVersion() > 16) {
				long newSeed = event.getTo().getWorld().getSeed();
				ccp.sendPacket(new PacketWorldSeed(newSeed));
			}
			update = new Runnable() {
				public void run() {
					ContribCraftPlayer.updateBukkitEntity(event.getPlayer());
					((SimpleAppearanceManager)BukkitContrib.getAppearanceManager()).onPlayerJoin((ContribPlayer)event.getPlayer());
				}
			};
		}
		else {
			update = new Runnable() {
				public void run() {
					((SimpleAppearanceManager)BukkitContrib.getAppearanceManager()).onPlayerJoin((ContribPlayer)event.getPlayer());
				}
			};
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BukkitContrib.getInstance(), update, 2);
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
		if (player.isSpoutCraftEnabled()) {
			return;
		}
		if (event.getMessage().split("\\.").length == 3) {
			player.setVersion(event.getMessage().substring(1));
			if (player.isSpoutCraftEnabled()) {
				event.setCancelled(true);
				((SimpleAppearanceManager)BukkitContrib.getAppearanceManager()).onPlayerJoin(player);
				manager.onBukkitContribSPEnable(player);
				((SimpleItemManager)BukkitContrib.getItemManager()).onPlayerJoin(player);
				((SimpleSkyManager)BukkitContrib.getSkyManager()).onPlayerJoin(player);
				System.out.println("[BukkitContrib] Successfully authenticated " + player.getName() + "'s Spoutcraft client. Running client version: " + player.getVersion());
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

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {

		if(event.isCancelled()) {
			return;
		}

		ContribCraftPlayer player = (ContribCraftPlayer)event.getPlayer();
		ContribNetServerHandler netServerHandler = player.getNetServerHandler();

		Location loc = event.getTo();

		int cx = ((int)loc.getX()) >> 4;
		int cz = ((int)loc.getZ()) >> 4;

		netServerHandler.setPlayerChunk(cx, cz);

	}

}
