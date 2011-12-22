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
package org.getspout.unchecked.api.inventory;

import org.getspout.api.util.nbt.Tag;
import org.getspout.unchecked.api.material.CustomBlockMaterial;
import org.getspout.unchecked.api.material.CustomItemMaterial;
import org.getspout.unchecked.api.material.Material;
import org.getspout.unchecked.api.material.MaterialData;

import java.util.Map;

/**
 * Represents a stack of items
 */
public class ItemStack implements Cloneable {
	private int type;
	private int amount = 0;
	private short durability = 0;
	private Map<String, Tag> nbtData = null;

	public ItemStack(final int type) {
		this(type, 0);
	}

	public ItemStack(final Material type) {
		this(type, 0);
	}

	public ItemStack(final int type, final int amount) {
		this(type, amount, (short) 0);
	}

	public ItemStack(final Material type, final int amount) {
		this(type.getRawId(), amount, (short)type.getRawData());
	}

	public ItemStack(final int type, final int amount, final short damage) {
		this(type, amount, damage, null);
	}

	public ItemStack(final Material type, final int amount, final short damage) {
		this(type.getRawId(), amount, damage);
	}

	public ItemStack(final int type, final int amount, final short damage, final Byte data) {
		this(type, amount, damage, data, null);
	}

	public ItemStack(final int type, final int amount, final short damage, final Byte data, final Map<String, Tag> nbtData) {
		this.type = type;
		this.amount = amount;
		durability = damage;
		if (data != null) {
			durability = data;
		}
		this.nbtData = nbtData;
	}

	public ItemStack(final Material type, final int amount, final short damage, final Byte data) {
		this(type.getRawId(), amount, damage, data);
	}

	public ItemStack(CustomItemMaterial item) {
		this(item.getRawId(), 1, (short) item.getRawData());
	}

	public ItemStack(CustomItemMaterial item, int amount) {
		this(item.getRawId(), amount, (short) item.getRawData());
	}

	public ItemStack(CustomBlockMaterial block) {
		this(block.getBlockItem());
	}

	public ItemStack(CustomBlockMaterial block, int amount) {
		this(block.getBlockItem(), amount);
	}

	/**
	 * Is true if the item is a custom item, not in the vanilla game
	 *
	 * @return true if custom item
	 */
	public boolean isCustomItem() {
		return getType() instanceof CustomItemMaterial;
	}

	/**
	 * Gets the type of this item
	 *
	 * @return Type of the items in this stack
	 */
	public Material getType() {
		return MaterialData.getMaterial(type);
	}

	/**
	 * Sets the type of this item<br />
	 * <br />
	 * Note that in doing so you will reset the MaterialData for this stack
	 *
	 * @param type New type to set the items in this stack to
	 */
	public void setType(Material type) {
		setTypeId(type.getRawId());
	}

	/**
	 * Gets the type id of this item
	 *
	 * @return Type Id of the items in this stack
	 */
	public int getTypeId() {
		return type;
	}

	/**
	 * Sets the type id of this item<br />
	 * <br />
	 * Note that in doing so you will reset the MaterialData for this stack
	 *
	 * @param type New type id to set the items in this stack to
	 */
	public void setTypeId(int type) {
		this.type = type;
	}

	/**
	 * Gets the amount of items in this stack
	 *
	 * @return Amount of items in this stick
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the amount of items in this stack
	 *
	 * @param amount New amount of items in this stack
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Sets the MaterialData for this stack of items
	 *
	 * @param data New MaterialData for this item
	 */
	public void setData(Material data) {
		setTypeId(data.getRawId());
		setDurability((short) data.getRawData());
	}

	/**
	 * Sets the durability of this item
	 *
	 * @param durability Durability of this item
	 */
	public void setDurability(final short durability) {
		this.durability = durability;
	}

	/**
	 * Gets the durability of this item
	 *
	 * @return Durability of this item
	 */
	public short getDurability() {
		return durability;
	}

	/**
	 * Get the maximum stacksize for the material hold in this ItemStack Returns
	 * -1 if it has no idea.
	 *
	 * @return The maximum you can stack this material to.
	 */
	public int getMaxStackSize() {
		return -1;
	}

	public void setNbtData(Map<String, Tag> nbtData) {
		this.nbtData = nbtData;
	}

	public Map<String, Tag> getNbtData() {
		return nbtData;
	}

	@Override
	public String toString() {
		return "ItemStack{" + getType().getName() + " x " + getAmount() + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemStack)) {
			return false;
		}

		ItemStack item = (ItemStack) obj;

		return item.getAmount() == getAmount() && item.getTypeId() == getTypeId();
	}

	@Override
	public ItemStack clone() {
		return new ItemStack(type, amount, durability);
	}

	@Override
	public int hashCode() {
		int hash = 11;

		hash = hash * 19 + 7 * getTypeId(); // Overriding hashCode since equals is overridden, it's just
		hash = hash * 7 + 23 * getAmount(); // too bad these are mutable values... Q_Q
		return hash;
	}
}