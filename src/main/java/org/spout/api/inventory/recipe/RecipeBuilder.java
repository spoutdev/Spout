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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public class RecipeBuilder {
	public Plugin plugin = null;
	public ItemStack result = null;
	public HashMap<Character, Material> ingredientsMap = new HashMap<Character, Material>();
	public List<List<Character>> rows = new ArrayList<List<Character>>();
	public List<Material> ingredients = new ArrayList<Material>();
	public boolean includeData = false;

	public ShapedRecipe buildShapedRecipe() throws IllegalStateException {
		if (result == null) {
			throw new IllegalStateException("Result must be set.");
		}
		if (rows.isEmpty()) {
			throw new IllegalStateException("Must add rows.");
		}
		ArrayList<Material> nullCheck = new ArrayList<Material>(ingredientsMap.values());
		nullCheck.removeAll(Collections.singletonList(null));
		if (nullCheck.isEmpty()) { // Make sure there is at least one ingredient
			throw new IllegalStateException("Must specify the ingredients.");
		}
		if (!includeData) {
			for (Entry<Character, Material> entry : ingredientsMap.entrySet()) {
				Material mat = entry.getValue();
				if (mat != null && mat.isSubMaterial()) {
					entry.setValue(mat.getParentMaterial());
				}
			}
		}
		return new ShapedRecipe(this);
	}

	public ShapelessRecipe buildShapelessRecipe() {
		if (result == null) {
			throw new IllegalStateException("Result must be set.");
		}
		ArrayList<Material> nullCheck = new ArrayList<Material>(ingredients);
		nullCheck.removeAll(Collections.singletonList(null));
		if (nullCheck.isEmpty()) {
			throw new IllegalStateException("Mus add materials.");
		}
		if (!includeData) {
			for (int i = 0; i < ingredients.size(); i++) {
				Material mat = ingredients.get(i);
				if (mat != null && mat.isSubMaterial()) {
					ingredients.remove(i);
					ingredients.add(i, mat.getParentMaterial());
				}
			}
		}
		return new ShapelessRecipe(this);
	}

	public RecipeBuilder addPlugin(Plugin plugin) {
		this.plugin = plugin;
		return this;
	}

	public RecipeBuilder setResult(ItemStack result) {
		this.result = result;
		return this;
	}

	public RecipeBuilder setResult(Material material, int amount) {
		setResult(new ItemStack(material, amount));
		return this;
	}

	/**
	 * Should be used only in recipes that will end up being shaped.
	 * @param symbol character to represent the material
	 * @param ingredient material to use
	 * @return this
	 */
	public RecipeBuilder addIngredient(Character symbol, Material ingredient) {
		if (ingredient == null) {
			return this;
		}
		ingredientsMap.put(symbol, ingredient);
		return this;
	}

	/**
	 * Should be used only in recipes that will end up being shapeless.
	 * @param ingredient ingredient to add
	 * @return this
	 */
	public RecipeBuilder addIngredient(Material ingredient) {
		if (ingredient == null) {
			return this;
		}
		ingredients.add(ingredient);
		return this;
	}

	/**
	 * Should be used only in recipes that will end up being shapeless.
	 * @param ingredient ingredient to add
	 * @param amount amount to add
	 * @return this
	 */
	public RecipeBuilder addIngredient(Material ingredient, int amount) {
		if (ingredient == null) {
			return this;
		}
		for (int i = 0; i < amount; i++) {
			ingredients.add(ingredient);
		}
		return this;
	}

	public RecipeBuilder addRow(List<Character> row) {
		rows.add(row);
		return this;
	}

	public RecipeBuilder addRow(String str) {
		char[] chars = str.toCharArray();
		ArrayList<Character> row = new ArrayList<Character>();
		for (char c : chars) {
			row.add(c);
		}
		if (!row.isEmpty()) {
			addRow(row);
		}
		return this;
	}

	public RecipeBuilder removeRow(int rowNumber) {
		if (rows.size() < rowNumber) {
			return this;
		}
		rows.remove(rowNumber);
		return this;
	}

	public RecipeBuilder replaceRow(int rowNumber, List<Character> newRow) {
		if (rows.size() < rowNumber || newRow == null) {
			return this;
		}
		rows.remove(rowNumber);
		rows.add(rowNumber, newRow);
		return this;
	}

	public RecipeBuilder replaceRow(int rowNumber, String str) {
		if (rows.size() < rowNumber || str == null) {
			return this;
		}
		rows.remove(rowNumber);
		char[] chars = str.toCharArray();
		ArrayList<Character> row = new ArrayList<Character>();
		for (char c : chars) {
			row.add(c);
		}
		if (!row.isEmpty()) {
			rows.add(rowNumber, row);
		}
		return this;
	}

	public RecipeBuilder clone(Recipe recipe) {
		plugin = recipe.getPlugin();
		result = recipe.getResult();
		ingredients = recipe.getIngredients();
		if (recipe instanceof ShapedRecipe) {
			ShapedRecipe shaped = (ShapedRecipe) recipe;
			this.ingredientsMap = shaped.getIngredientsMap();
			rows.addAll(shaped.getRows());
		} else if (recipe instanceof ShapelessRecipe) {
			// if needed
		}
		return this;
	}

	public RecipeBuilder setIncludeData(boolean includeData) {
		this.includeData = includeData;
		return this;
	}
}