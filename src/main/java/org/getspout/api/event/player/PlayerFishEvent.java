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
package org.getspout.api.event.player;

import org.getspout.api.entity.Entity;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;

/**
 * Called when a player fishes for something.
 */
public class PlayerFishEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Entity caught = null;

	private FishingStatus status = null;

	/**
	 * Gets the entity caught by the player
	 *
	 * @return Entity caught by the player, null if fishing, bobber has gotten
	 *         stuck in the ground or nothing has been caught
	 */
	public Entity getCaught() {
		return caught;
	}

	public void setCaught(Entity caught) {
		this.caught = caught;
	}

	/**
	 * Gets the status of fishing
	 *
	 * @return A FishingStatus corresponding with the status of fishing.
	 */
	public FishingStatus getStatus() {
		return status;
	}

	public void setStatus(FishingStatus status) {
		this.status = status;
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

	/**
	 * An enum to specify the state of the fishing
	 */
	public enum FishingStatus {
		/**
		 * When a player is fishing
		 */
		FISHING,
		/**
		 * When a player has successfully caught a fish
		 */
		CAUGHT_FISH,
		/**
		 * When a player has successfully caught an entity
		 */
		CAUGHT_ENTITY,
		/**
		 * When a bobber is stuck in the ground
		 */
		IN_GROUND,
		/**
		 * When a player fails to catch anything while fishing usually due to
		 * poor aiming or timing
		 */
		FAILED_ATTEMPT,
	}

}
