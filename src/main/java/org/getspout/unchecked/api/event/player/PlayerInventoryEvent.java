/*
 * This file is part of Spout API (http://wiki.getspout.org/).
 *
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.unchecked.api.event.player;

import org.getspout.unchecked.api.event.Cancellable;
import org.getspout.unchecked.api.event.HandlerList;
import org.getspout.unchecked.api.geo.discrete.Point;
import org.getspout.unchecked.api.inventory.Inventory;

/**
 * Represents an inventory-related event.
 */
public abstract class PlayerInventoryEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	protected Point point = null;

	protected Inventory inventory;

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
