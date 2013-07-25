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
package org.spout.api.inventory.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.spout.api.faker.EngineFaker;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShapedRecipeTest {
	@Before
	public void setupMaterials() {
		EngineFaker.setupEngine();
	}

	@Test
	public void testShapedRecipe() {
		RecipeBuilder builder = new RecipeBuilder();

		// Add rows
		builder.addRow(' ', 'X', ' ');
		builder.addRow('X', 'X', 'X');
		builder.addRow(' ', 'X', ' ');
		builder.setIngredient('X', BlockMaterial.SOLID_BLUE);
		builder.setResult(new ItemStack(BlockMaterial.AIR, 1));

		final ShapedRecipe recipe = builder.buildShapedRecipe();
		SimpleRecipeManager manager = new SimpleRecipeManager();
		manager.register(recipe);

		List<List<Material>> materials = new ArrayList<List<Material>>();

		// Make sure we can't craft yet
		assertFalse(manager.matchShapedRecipe(materials) != null);

		// Add ingredients
		materials.add(Arrays.asList(new Material[] {null, BlockMaterial.SOLID_BLUE, null}));
		materials.add(Arrays.asList(new Material[] {BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE}));
		materials.add(Arrays.asList(new Material[] {null, BlockMaterial.SOLID_BLUE, null}));

		// Try to craft
		assertTrue(manager.matchShapedRecipe(materials) != null);
	}
}
