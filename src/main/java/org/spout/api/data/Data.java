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
package org.spout.api.data;

import org.spout.api.inventory.ItemStack;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;

/**
 * Common default data mappings one could expect each game to have. These are used instead of strings when retrieving values
 * from an object that holds a datatable. IF you wish to provide your own keys for a plugin you are constructing, simply make a class
 * and construct keys like the below.
 */
public class Data {
	public static final DefaultedKey<ItemStack> HELD_ITEM = new DefaultedKeyImpl<ItemStack>("held_item", new ItemStack(BlockMaterial.AIR, 1));
	public static final DefaultedKey<String> HELD_MATERIAL_NAME = new DefaultedKeyImpl<String>("held_material_name", BlockMaterial.AIR.getName());
	public static final DefaultedKey<String> NAME = new DefaultedKeyImpl<String>("name", "");
	public static final DefaultedKey<Vector3> VELOCITY = new DefaultedKeyImpl<Vector3>("velocity", Vector3.ZERO);
}
