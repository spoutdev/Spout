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

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EntityTrackerManager {
	private HashMap<String, EntityTracker> trackers = new HashMap<String, EntityTracker>();
	//private long nanoTime =0;
	public void track(SpoutPlayer player) {
		if (player.isSpoutCraftEnabled()) {
			EntityTracker tracker = new EntityTracker(player);
			trackers.put(player.getName(), tracker);
			tracker.trackEntities(player.getWorld().getEntities());
		}
	}
	
	public void untrack(SpoutPlayer player) {
		EntityTracker old = trackers.remove(player.getName());
		if (old != null) {
			old.untrackAllEntities();
		}
	}
	
	public void onPostWorldChange(SpoutPlayer player) {
		//long start = System.nanoTime();
		EntityTracker tracker = trackers.get(player.getName());
		if (tracker != null) {
			tracker.untrackAllEntities();
			tracker.trackEntities(player.getWorld().getEntities());
		}
		//nanoTime += (System.nanoTime() - start);
	}
	
	public void onEntityJoin(Entity entity) {
		//long start = System.nanoTime();
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
		//nanoTime += (System.nanoTime() - start);
	}
	
	public void onEntityDeath(Entity entity) {
		//long start = System.nanoTime();
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
				//nanoTime += (System.nanoTime() - start);
	}
	
	public void onTick() {
		//long start = System.nanoTime();
		for (EntityTracker tracker : trackers.values()) {
			tracker.onTick();
		}
		//nanoTime += (System.nanoTime() - start);
		//System.out.println("Tracking " + trackers.size() + " players took " + nanoTime / 1E6D + " ms");
		//nanoTime = 0L;
	}
}
