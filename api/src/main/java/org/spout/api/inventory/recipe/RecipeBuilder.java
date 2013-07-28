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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public class RecipeBuilder {
	public Plugin plugin = null;
	public ItemStack result = null;
	public Map<Character, Material> ingredientsMap = new HashMap<>();
	public List<List<Character>> rows = new ArrayList<>();
	public List<Material> ingredients = new ArrayList<>();
	public boolean includeData = false;

	public ShapedRecipe buildShapedRecipe() throws IllegalStateException {
		if (result == null) {
			throw new IllegalStateException("Result must be set.");
		}
		if (rows.isEmpty()) {
			throw new IllegalStateException("Must add rows.");
		}
		ArrayList<Material> nullCheck = new ArrayList<>(ingredientsMap.values());
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
		shapelessCheck();
		return new ShapelessRecipe(this);
	}

	public SmeltedRecipe buildSmeltedRecipe() {
		shapelessCheck();
		return new SmeltedRecipe(this);
	}

	private void shapelessCheck() {
		if (result == null) {
			throw new IllegalStateException("Result must be set.");
		}
		ArrayList<Material> nullCheck = new ArrayList<>(ingredients);
		nullCheck.removeAll(Collections.singletonList(null));
		if (nullCheck.isEmpty()) {
			throw new IllegalStateException("Must add materials.");
		}
		if (!includeData) {
			for (int i = 0; i < ingredients.size(); i++) {
				Material mat = ingredients.get(i);
				while (mat != null && mat.isSubMaterial()) {
					mat = mat.getParentMaterial();
					ingredients.set(i, mat);
				}
			}
		}
	}

	public RecipeBuilder setPlugin(Plugin plugin) {
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
	 * Sets the {@link Character} that the {@link ItemStack} ingredient is represented by. Used only by shaped recipes.
	 *
	 * @param c to represent specified ingredient
	 * @param ingredient to be represented by specified character
	 */
	public RecipeBuilder setIngredient(Character c, Material ingredient) {
		if (ingredient == null) {
			return this;
		}
		ingredientsMap.put(c, ingredient);
		return this;
	}

	/**
	 * Sets all {@link Character} in the map to represent all {@link ItemStack} in the map.
	 *
	 * @param chars character map to represent values
	 */
	public RecipeBuilder setIngredients(Map<Character, Material> chars) {
		ingredientsMap.putAll(chars);
		return this;
	}

	/**
	 * Adds an ingredient required to craft this recipe. Used for shapeless recipes.
	 *
	 * @param ingredient to add
	 */
	public RecipeBuilder addIngredient(Material ingredient) {
		if (ingredient == null) {
			return this;
		}
		ingredients.add(ingredient);
		return this;
	}

	/**
	 * Adds an ingredient required to craft this recipe.
	 *
	 * @param material to add
	 * @param amount of material to add
	 */
	public RecipeBuilder addIngredient(Material material, int amount) {
		for (int i = 0; i < amount; i++) {
			addIngredient(material);
		}
		return this;
	}

	/**
	 * Adds all specified ingredients.
	 *
	 * @return true if all ingredients were added.
	 */
	public RecipeBuilder addIngredients(Collection<Material> ingredient) {
		ingredients.addAll(ingredient);
		return this;
	}

	/**
	 * Adds all specified ingredients.
	 */
	public RecipeBuilder addIngredients(Material... i) {
		ingredients.addAll(Arrays.asList(i));
		return this;
	}

	/**
	 * Removes all occurrences of the material
	 */
	public RecipeBuilder removeIngredient(Material material) {
		ingredients.remove(material);
		return this;
	}

	/**
	 * Adds a row to the recipe.
	 *
	 * @param chars characters to represent {@link ItemStack}s
	 */
	public RecipeBuilder addRow(Character... chars) {
		rows.add(Arrays.asList(chars));
		return this;
	}

	public RecipeBuilder addRow(List<Character> row) {
		rows.add(row);
		return this;
	}

	public RecipeBuilder addRow(String str) {
		char[] chars = str.toCharArray();
		ArrayList<Character> row = new ArrayList<>();
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
		ArrayList<Character> row = new ArrayList<>();
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