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

import org.spout.api.entity.Entity;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;

/**
 * Called when an Entity interacts with something else.
 * Implements {@link Cancellable}, which allows this event's normal outcome to be prevented.
 * 
 * @param <T> the type of object being interacted with.
 */
public class EntityInteractEvent<T> extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private T interacted;

	public EntityInteractEvent(Entity e, T interacted) {
		super(e);
		this.interacted = interacted;
	}

	/**
	 * Get the object being interacted with.
	 * @return The object interacted with.
	 */
	public T getInteractedWith() {
		return interacted;
	}

	/**
	 * Set the object being interacted with.
	 * @param t The object that will be interacted with.
	 */
	public void setInteractedWith(T t) {
		interacted = t;
	}

	@Override
	public void setCancelled(boolean cancelled) {
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