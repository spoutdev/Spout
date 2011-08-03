package org.getspout.spout.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.PlayerManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimplePlayerManager implements PlayerManager{

	@Override
	public SpoutPlayer getPlayer(Player player) {
		return SpoutCraftPlayer.getPlayer(player);
	}

	@Override
	public SpoutPlayer getPlayer(UUID id) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getUniqueId().equals(id)) {
				return getPlayer(player);
			}
		}
		return null;
	}

	@Override
	public SpoutPlayer getPlayer(int entityId) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getEntityId() == entityId) {
				return getPlayer(player);
			}
		}
		return null;
	}

}
