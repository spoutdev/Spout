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
package org.spout.api.event.player;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.HandlerList;
import org.spout.api.event.entity.EntityInteractEntityEvent;
import org.spout.api.geo.discrete.Point;

/**
 * Called when a {@link Player} interacts with a {@link Entity}.
 */
public class PlayerInteractEntityEvent extends EntityInteractEntityEvent {
	private static HandlerList handlers = new HandlerList();
	private final Action action;

	public PlayerInteractEntityEvent(Player p, Entity interacted, Point point, Action action) {
		super(p, interacted, point);
		this.action = action;
	}

	/**
	 * Gets the action by the {@link Player} that caused the interaction.
	 *
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	@Override
	public Player getEntity() {
		return (Player) super.getEntity();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
