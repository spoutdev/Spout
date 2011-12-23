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
package org.getspout.unchecked.api.event.player;

import org.getspout.api.entity.Entity;
import org.getspout.unchecked.api.event.Cancellable;
import org.getspout.unchecked.api.event.HandlerList;

/**
 * Called when a player right clicks an entity.
 */
public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	protected Entity rightClicked;

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	/**
	 * Gets the entity that was right clicked by the player.
	 *
	 * @return entity right clicked by player
	 */
	public Entity getRightClicked() {
		return rightClicked;
	}

	public void setRightClicked(Entity rightClicked) {
		this.rightClicked = rightClicked;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
