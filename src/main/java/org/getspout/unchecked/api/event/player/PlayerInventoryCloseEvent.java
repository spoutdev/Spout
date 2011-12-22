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

import org.getspout.unchecked.api.event.HandlerList;
import org.getspout.unchecked.api.inventory.Inventory;

/**
 * Called when a player closes an inventory.
 */
public class PlayerInventoryCloseEvent extends PlayerInventoryEvent {
	private static HandlerList handlers = new HandlerList();

	private Inventory other;

	/**
	 * Get's the top (or main) inventory that was closed
	 *
	 * @return inventory closed
	 */

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Get's the second, bottom inventory that was closed.
	 *
	 * @return bottom inventory closed or null if there was no second inventory
	 *         closed
	 */
	public Inventory getBottomInventory() {
		return other;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
