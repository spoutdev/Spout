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

import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.getspout.spoutapi.packet.PacketUniqueId;
import org.getspout.spoutapi.player.SpoutPlayer;

import gnu.trove.set.hash.TIntHashSet;

public class EntityTracker {
	private TIntHashSet entitiesTracked = new TIntHashSet(1000);
	private LinkedList<Entity> aliveNeedsUpdate = new LinkedList<Entity>();
	private LinkedList<Entity> deadNeedsUpdate = new LinkedList<Entity>();
	private final SpoutPlayer trackedPlayer;
	public EntityTracker(SpoutPlayer player) {
		trackedPlayer = player;
	}
	
	public SpoutPlayer getTrackedPlayer() {
		return trackedPlayer;
	}
	
	public synchronized boolean isEntityTracked(int id) {
		return entitiesTracked.contains(id);
	}
	
	public synchronized void trackEntity(Entity entity) {
		if (!entitiesTracked.contains(entity.getEntityId())) {
			entitiesTracked.add(entity.getEntityId());
			aliveNeedsUpdate.add(entity);
		}
	}
	
	public synchronized void untrackEntity(Entity entity) {
		if (entitiesTracked.remove(entity.getEntityId())) {
			deadNeedsUpdate.add(entity);
		}
	}
	
	//Unsynchronized because trackEntity(...) is already synchronized
	public void trackEntities(Entity[] entities) {
		for (Entity e : entities) {
			trackEntity(e);
		}
	}
	
	//Unsynchronized because trackEntity(...) is already synchronized
	public void trackEntities(List<Entity> entities) {
		for (Entity e : entities) {
			trackEntity(e);
		}
	}
	
	public synchronized void untrackAllEntities() {
		aliveNeedsUpdate.clear();
		deadNeedsUpdate.clear();
		entitiesTracked.clear();
	}
	
	public synchronized void onTick() {
		if (!aliveNeedsUpdate.isEmpty()) {
			trackedPlayer.sendPacket(new PacketUniqueId(aliveNeedsUpdate, true));
			aliveNeedsUpdate = new LinkedList<Entity>();
		}
		if (!deadNeedsUpdate.isEmpty()) {
			trackedPlayer.sendPacket(new PacketUniqueId(deadNeedsUpdate, false));
			deadNeedsUpdate = new LinkedList<Entity>();
		}
	}
}
