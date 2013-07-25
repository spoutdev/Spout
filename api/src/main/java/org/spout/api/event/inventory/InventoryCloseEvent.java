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
package org.spout.api.event.inventory;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.event.cause.EntityCause;
import org.spout.api.event.cause.PlayerCause;
import org.spout.api.inventory.Inventory;

/**
 * Event which is fired when an inventory is closed.
 */
public class InventoryCloseEvent extends InventoryEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private final Entity viewer;

	public InventoryCloseEvent(Inventory inventory, Entity viewer) {
		super(inventory, (viewer instanceof Player ? new PlayerCause((Player) viewer) : new EntityCause(viewer)));
		this.viewer = viewer;
	}

	/**
	 * Returns the viewer who closed the inventory
	 * 
	 * @return viewer
	 */
	public Entity getViewer() {
		return viewer;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
