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
package org.getspout.unchecked.api.event.entity;

import org.getspout.unchecked.api.event.Cancellable;
import org.getspout.unchecked.api.event.HandlerList;

/**
 * Called when an entity is damaged.
 */
public class EntityDamageEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private int damage;

	private DamageCause cause;

	/**
	 * Gets the damage amount
	 *
	 * @return The damage amount
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * Sets the damage amount
	 *
	 * @param damage The damage amount to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/**
	 * Gets the cause of the damage.
	 *
	 * @return A DamageCause value detailing the cause of the damage.
	 */
	public DamageCause getCause() {
		return cause;
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
	 * Specifies the cause of damage.
	 */
	public enum DamageCause {
		CONTACT,
		ENTITY_ATTACK,
		PROJECTILE,
		SUFFOCATION,
		FALL,
		FIRE,
		FIRE_TICK,
		LAVA,
		DROWNING,
		BLOCK_EXPLOSION,
		ENTITY_EXPLOSION,
		VOID,
		LIGHTNING,
		SUICIDE,
		STARVATION,
		CUSTOM

	}

}
