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

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spout.config.ConfigReader;
import org.getspout.spout.inventory.SimpleItemManager;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.player.SimpleAppearanceManager;
import org.getspout.spout.player.SimpleBiomeManager;
import org.getspout.spout.player.SimpleFileManager;
import org.getspout.spout.player.SimpleSkyManager;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.packet.PacketAllowVisualCheats;
import org.getspout.spoutapi.packet.PacketCacheHashUpdate;
import org.getspout.spoutapi.packet.PacketMovementModifiers;
import org.getspout.spoutapi.player.PlayerInformation;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PlayerManager {
	private HashMap<String, Integer> timer = new HashMap<String, Integer>();
	HashMap<String, PlayerInformation> infoMap = new HashMap<String, PlayerInformation>();
	
	public void onPlayerJoin(Player player){
		timer.put(player.getName(), ConfigReader.getAuthenticateTicks());
	}
	
	public void onServerTick() {
		if (!ConfigReader.isForceClient()) {
			return;
		}
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (timer.containsKey(player.getName())) {
				int ticksLeft = timer.get(player.getName());
				if (--ticksLeft > 0) {
					timer.put(player.getName(), ticksLeft);
				}
				else {
					timer.remove(player.getName());
					System.out.println("[Spout] Failed to authenticate " + player.getName() + "'s client in " + ConfigReader.getAuthenticateTicks() + " server ticks.");
					if (ConfigReader.isForceClient()) {
						System.out.println("[Spout] Kicking " + player.getName() + " for not running Spout client");
						player.kickPlayer(ConfigReader.getKickMessage());
					}
				}
			}
		}
	}
	
	public void onSpoutcraftEnable(SpoutPlayer player) {
		timer.remove(player.getName());
		((SimpleAppearanceManager)SpoutManager.getAppearanceManager()).onPlayerJoin(player);
		((SimpleItemManager)SpoutManager.getItemManager()).onPlayerJoin(player);
		((SimpleSkyManager)SpoutManager.getSkyManager()).onPlayerJoin(player);
		((SimpleBiomeManager)SpoutManager.getBiomeManager()).onPlayerJoin(player);
		((SimpleFileManager)SpoutManager.getFileManager()).onPlayerJoin(player);
		player.sendPacket(new PacketAllowVisualCheats(ConfigReader.isAllowVisualCheats()));
		player.sendPacket(new PacketMovementModifiers(player.getGravityMultiplier(), player.getWalkingMultiplier(), player.getSwimmingMultiplier()));
		System.out.println("[Spout] Successfully authenticated " + player.getName() + "'s Spoutcraft client. Running client version: " + player.getVersion());
		if(player.getVersion() > 101) {
			PacketCacheHashUpdate p = new PacketCacheHashUpdate();
			p.reset = true;
			((SpoutCraftPlayer)player).getNetServerHandler().sendPacket(new CustomPacket(p));
		}
		Bukkit.getServer().getPluginManager().callEvent(new SpoutCraftEnableEvent(player));
	}

}
