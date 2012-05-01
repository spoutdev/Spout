/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.inventory;

import org.spout.api.material.Material;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialData;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.material.source.MaterialState;
import org.spout.api.util.LogicUtil;
import org.spout.nbt.CompoundMap;

/**
 * Represents a stack of items
 */
public class ItemStack implements MaterialState {
	private Material material;
	private int amount;
	private short data;
	private CompoundMap auxData;

	/**
	 * Creates a new ItemStack from the specified Material of the specified
	 * amount
	 */
	public ItemStack(Material material, int amount) {
		this(material, material.getData(), amount);
	}

	/**
	 * Creates a new ItemStack from the specified Material and data of the
	 * specified amount
	 */
	public ItemStack(Material material, short data, int amount) {
		this.setMaterial(material).setData(data);
		this.amount = amount;
	}

	/**
	 * Gets the Material of the stack
	 * 
	 * @return the material
	 */
	@Override
	public Material getMaterial() {
		return material;
	}
	
	@Override
	public Material getSubMaterial() {
		return this.getMaterial().getSubMaterial(this.getData());
	}

	/**
	 * Gets the amount of the Material contained in the item stack
	 * 
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets amount of the Material contained in the item stack
	 * 
	 * @param amount the amount
	 */
	public ItemStack setAmount(int amount) {
		this.amount = amount;
		return this;
	}
	
	/**
	 * Gets whether this item stack is empty
	 * 
	 * @return whether the amount equals zero
	 */
	public boolean isEmpty() {
		return this.amount == 0;
	}

	/**
	 * returns a copy of the map containing the aux data for this stack
	 * 
	 * @return the aux data
	 */
	public CompoundMap getAuxData() {
		if (auxData == null) {
			return null;
		} else {
			return new CompoundMap(auxData);
		}
	}

	/**
	 * Sets the aux data for this stack
	 * 
	 * @return the item stack
	 */
	public ItemStack setAuxData(CompoundMap auxData) {
		if (auxData == null) {
			this.auxData = null;
		} else {
			this.auxData = new CompoundMap(auxData);
		}
		return this;
	}

	@Override
	public ItemStack clone() {
		ItemStack newStack = new ItemStack(material, data, amount);
		newStack.setAuxData(auxData);
		return newStack;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ItemStack)) {
			return false;
		}
		ItemStack stack = (ItemStack) other;
		return equalsIgnoreSize(stack) && amount == stack.amount;
	}

	public boolean equalsIgnoreSize(ItemStack other) {
		return material.equals(other.material) && data == other.data && LogicUtil.bothNullOrEqual(auxData, other.auxData);
	}

	@Override
	public String toString() {
		return "ItemStack{" + "material=" + material + ",id=" + material.getId() + ",data=" + data + ",amount=" + amount + ",auxData=" + auxData + '}';
	}

	@Override
	public ItemStack setMaterial(MaterialSource material) {
		Material mat = material == null ? null : material.getMaterial();
		if (mat == null) {
			throw new IllegalArgumentException("Material can not be null!");
		} else {
			this.material = material.getMaterial().getRoot();
			this.setData(material);
		}
		return this;
	}

	@Override
	public ItemStack setMaterial(MaterialSource material, DataSource datasource) {
		return this.setMaterial(material, datasource.getData());
	}

	@Override
	public ItemStack setMaterial(MaterialSource material, short data) {
		return this.setMaterial(material).setData(data);
	}

	@Override
	public ItemStack setData(DataSource datasource) {
		return this.setData(datasource.getData());
	}

	@Override
	public ItemStack setData(short data) {
		this.data = data;
		return this;
	}

	@Override
	public short getData() {
		return this.data;
	}

	@Override
	public MaterialData createData() {
		return this.material.createData(this.data);
	}

	
	/**
	 * Gets this item stack limited by the maximum stacking size<br>
	 * The amount of this item stack is set to contain the remaining amount<br>
	 * The amount of the returned stack is set to be this amount or the maximum stacking size<br><br>
	 * 
	 * For example, limiting a stack of amount 120 to a max stacking size of 64 will:
	 * <ul>
	 * <li>Set the amount of this item stack to 56
	 * <li>Return an item stack with amount 64
	 * </ul>
	 * 
	 * @return the limited stack
	 */
	public ItemStack limitStackSize() {
		return this.limitSize(this.getMaxStackSize());
	}
	
	/**
	 * Gets this item stack limited by the maximum size specified<br>
	 * The amount of this item stack is set to contain the remaining amount<br>
	 * The amount of the returned stack is set to be this amount or the maximum amount<br><br>
	 * 
	 * For example, limiting a stack of amount 5 to a max size of 2 will:
	 * <ul>
	 * <li>Set the amount of this item stack to 3
	 * <li>Return an item stack with amount 2
	 * </ul>
	 * 
	 * @return the limited stack
	 */
	public ItemStack limitSize(int maxSize) {
		ItemStack stack = this.clone();
		if (stack.getAmount() <= maxSize) {
			this.setAmount(0);
		} else {
			stack.setAmount(maxSize);
			this.setAmount(this.getAmount() - maxSize);
		}
		return stack;
	}
	
	/**
	 * Tries to stack an item on top of this item<br>
	 * The item must have the same properties as this item stack<br>
	 * The amount of this item is kept below the max stacking size<br><br>
	 * 
	 * The input item amount is affected<br>
	 * If true is returned, this amount is 0, otherwise it is the amount it didn't stack into this item
	 * 
	 * @param item to stack
	 * @return True if stacking was successful, False otherwise
	 */
	public boolean stack(ItemStack item) {
		if (this.equalsIgnoreSize(item)) {
			final int maxsize = this.getMaxStackSize();
			final int combinedSize = this.getAmount() + item.getAmount();
			if (combinedSize <= maxsize) {
				this.setAmount(combinedSize);
				item.setAmount(0);
				return true;
			} else {
				this.setAmount(maxsize);
				item.setAmount(combinedSize - maxsize);
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the maximum size this {@link ItemStack} can be using the material it has
	 * 
	 * @return the max stack size
	 */
	public int getMaxStackSize() {
		return this.getSubMaterial().getMaxStackSize();
	}
}
