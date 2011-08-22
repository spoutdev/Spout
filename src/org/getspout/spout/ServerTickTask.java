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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.event.spout.ServerTickEvent;

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
