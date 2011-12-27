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
package org.getspout.api.event.entity;

import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.geo.discrete.Point;


/**
 * Holds information for entity movement events
 */
public class EntityMoveEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Point from;

	private Point to;

	/**
	 * Gets the Point this entity moved from
	 *
	 * @return Point the entity moved from
	 */
	public Point getFrom() {
		return from;
	}

	/**
	 * Sets the Point to mark as where the entity moved from
	 *
	 * @param from New Point to mark as the entitys previous Point
	 */
	public void setFrom(Point from) {
		this.from = from;
	}

	/**
	 * Gets the Point this entity moved to
	 *
	 * @return Point the entity moved to
	 */
	public Point getTo() {
		return to;
	}

	/**
	 * Sets the Point that this entity will move to
	 *
	 * @param to New Point this entity will move to
	 */
	public void setTo(Point to) {
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