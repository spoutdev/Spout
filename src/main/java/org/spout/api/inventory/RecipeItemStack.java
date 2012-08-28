/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import org.spout.api.datatable.DataMap;
import org.spout.api.material.Material;

/**
 * This is a version of the item stack for use with recipes.<br>
 * <br>
 * The hashcode and equals methods have been overriden.<br>
 * <br>
 * An item stack is considered to equal to another one if the material's id and data matches.<br>
 * <br>
 * It ignores data bits outside the datamask, any aux data, nbt data or the amount.<br>
 * <br>
 * This is intended only for storing recipe item stacks.
 */
public class RecipeItemStack extends ItemStack {

	private static final long serialVersionUID = 1L;

	private final int hashcode;

	public RecipeItemStack(ItemStack stack) {
		this(stack.getMaterial(), stack.getData(), stack.getAmount(), (DataMap) stack.getAuxData());
	}

	public RecipeItemStack(Material material, short data, int amount, DataMap auxData) {
		super(material, data, amount, auxData);
		short hashId = material.getId();
		short hashData = (short) (material.getData() & material.getDataMask());
		hashcode = hashId << 16 | (hashData & 0xFFFF);
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof RecipeItemStack) {
			RecipeItemStack other = (RecipeItemStack) o;
			return hashcode == other.hashcode;
		} else {
			return false;
		}
	}

}
