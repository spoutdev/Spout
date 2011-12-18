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

import org.getspout.api.entity.Entity;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;

/**
 * Called when an entity targets or untargets another entity.
 */
public class EntityTargetEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Entity target;

	private TargetReason reason;

	public TargetReason getReason() {
		return reason;
	}

	public void setReason(TargetReason reason) {
		this.reason = reason;
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	/**
	 * Returns true if the entity has targeted.
	 * 
	 * @return 
	 */
	public boolean isTarget() {
		return reason.isTarget();
	}

	/**
	 * Returns true if the entity has untargeted.
	 * 
	 * @return 
	 */
	public boolean isUntarget() {
		return !reason.isTarget();
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

	public enum TargetReason {
		TARGET_DIED(false),
		CLOSEST_PLAYER(true),
		TARGET_ATTTACKED_ENTITY(true),
		PIG_ZOMBIE_MASSACRE(true),
		FORGOT_TARGET(false),
		OWNER_ATTACKED(true),
		RANDOM_TARGET(true),
		CUSTOM_TARGET(true),
		CUSTOM_UNTARGET(false);

		private boolean target;

		private TargetReason(boolean target) {
			this.target = target;
		}

		public boolean isTarget() {
			return target;
		}

	}

}
