/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event.entity;

import org.getspout.api.entity.object.Item;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;

/**
 * Called when an item is dropped.
 */
public class ItemDropEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Item item;

	private ItemDropReason reason;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public ItemDropReason getReason() {
		return reason;
	}

	public void setReason(ItemDropReason reason) {
		this.reason = reason;
	}

	/**
	 * Enum specifying the drop reason.
	 */
	public enum ItemDropReason {
		PLAYER_DROP,
		ENTITY_DEATH,
		EXPLOSION;

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
