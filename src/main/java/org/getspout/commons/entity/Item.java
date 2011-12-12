/*
 * This file is part of Bukkit (http://bukkit.org/).
 * 
 * Bukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.entity;

import org.getspout.commons.inventory.ItemStack;

public interface Item extends Entity {

	/**
	 * Gets the item stack associated with this item drop.
	 *
	 * @return An item stack.
	 */
	public ItemStack getItemStack();

	/**
	 * Sets the item stack associated with this item drop.
	 *
	 * @param stack An item stack.
	 */
	public void setItemStack(ItemStack stack);

	/**
	 * Gets the delay before this Item is available to be picked up by players
	 *
	 * @return Remaining delay
	 */
	public int getPickupDelay();

	/**
	 * Sets the delay before this Item is available to be picked up by players
	 *
	 * @param delay New delay
	 */
	public void setPickupDelay(int delay);
}
