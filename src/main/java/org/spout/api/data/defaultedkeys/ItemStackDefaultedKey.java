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
package org.spout.api.data.defaultedkeys;

import org.spout.api.inventory.ItemStack;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.material.Material;

public class ItemStackDefaultedKey extends DefaultedKeyImpl<ItemStack>{

	private final Material material;
	private final int data;
	private final int amount;
	
	public ItemStackDefaultedKey(String keyString, Material material, int amount) {
		this(keyString, material, material.getData(), amount);
	}
	
	public ItemStackDefaultedKey(String keyString, Material material, int data, int amount) {
		super(keyString, null);
		this.material = material;
		this.data = data;
		this.amount = amount;
	}
	
	@Override
	public ItemStack getDefaultValue() {
		return new ItemStack(material, data, amount);
	}
	
}

