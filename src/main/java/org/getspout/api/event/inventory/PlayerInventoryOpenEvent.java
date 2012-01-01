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
package org.getspout.api.event.inventory;

import org.getspout.api.event.HandlerList;
import org.getspout.api.inventory.Inventory;

/**
 * Called when a player opens an inventory.
 */
public class PlayerInventoryOpenEvent extends PlayerInventoryEvent {
	private static HandlerList handlers = new HandlerList();

	private Inventory other;

	/**
	 * Gets the top (or main) inventory that was opened
	 *
	 * @return inventory opened
	 */

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Gets the second, bottom inventory that was opened.
	 *
	 * @return bottom inventory opened or null if there was no second inventory
	 *         opened
	 */
	public Inventory getBottomInventory() {
		return other;
	}

	public void setBottomInventory(Inventory other) {
		this.other = other;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
