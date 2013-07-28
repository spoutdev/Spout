/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.inventory;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.Material;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.util.LogicUtil;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

/**
 * Represents a stack of items
 */
public class ItemStack implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Material material;
	private short data;
	private int amount;
	private CompoundMap nbtData = null;
	private SerializableMap auxData;

	/**
	 * Creates a new ItemStack from the specified Material of the specified amount
	 */
	public ItemStack(Material material, int amount) {
		this(material, material.getData(), amount);
	}

	/**
	 * Creates a new ItemStack from the specified Material and data of the specified amount
	 */
	public ItemStack(Material material, int data, int amount) {
		this(material, data, amount, null);
	}

	/**
	 * Creates a new ItemStack from the specified Material and data of the specified amount, with the specified aux data
	 */
	public ItemStack(Material material, int data, int amount, SerializableMap auxData) {
		this.material = material;
		this.data = (short) data;
		this.amount = amount;
		if (auxData != null) {
			this.auxData = auxData;
		} else {
			this.auxData = new ManagedHashMap();
		}
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

	public boolean isMaterial(Material... materials) {
		if (this.material == null) {
			for (Material material : materials) {
				if (material == null) {
					return true;
				}
			}
			return false;
		} else {
			return this.material.isMaterial(materials);
		}
	}

	public ItemStack setMaterial(Material material) {
		return setMaterial(material, material.getData());
	}

	public ItemStack setMaterial(Material material, int data) {
		this.material = material;
		this.data = (short) data;
		return this;
	}

	/**
	 * Gets the map containing the aux data for this stack
	 *
	 * @return the aux data
	 */
	public DefaultedMap<Serializable> getAuxData() {
		return auxData;
	}

	/**
	 * returns a copy of the map containing the aux data for this stack
	 *
	 * @return the aux data
	 */
	public CompoundMap getNBTData() {
		if (nbtData == null) {
			return null;
		}

		return new CompoundMap(nbtData);
	}

	/**
	 * Sets the aux data for this stack
	 *
	 * @return the item stack
	 */
	public ItemStack setNBTData(CompoundMap nbtData) {
		if (nbtData == null) {
			this.nbtData = null;
		} else {
			this.nbtData = new CompoundMap(nbtData);
		}
		return this;
	}

	/**
	 * If the item is null or empty, null is returned, otherwise the item is cloned
	 *
	 * @param item to clone
	 * @return null, or the cloned item
	 */
	public static ItemStack cloneSpecial(ItemStack item) {
		return item == null || item.isEmpty() ? null : item.clone();
	}

	@Override
	public ItemStack clone() {
		ItemStack newStack = new ItemStack(material, data, amount, auxData);
		newStack.setNBTData(nbtData);
		return newStack;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(91, 15).append(material).append(auxData).append(nbtData).append(amount).toHashCode();
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
		if (other == null) {
			return false;
		}
		return material.equals(other.material) && data == other.data && auxData.equals(other.auxData) && LogicUtil.bothNullOrEqual(nbtData, other.nbtData);
	}

	@Override
	public String toString() {
		return "ItemStack{" + "material=" + material + ",id=" + material.getId() + ",data=" + data + ",amount=" + amount + ",nbtData=" + nbtData + '}';
	}

	public ItemStack setData(Material datasource) {
		return this.setData(datasource.getData());
	}

	public ItemStack setData(int data) {
		this.data = (short) data;
		this.material = this.material.getRoot().getSubMaterial(this.data);
		return this;
	}

	public short getData() {
		return this.data;
	}

	/**
	 * Gets this item stack limited by the maximum stacking size<br> The amount of this item stack is set to contain the remaining amount<br> The amount of the returned stack is set to be this amount or
	 * the maximum stacking size<br><br> <p> For example, limiting a stack of amount 120 to a max stacking size of 64 will: <ul> <li>Set the amount of this item stack to 56 <li>Return an item stack with
	 * amount 64 </ul>
	 *
	 * @return the limited stack
	 */
	public ItemStack limitStackSize() {
		return this.limitSize(this.getMaxStackSize());
	}

	/**
	 * Gets this item stack limited by the maximum size specified<br> The amount of this item stack is set to contain the remaining amount<br> The amount of the returned stack is set to be this amount or
	 * the maximum amount<br><br> <p> For example, limiting a stack of amount 5 to a max size of 2 will: <ul> <li>Set the amount of this item stack to 3 <li>Return an item stack with amount 2 </ul>
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
	 * Tries to stack an item on top of this item<br> The item must have the same properties as this item stack<br> The amount of this item is kept below the max stacking size<br><br> <p> The input item
	 * amount is affected<br> If true is returned, this amount is 0, otherwise it is the amount it didn't stack into this item
	 *
	 * @param item to stack
	 * @return True if stacking was successful, False otherwise
	 */
	public boolean stack(ItemStack item) {
		if (!this.equalsIgnoreSize(item)) {
			return false;
		}

		final int maxsize = this.getMaxStackSize();
		final int combinedSize = this.getAmount() + item.getAmount();
		if (combinedSize > maxsize) {
			this.setAmount(maxsize);
			item.setAmount(combinedSize - maxsize);
			return false;
		}

		this.setAmount(combinedSize);
		item.setAmount(0);
		return true;
	}

	/**
	 * Gets the maximum size this {@link ItemStack} can be using the material it has
	 *
	 * @return the max stack size
	 */
	public int getMaxStackSize() {
		if (!auxData.isEmpty() || (nbtData != null && !nbtData.isEmpty())) {
			return 1;
		}
		return this.getMaterial().getMaxStackSize();
	}

	/**
	 * Gets the maximum data this {@link ItemStack} can have using the material it has
	 *
	 * @return the max data
	 */
	public short getMaxData() {
		return this.getMaterial().getMaxData();
	}

	//Custom serialization logic because material & auxData can not be made
	// serializable
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeShort(material.getId());
		out.writeShort(material.getData());
		out.writeInt(amount);
		out.writeShort(data);
		byte[] auxData = this.auxData.serialize();
		if (auxData != null) {
			out.writeInt(auxData.length);
			out.write(auxData);
		} else {
			out.writeInt(0);
		}

		if (nbtData != null && !nbtData.isEmpty()) {
			out.writeBoolean(true);
			try (NBTOutputStream os = new NBTOutputStream(out, false)) {
				os.writeTag(new CompoundTag("nbtData", nbtData));
			}
		} else {
			out.writeBoolean(false);
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		short matId = in.readShort();
		short matData = in.readShort();
		material = MaterialRegistry.get(matId);
		if (matData != 0 && material != null) {
			material = material.getSubMaterial(matData);
		}
		amount = in.readInt();
		data = in.readShort();
		int auxDataSize = in.readInt();
		if (auxDataSize > 0) {
			byte[] auxData = new byte[auxDataSize];
			in.read(auxData);
			ManagedHashMap map = new ManagedHashMap();
			map.deserialize(auxData);
			this.auxData = map;
		}

		boolean hasNBTData = in.readBoolean();
		if (hasNBTData) {
			try (NBTInputStream is = new NBTInputStream(in, false)) {
				CompoundTag tag = (CompoundTag) is.readTag();
				nbtData = tag.getValue();
			}
		}

		if (material == null) {
			throw new ClassNotFoundException("No material matching {" + matId + ", " + matData + "} was found!");
		}
	}
}
