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

import java.util.ArrayList;
import java.util.List;
import org.spout.api.material.Material;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialContainer;
import org.spout.api.material.source.MaterialData;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.util.LogicUtil;
import org.spout.nbt.Tag;

/**
 * Represents a stack of items
 */
public class ItemStack implements MaterialContainer {
	private Material material;
	private int amount;
	private short data;
	private List<Tag> auxData;

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
		this.setMaterial(material);
		this.amount = amount;
		this.data = data;
	}

	/**
	 * Gets the Material of the stack
	 * 
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Sets the Material for the stack
	 * 
	 * @param material the material
	 */
	public ItemStack setMaterial(Material material) {
		this.material = material;
		return this;
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
	 * returns a copy of the map containing the aux data for this stack
	 * 
	 * @return the aux data
	 */
	public List<Tag> getAuxData() {
		if (auxData == null) {
			return null;
		} else {
			return new ArrayList<Tag>(auxData);
		}
	}

	/**
	 * Sets the aux data for this stack
	 * 
	 * @return the item stack
	 */
	public ItemStack setAuxData(List<Tag> auxData) {
		if (auxData == null) {
			this.auxData = null;
		} else {
			this.auxData = new ArrayList<Tag>(auxData);
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
	public void setMaterial(MaterialSource material) {
		Material mat = material == null ? null : material.getMaterial();
		if (mat == null) {
			throw new IllegalArgumentException("Material can not be null!");
		} else {
			this.material = material.getMaterial();
		}
	}

	@Override
	public void setMaterial(MaterialSource material, DataSource datasource) {
		this.setMaterial(material, datasource.getData());
	}

	@Override
	public void setMaterial(MaterialSource material, short data) {
		this.setMaterial(material);
		this.data = data;
	}

	@Override
	public void setData(DataSource datasource) {
		this.setData(datasource.getData());
	}

	@Override
	public void setData(short data) {
		this.data = data;
	}

	@Override
	public short getData() {
		return this.data;
	}

	@Override
	public MaterialData createData() {
		return this.material.createData(this.data);
	}
}
