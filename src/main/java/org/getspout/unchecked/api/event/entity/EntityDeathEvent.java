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

import java.util.List;

import org.getspout.api.event.HandlerList;
import org.getspout.unchecked.api.inventory.ItemStack;

/**
 * Called when an entity dies.
 */
public class EntityDeathEvent extends EntityEvent {
	private static HandlerList handlers = new HandlerList();

	private int dropExp;

	private List<ItemStack> drops;

	/**
	 * Gets the amount of experience to drop.
	 *
	 * @return The amount of experience to drop.
	 */
	public int getDropExp() {
		return dropExp;
	}

	/**
	 * Sets the amount of experience to drop.
	 *
	 * @param dropExp The experience to set.
	 */
	public void setDropExp(int dropExp) {
		this.dropExp = dropExp;
	}

	/**
	 * The drops to drop.
	 *
	 * @return The drops to drop.
	 */
	public List<ItemStack> getDrops() {
		return drops;
	}

	/**
	 * Sets the drops to drop.
	 *
	 * @param drops The drops to set.
	 */
	public void setDrops(List<ItemStack> drops) {
		this.drops = drops;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
