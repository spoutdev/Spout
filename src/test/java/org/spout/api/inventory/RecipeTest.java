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
package org.spout.api.inventory;

import org.junit.Test;

import org.spout.api.EngineFaker;
import org.spout.api.inventory.recipe.ShapedRecipe;
import org.spout.api.inventory.recipe.ShapedRecipeBuilder;
import org.spout.api.inventory.recipe.ShapelessRecipe;
import org.spout.api.inventory.recipe.SimpleRecipeManager;
import org.spout.api.material.BlockMaterial;
import org.spout.api.plugin.CommonPlugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipeTest {
	private final SimpleRecipeManager manager = new SimpleRecipeManager();
	private final FakePlugin plugin = new FakePlugin();

	static {
		EngineFaker.setupEngine();
	}

	@Test
	public void testShapedRecipe() {
		ShapedRecipeBuilder builder = new ShapedRecipeBuilder();
		ItemStack product = new ItemStack(BlockMaterial.SOLID, 5);

		builder.setProduct(product.clone());
		builder.setIngredient('A', BlockMaterial.SOLID);
		builder.setIngredient('B', BlockMaterial.SOLID_BROWN);
		builder.setIngredient('C', BlockMaterial.SOLID_GREEN);
		builder.setShape("AAA", "B B", "CCC");
		ShapedRecipe recipe1 = builder.build();
		manager.register(recipe1, plugin);

		builder.setProduct(product.clone());
		builder.setIngredient('X', BlockMaterial.SOLID);
		builder.setIngredient('Y', BlockMaterial.SOLID_BROWN);
		builder.setIngredient('Z', BlockMaterial.SOLID_GREEN);
		builder.setShape("XXX", "Y Y", "ZZZ");
		ShapedRecipe recipe2 = builder.build();

		assertTrue(recipe2.equals(recipe1));
		assertTrue(manager.contains(recipe2));
		assertTrue(manager.contains(plugin, recipe2));

		builder.setShape("ZZZ", "Y Y", "XXX");
		ShapedRecipe recipe3 = builder.build();

		assertFalse(recipe3.equals(recipe1));
		assertFalse(manager.contains(recipe3));
		assertFalse(manager.contains(plugin, recipe3));

		manager.remove(recipe1);
		assertFalse(manager.contains(recipe1));
	}

	@Test
	public void testShapelessRecipe() {
		manager.clear();
		ItemStack product = new ItemStack(BlockMaterial.SOLID, 5);
		ShapelessRecipe recipe1 = new ShapelessRecipe(product.clone(), BlockMaterial.SOLID, BlockMaterial.SOLID_BROWN, BlockMaterial.SOLID_GREEN);
		ShapelessRecipe recipe2 = new ShapelessRecipe(product.clone(), BlockMaterial.SOLID_GREEN, BlockMaterial.SOLID_BROWN, BlockMaterial.SOLID);
		manager.register(recipe1, plugin);

		assertTrue(recipe2.equals(recipe1));
		assertTrue(manager.contains(recipe2));
		recipe2 = new ShapelessRecipe(product.clone(), BlockMaterial.SOLID);
		assertFalse(recipe2.equals(recipe1));
		assertFalse(manager.contains(recipe2));
	}

	private static class FakePlugin extends CommonPlugin  {
		@Override
		public void onEnable() {
		}

		@Override
		public void onDisable() {
		}
	}
}
