/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.event.inventory;

import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.event.player.PlayerEvent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.Inventory;
import org.spout.api.player.Player;

/**
 * Represents an inventory-related event.
 */
public abstract class PlayerInventoryEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	protected Point point = null;

	protected Inventory inventory;

	public PlayerInventoryEvent(Player p) {
		super(p);
	}

	/**
	 * Gets the inventory involved in this event.
	 *
	 * @return The inventory.
	 */
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	/**
	 * Gets the location of the inventory, if there is one. Returns null if no
	 * location could be found.
	 *
	 * @return location of the inventory
	 */
	public Point getPoint() {
		return point;
	}

	public void setPosition(Point point) {
		this.point = point;
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
