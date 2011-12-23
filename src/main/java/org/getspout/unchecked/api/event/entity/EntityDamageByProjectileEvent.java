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

import org.getspout.api.entity.Entity;

/**
 * Called when an entity is damaged by a projectile.
 */
public class EntityDamageByProjectileEvent extends EntityDamageByEntityEvent {
	/**
	 * Gets the projectile that damaged the entity.
	 *
	 * @return The projectile that damaged the entity.
	 */

	@Override
	public Entity getDamager() {
		return (Entity) super.getDamager();
	}

}
