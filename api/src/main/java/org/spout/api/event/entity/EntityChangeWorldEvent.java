/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.event.entity;

import org.spout.api.entity.Entity;
import org.spout.api.event.HandlerList;
import org.spout.api.geo.World;

/**
 * Called when an {@link Entity} changes its {@link World}.
 */
public class EntityChangeWorldEvent extends EntityEvent {
	private static HandlerList handlers = new HandlerList();
	private final World previous;
	private World target;

	public EntityChangeWorldEvent(Entity e, World previous, World target) {
		super(e);
		this.previous = previous;
		this.target = target;
	}

	/**
	 * Gets the world that the entity came from
	 *
	 * @return The world the entity came from.
	 */
	public World getPrevious() {
		return previous;
	}

	/**
	 * Gets the world that the entity will be moved to.
	 *
	 * @return The world the entity will be moved to.
	 */
	public World getTarget() {
		return target;
	}

	/**
	 * Sets the world that the entity will be moved to.
	 *
	 * @param target world the entity will be moved to.
	 */
	public void setTarget(World target) {
		this.target = target;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
