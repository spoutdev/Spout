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

import org.getspout.unchecked.api.entity.Entity;

/**
 * Called when an entity is damaged by another entity.
 */
public class EntityDamageByEntityEvent extends EntityDamageEvent {
	private Entity damager;

	/**
	 * Gets the entity that damaged the entity.
	 *
	 * @return The entity that damaged the entity.
	 */
	public Entity getDamager() {
		return damager;
	}

	/**
	 * Sets the entity that damaged the entity.
	 *
	 * @param damager The entity to set
	 */
	public void setDamager(Entity damager) {
		this.damager = damager;
	}

}
