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

import org.bukkit.block.Block;

/**
 * Called when an entity is damaged by a block.
 */
public class EntityDamageByBlockEvent extends EntityDamageEvent {
	private Block damager;

	/**
	 * Gets the block that damaged the entity.
	 *
	 * @return The block that damaged the entity
	 */
	public Block getDamager() {
		return damager;
	}

	/**
	 * Sets the block that damaged the entity.
	 *
	 * @param damager The block to set
	 */
	public void setDamager(Block damager) {
		this.damager = damager;
	}
}
