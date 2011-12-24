/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.unchecked.api.event.entity;

import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.geo.discrete.Point;

/**
 * Called when an entity spawns into the world.
 */
public class EntitySpawnEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Point point;

	private SpawnReason reason;

	/**
	 * Gets the location in which spawning will take place.
	 *
	 * @return
	 */
	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	/**
	 * Gets the reason in which spawning occurred.
	 *
	 * @return
	 */
	public SpawnReason getReason() {
		return reason;
	}

	public void setReason(SpawnReason reason) {
		this.reason = reason;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Specifies the reason that spawning took place
	 */
	public enum SpawnReason {
		/**
		 * When a creature is spawned by a player that is sleeping
		 */
		BED,
		/**
		 * When a creature spawns from an egg
		 */
		EGG,
		/**
		 * When something spawns from natural means
		 */
		NATURAL,
		/**
		 * When a creature spawns from a spawner
		 */
		SPAWNER,
		/**
		 * When a creature spawns because of a lightning strike
		 */
		LIGHTNING,
		/**
		 * When a player respawns.
		 */
		RESPAWN,
		/**
		 * When a creature is spawned through the API.
		 */
		CUSTOM;

	}

}
