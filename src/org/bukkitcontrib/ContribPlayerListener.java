package org.bukkitcontrib;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ContribPlayerListener extends PlayerListener{

	public void onPlayerJoin(PlayerJoinEvent event) {
		swapNetServerHandler(event.getPlayer());
	}

	private static void swapNetServerHandler(Player player) {
		CraftPlayer cp = (CraftPlayer)player;
		CraftServer server = (CraftServer)BukkitContrib.getMinecraftServer();
		
		if (!(cp.getHandle().netServerHandler instanceof BukkitContribNetServerHandler)) {
			Location loc = player.getLocation();
			BukkitContribNetServerHandler handler = new BukkitContribNetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
			handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			cp.getHandle().netServerHandler = handler;
		}
	}
}
