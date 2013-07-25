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

import org.spout.api.event.Cancellable;
import org.spout.api.event.Cause;
import org.spout.api.event.Event;
import org.spout.api.inventory.Inventory;

/**
 * Event which is fired when a modification to an inventory occurs.
 */
public abstract class InventoryEvent extends Event implements Cancellable {
	private final Inventory inventory;
	private final Cause<?> cause;

	public InventoryEvent(Inventory inventory, Cause<?> reason) {
		this.inventory = inventory;
		this.cause = reason;
	}

	/**
	 * Returns the inventory which caused this event.
	 *
	 * @return inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Returns the Cause which caused the InventoryEvent
	 * @return cause
	 */
	public Cause<?> getCause() {
		return cause;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}
}
