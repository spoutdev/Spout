package org.bukkitcontrib;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkitcontrib.block.ContribCraftChunk;
import org.bukkitcontrib.event.bukkitcontrib.ServerTickEvent;
import org.bukkitcontrib.player.ContribCraftPlayer;

public class ServerTickTask implements Runnable {

	@Override
	public void run() {
		BukkitContrib.playerListener.manager.onServerTick();
		Player[] online = Bukkit.getServer().getOnlinePlayers();
		for (Player player : online) {
			if (player instanceof ContribCraftPlayer) {
				((ContribCraftPlayer)player).onTick();
			}
		}
		for (World w : Bukkit.getServer().getWorlds()) {
			Chunk[] chunks = w.getLoadedChunks();
			for (Chunk chunk : chunks) {
				if (chunk instanceof ContribCraftChunk) {
					((ContribCraftChunk)chunk).onTick();
				}
			}
		}
		ServerTickEvent event = new ServerTickEvent();
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

}
