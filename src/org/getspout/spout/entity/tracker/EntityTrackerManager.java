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
package org.getspout.spout.entity.tracker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EntityTrackerManager {
	private ConcurrentHashMap<String, EntityTracker> trackers = new ConcurrentHashMap<String, EntityTracker>();
	public EntityTrackerManager() {
		Thread thread = new EntityTrackingThread(Spout.getInstance(), trackers);
		thread.run();
	}
	public void track(SpoutPlayer player) {
		if (player.isSpoutCraftEnabled()) {
			EntityTracker tracker = new EntityTracker(player);
			trackers.put(player.getName(), tracker);
			tracker.trackEntities(player.getWorld().getEntities());
		}
	}
	
	public void untrack(Player player) {
		EntityTracker old = trackers.remove(player.getName());
		if (old != null) {
			old.untrackAllEntities();
		}
	}
	
	public void onPostWorldChange(SpoutPlayer player) {
		EntityTracker tracker = trackers.get(player.getName());
		if (tracker != null) {
			tracker.untrackAllEntities();
			tracker.trackEntities(player.getWorld().getEntities());
		}
	}
	
	public void onChunkLoad(Chunk chunk){
		List<Player> players = chunk.getWorld().getPlayers();
		Entity[] entities = chunk.getEntities();
		for (Player p : players) {
			if (p instanceof SpoutPlayer) {
				SpoutPlayer player = (SpoutPlayer)p;
				if (player.isSpoutCraftEnabled()) {
					EntityTracker tracker = trackers.get(player.getName());
					if (tracker != null) {
						tracker.trackEntities(entities);
					}
				}
			}
		}
	}
	
	public void onEntityJoin(Entity entity) {
		List<Player> players = entity.getWorld().getPlayers();
		for (Player p : players) {
			if (p instanceof SpoutPlayer) {
				SpoutPlayer player = (SpoutPlayer)p;
				if (player.isSpoutCraftEnabled()) {
					EntityTracker tracker = trackers.get(player.getName());
					if (tracker != null) {
						tracker.trackEntity(entity);
					}
				}
			}
		}
	}
	
	public void onEntityDeath(Entity entity) {
		List<Player> players = entity.getWorld().getPlayers();
		for (Player p : players) {
			if (p instanceof SpoutPlayer) {
				SpoutPlayer player = (SpoutPlayer)p;
				if (player.isSpoutCraftEnabled()) {
					EntityTracker tracker = trackers.get(player.getName());
					if (tracker != null) {
						tracker.untrackEntity(entity);
					}
				}
			}
		}
	}
	
	public void onTick() {

	}
}

class EntityTrackingThread extends Thread {
	Plugin plugin;
	Map<String, EntityTracker> trackers;
	public EntityTrackingThread(Plugin plugin, Map<String, EntityTracker> trackers) {
		this.plugin = plugin;
		this.trackers = trackers;
	}
	
	public void run() {
		while(plugin.isEnabled()) {
			try {
				sleep(50);
			}
			catch (InterruptedException ignore) { }
			
			for (EntityTracker tracker : trackers.values()) {
				tracker.onTick();
			}
		}
	}
}
