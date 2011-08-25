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

	@Override
	public SpoutPlayer[] getOnlinePlayers() {
		Player[] online = Bukkit.getServer().getOnlinePlayers();
		SpoutPlayer[] spoutPlayers = new SpoutPlayer[online.length];
		for (int i = 0; i < online.length; i++) {
			spoutPlayers[i] = getPlayer(online[i]);
		}
		return spoutPlayers;
	}
}
