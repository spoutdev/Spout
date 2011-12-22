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

import org.getspout.unchecked.api.event.Cancellable;
import org.getspout.unchecked.api.event.HandlerList;
import org.getspout.unchecked.api.util.Location;

/**
 * Holds information for entity movement events
 */
public class EntityMoveEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Location from;

	private Location to;

	/**
	 * Gets the location this entity moved from
	 *
	 * @return Location the entity moved from
	 */
	public Location getFrom() {
		return from;
	}

	/**
	 * Sets the location to mark as where the entity moved from
	 *
	 * @param from New location to mark as the entitys previous location
	 */
	public void setFrom(Location from) {
		this.from = from;
	}

	/**
	 * Gets the location this entity moved to
	 *
	 * @return Location the entity moved to
	 */
	public Location getTo() {
		return to;
	}

	/**
	 * Sets the location that this entity will move to
	 *
	 * @param to New Location this entity will move to
	 */
	public void setTo(Location to) {
		this.to = to;
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

}