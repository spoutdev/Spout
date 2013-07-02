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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.spout.api.EngineFaker;
import org.spout.api.material.Material;

import static org.junit.Assert.assertTrue;
import org.spout.api.material.BlockMaterial;

public class RecipeBuilderTest {
	@Before
	public void setupMaterials() {
		EngineFaker.setupEngine();
	}

	@Test
	public void singleIngredientsTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.addIngredient(BlockMaterial.SOLID).addIngredient(BlockMaterial.AIR);
		builder.addIngredient(BlockMaterial.ERROR, 2);
		builder.setResult(BlockMaterial.UNBREAKABLE, 1);
		ShapelessRecipe recipe = builder.buildShapelessRecipe();
		List<Material> materials = new ArrayList<Material>();
		materials.add(BlockMaterial.SOLID);
		materials.add(BlockMaterial.AIR);
		materials.add(BlockMaterial.ERROR);
		materials.add(BlockMaterial.ERROR);
		for (Material m : recipe.getIngredients()) {
			assertTrue(materials.remove(m));
		}
		assertTrue(materials.isEmpty());
	}

	@Test
	public void characterIngredientsTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.addIngredient(BlockMaterial.SOLID).addIngredient(BlockMaterial.UNBREAKABLE);
		builder.addIngredient(BlockMaterial.SKYBOX);
		builder.setResult(BlockMaterial.SOLID, 1);
		ShapelessRecipe recipe = builder.buildShapelessRecipe();
		List<Material> testIngredients = new ArrayList<Material>();
		List<Material> recipeIngredients = new ArrayList<Material>();
		recipeIngredients.addAll(recipe.getIngredients());
		testIngredients.add(BlockMaterial.SOLID);
		testIngredients.add(BlockMaterial.UNBREAKABLE);
		testIngredients.add(BlockMaterial.SKYBOX);
		recipeIngredients.removeAll(testIngredients);
		testIngredients.removeAll(recipe.getIngredients());
		assertTrue(testIngredients.isEmpty());
		assertTrue(recipeIngredients.isEmpty());
	}

	@Test
	public void overwritingMaterialsTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.setIngredient('B', BlockMaterial.SKYBOX).setIngredient('A', BlockMaterial.AIR);
		builder.setResult(BlockMaterial.SOLID, 1);
		builder.addRow("AB");
		ShapedRecipe recipe = builder.buildShapedRecipe();
		List<Material> testIngredients = new ArrayList<Material>();
		List<Material> recipeIngredients = new ArrayList<Material>();
		recipeIngredients.addAll(recipe.getIngredients());
		testIngredients.add(BlockMaterial.SKYBOX);
		testIngredients.add(BlockMaterial.AIR);
		recipeIngredients.removeAll(testIngredients);
		testIngredients.removeAll(recipe.getIngredients());
		assertTrue(testIngredients.isEmpty());
		assertTrue(recipeIngredients.isEmpty());
	}

	@Test
	public void shapedAmountsTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("AAA").addRow("BBB").addRow("AAA");
		builder.setResult(BlockMaterial.SOLID, 1);
		ShapedRecipe recipe = builder.buildShapedRecipe();
		List<Material> testIngredients = new ArrayList<Material>();
		List<Material> recipeIngredients = new ArrayList<Material>();
		recipeIngredients.addAll(recipe.getIngredients());
		testIngredients.add(BlockMaterial.SOLID);
		testIngredients.add(BlockMaterial.UNBREAKABLE);
		recipeIngredients.removeAll(testIngredients);
		testIngredients.removeAll(recipe.getIngredients());
		assertTrue(testIngredients.isEmpty());
		assertTrue(recipeIngredients.isEmpty());
	}

	@Test
	public void rowsTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("AAA").addRow("BBB").addRow("AAA");
		builder.setResult(BlockMaterial.SOLID, 1);
		ShapedRecipe recipe = builder.buildShapedRecipe();
		List<List<Character>> rows = recipe.getRows();
		assertTrue(rows.get(0).get(0) == 'A');
		assertTrue(rows.get(0).get(1) == 'A');
		assertTrue(rows.get(0).get(2) == 'A');
		assertTrue(rows.get(1).get(0) == 'B');
		assertTrue(rows.get(1).get(1) == 'B');
		assertTrue(rows.get(1).get(2) == 'B');
		assertTrue(rows.get(2).get(0) == 'A');
		assertTrue(rows.get(2).get(1) == 'A');
		assertTrue(rows.get(2).get(2) == 'A');
	}

	@Test
	public void replaceRowsTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("AAA").addRow("BBB").addRow("AAA").replaceRow(1, "CCC");
		builder.setResult(BlockMaterial.SOLID, 1);
		ShapedRecipe recipe = builder.buildShapedRecipe();
		List<List<Character>> rows = recipe.getRows();
		assertTrue(rows.get(0).get(0) == 'A');
		assertTrue(rows.get(0).get(1) == 'A');
		assertTrue(rows.get(0).get(2) == 'A');
		assertTrue(rows.get(1).get(0) == 'C');
		assertTrue(rows.get(1).get(1) == 'C');
		assertTrue(rows.get(1).get(2) == 'C');
		assertTrue(rows.get(2).get(0) == 'A');
		assertTrue(rows.get(2).get(1) == 'A');
		assertTrue(rows.get(2).get(2) == 'A');
	}
	/* TODO this test needs items with data
	@Test
	public void dataTest() {
		RecipeBuilder builder = new RecipeBuilder();
		SimpleRecipeManager manager = new SimpleRecipeManager();
		builder.addIngredient('A', Plank.BIRCH).addIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("AAA").addRow("BBB").addRow("AAA");
		builder.setIncludeData(true);
		builder.setResult(BlockMaterial.SOLID, 1);
		ShapedRecipe withData = builder.buildShapedRecipe();
		assertNotNull(withData);
		manager.addRecipe(withData);
		List<List<Material>> rows = new ArrayList<List<Material>>();
		rows.add(new ArrayList<Material>(Arrays.asList(Plank.PLANK, Plank.PLANK, Plank.PLANK)));
		rows.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE)));
		rows.add(new ArrayList<Material>(Arrays.asList(Plank.PLANK, Plank.PLANK, Plank.PLANK)));
		List<List<Material>> rows2 = new ArrayList<List<Material>>();
		rows2.add(new ArrayList<Material>(Arrays.asList(Plank.PLANK, Plank.PLANK, Plank.PLANK)));
		rows2.add(new ArrayList<Material>(Arrays.asList(BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE, BlockMaterial.UNBREAKABLE)));
		rows2.add(new ArrayList<Material>(Arrays.asList(Plank.PLANK, Plank.PLANK, Plank.PLANK)));
		assertNull(manager.matchShapedRecipe(rows));
		builder.setIncludeData(false);
		ShapedRecipe noData = builder.buildShapedRecipe();
		assertNotNull(noData);
		assertTrue(manager.addRecipe(noData));
		assertSame(noData, manager.matchShapedRecipe(rows));
	}
	*/

	@Test
	public void cloneTest() {
		RecipeBuilder builder = new RecipeBuilder();
		builder.setIngredient('A', BlockMaterial.SOLID).setIngredient('B', BlockMaterial.UNBREAKABLE);
		builder.addRow("AAA").addRow("BBB").addRow("AAA");
		builder.setResult(BlockMaterial.SOLID, 1);
		ShapedRecipe recipe1 = builder.buildShapedRecipe();
		RecipeBuilder builder2 = new RecipeBuilder();
		builder2.clone(recipe1);
		builder2.replaceRow(1, "CCC");
		ShapedRecipe recipe2 = builder2.buildShapedRecipe();
		assertTrue(!recipe1.equals(recipe2));
		RecipeBuilder builder3 = new RecipeBuilder();
		builder3.setIngredient('A', BlockMaterial.SOLID).setIngredient('C', BlockMaterial.UNBREAKABLE);
		builder3.addRow("AAA").addRow("CCC").addRow("AAA");
		builder3.setResult(BlockMaterial.SOLID, 1);
		ShapedRecipe recipe3 = builder3.buildShapedRecipe();
		assertTrue(recipe2.equals(recipe3));
	}
}
