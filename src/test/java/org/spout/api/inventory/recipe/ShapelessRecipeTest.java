/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import org.spout.api.EngineFaker;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.spout.api.material.Material;

public class ShapelessRecipeTest {
	@Before
	public void setupMaterials() {
		EngineFaker.setupEngine();
	}

	@Test
	public void testShapelessRecipe() {
		ItemStack solid = new ItemStack(BlockMaterial.SOLID, 1);
		RecipeBuilder builder = new RecipeBuilder().setResult(solid).addIngredient(BlockMaterial.UNBREAKABLE);
		ShapelessRecipe recipe = builder.buildShapelessRecipe();
		List<Material> single = Arrays.asList((Material) BlockMaterial.UNBREAKABLE);
		List<Material> multiple = Arrays.asList((Material) BlockMaterial.UNBREAKABLE, (Material) BlockMaterial.UNBREAKABLE);
		SimpleRecipeManager manager = new SimpleRecipeManager();
		manager.register(recipe);
		assertNotNull(manager.matchShapelessRecipe(single));
		assertNull(manager.matchShapelessRecipe(multiple));
	}
}
