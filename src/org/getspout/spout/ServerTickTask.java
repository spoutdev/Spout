package org.getspout.spout;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.event.bukkitcontrib.ServerTickEvent;

public class ServerTickTask implements Runnable {

	@Override
	public void run() {
		Spout.getInstance().playerListener.manager.onServerTick();
		Player[] online = Bukkit.getServer().getOnlinePlayers();
		for (Player player : online) {
			if (player instanceof SpoutCraftPlayer) {
				((SpoutCraftPlayer)player).onTick();
			}
		}
		SpoutCraftChunk.updateTicks();
		ServerTickEvent event = new ServerTickEvent();
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

}
