/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import org.spout.api.collision.SpoutContactInfo;
import org.spout.api.entity.Entity;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;

public abstract class EntityCollideEvent<T> extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private final T collided;
	private final SpoutContactInfo info;

	public EntityCollideEvent(Entity e, T collided, SpoutContactInfo info) {
		super(e);
		this.collided = collided;
		this.info = info;
	}

	/**
	 * Returns the collided object of the event.
	 * @return The collided object
	 */
	public T getCollided() {
		return collided;
	}

	/**
	 * Returns a {@link SpoutContactInfo} which details all the information
	 * concerning the collision that occurred.
	 * @return the contact info
	 */
	public SpoutContactInfo getContactInfo() {
		return info;
	}

	@Override
	public void setCancelled(final boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}