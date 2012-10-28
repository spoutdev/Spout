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
package org.spout.api.inventory.recipe;

import org.junit.Test;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShapedRecipeTest {
	/*@Test
	public void testShapedRecipe() {

		final ItemStack solid = new ItemStack(BlockMaterial.SOLID, 5);
		final ShapedRecipe recipe = new ShapedRecipe(new ItemStack(BlockMaterial.UNBREAKABLE, 1));

		// Add rows
		recipe.addRow(' ', 'X', ' ');
		recipe.addRow('X', 'X', 'X');
		recipe.addRow(' ', 'X', ' ');
		recipe.setIngredient('X', solid.clone());

		// Create inventory to test
		Inventory inventory = new Inventory(9);

		// Make sure we can't craft yet
		assertFalse(recipe.handle(inventory));

		// Add ingredients
		inventory.set(1, solid.clone());
		inventory.set(3, solid.clone());
		inventory.set(4, solid.clone());
		inventory.set(5, solid.clone());
		inventory.set(7, solid.clone());

		// Try to craft
		assertTrue(recipe.handle(inventory));
	}*/
}
