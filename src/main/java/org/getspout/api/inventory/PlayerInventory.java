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
package org.getspout.api.inventory;

/**
 * Includes interface to the 4 armor slots
 */
public interface PlayerInventory extends Inventory {

	/**
	 * Get all ItemStacks from the armor slots
	 *
	 * @return All the ItemStacks from the armor slots
	 */
	public ItemStack[] getArmorContents();

	/**
	 * Return the ItemStack from the helmet slot
	 *
	 * @return The ItemStack in the helmet slot
	 */
	public ItemStack getHelmet();

	/**
	 * Return the ItemStack from the chestplate slot
	 *
	 * @return The ItemStack in the chestplate slot
	 */
	public ItemStack getChestplate();

	/**
	 * Return the ItemStack from the leg slot
	 *
	 * @return The ItemStack in the leg slot
	 */
	public ItemStack getLeggings();

	/**
	 * Return the ItemStack from the boots slot
	 *
	 * @return The ItemStack in the boots slot
	 */
	public ItemStack getBoots();

	/**
	 * Put the given ItemStacks into the armor slots
	 *
	 *  @param items The ItemStacks to use as armour
	 */
	public void setArmorContents(ItemStack[] items);

	/**
	 * Put the given ItemStack into the helmet slot
	 * This does not check if the ItemStack is a helmet
	 *
	 * @param helmet The ItemStack to use as helmet
	 */
	public void setHelmet(ItemStack helmet);

	/**
	 * Put the given ItemStack into the chestplate slot
	 * This does not check if the ItemStack is a chestplate
	 *
	 * @param chestplate The ItemStack to use as chestplate
	 */
	public void setChestplate(ItemStack chestplate);

	/**
	 * Put the given ItemStack into the leg slot
	 * This does not check if the ItemStack is a pair of leggings
	 *
	 * @param leggings The ItemStack to use as leggings
	 */
	public void setLeggings(ItemStack leggings);

	/**
	 * Put the given ItemStack into the boots slot
	 * This does not check if the ItemStack is a boots
	 *
	 * @param boots The ItemStack to use as boots
	 */
	public void setBoots(ItemStack boots);

	/**
	 * Returns the ItemStack currently hold
	 *
	 * @return The currently held ItemStack
	 */
	public ItemStack getItemInHand();

	/**
	 * Sets the item in hand
	 *
	 * @param stack Stack to set
	 */
	public void setItemInHand(ItemStack stack);

	/**
	 * Get the slot number of the currently held item
	 *
	 * @return Held item slot number
	 */
	public int getHeldItemSlot();
}
