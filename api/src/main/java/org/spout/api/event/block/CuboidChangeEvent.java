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
package org.spout.api.event.block;

import org.spout.api.event.Cancellable;
import org.spout.api.event.Cause;
import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;
import org.spout.api.util.cuboid.CuboidBuffer;

/**
 * Called when a cuboid region is changed.<br/> Implements {@link Cancellable}, which allows this event's outcome to be cancelled.
 */
public class CuboidChangeEvent extends Event implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private final CuboidBuffer buffer;
	private final Cause<?> cause;

	public CuboidChangeEvent(CuboidBuffer buffer, Cause<?> cause) {
		this.buffer = buffer;
		this.cause = cause;
	}

	/**
	 * Gets the cubiod buffer that contains the cuboid area to be changed and the base and size of the cuboid area to change.
	 *
	 * @return cuboid
	 */
	public CuboidBuffer getCuboid() {
		return buffer;
	}

	/**
	 * The plugin that is setting the cuboid area.
	 *
	 * @return plugin
	 */
	public Cause<?> getCause() {
		return cause;
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
