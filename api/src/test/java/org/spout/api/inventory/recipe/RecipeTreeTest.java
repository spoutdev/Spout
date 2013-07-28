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
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;

import static org.junit.Assert.assertSame;

public class RecipeTreeTest {
	@Before
	public void setupMaterials() {
		EngineFaker.setupEngine();
	}

	@Test
	public void treeTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID_BLUE).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("AAA").addRow("BBB").addRow("AAA");
		builder.setResult(BlockMaterial.SOLID_BLUE, 1);
		ShapedRecipe recipe = builder.buildShapedRecipe();
		List<List<Material>> testIngredients = new ArrayList<>();
		testIngredients.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE)));
		testIngredients.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE)));
		testIngredients.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE)));
		RecipeTree tree = new RecipeTree();
		tree.addRecipe(recipe);
		assertSame(recipe, tree.matchShapedRecipe(testIngredients, true));
	}

	@Test
	public void treeTest2() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID_BLUE).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("BB").addRow("AA");
		builder.setResult(BlockMaterial.SOLID_BLUE, 1);
		ShapedRecipe recipe = builder.buildShapedRecipe();
		List<List<Material>> testIngredients = new ArrayList<>();
		testIngredients.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE)));
		testIngredients.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE)));
		RecipeTree tree = new RecipeTree();
		tree.addRecipe(recipe);
		assertSame(recipe, tree.matchShapedRecipe(testIngredients, true));
	}

	@Test
	public void treeTest3() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID_BLUE).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("BB").addRow("AA");
		builder.setResult(BlockMaterial.SOLID_BLUE, 1);
		ShapedRecipe recipe = builder.buildShapedRecipe();
		List<List<Material>> testIngredients = new ArrayList<>();
		testIngredients.add(new ArrayList<>(Arrays.asList(null, null, (Material) null)));
		testIngredients.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE, null)));
		testIngredients.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.SOLID_BLUE, BlockMaterial.SOLID_BLUE, null)));
		RecipeTree tree = new RecipeTree();
		tree.addRecipe(recipe);
		assertSame(recipe, tree.matchShapedRecipe(testIngredients, true));
	}
}
