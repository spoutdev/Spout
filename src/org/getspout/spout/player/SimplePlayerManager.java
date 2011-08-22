package org.getspout.spout.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.PlayerInformation;
import org.getspout.spoutapi.player.PlayerManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimplePlayerManager implements PlayerManager{
	
	HashMap<String, PlayerInformation> infoMap = new HashMap<String, PlayerInformation>();
	PlayerInformation globalInfo = new SimplePlayerInformation();

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

	@Override
	public PlayerInformation getPlayerInfo(Player player) {
		return infoMap.get(player.getName());
	}
	
	public void onPlayerJoin(Player player) {
		if (getPlayerInfo(player) == null) {
			infoMap.put(player.getName(), new SimplePlayerInformation());
		}
	}
	
	public void onPluginEnable() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			infoMap.put(player.getName(), new SimplePlayerInformation());
		}
	}
	
	public void onPluginDisable() {
		infoMap.clear();
	}

	@Override
	public PlayerInformation getGlobalInfo() {
		return globalInfo;
	}
}
