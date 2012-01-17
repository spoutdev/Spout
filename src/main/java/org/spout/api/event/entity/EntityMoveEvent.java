/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.event.entity;

import org.spout.api.entity.Entity;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.geo.discrete.Point;

/**
 * Holds information for entity movement events
 */
public class EntityMoveEvent extends EntityEvent implements Cancellable {
	public EntityMoveEvent(Entity e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

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