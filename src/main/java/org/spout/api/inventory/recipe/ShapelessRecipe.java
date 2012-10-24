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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;

public class ShapelessRecipe extends Recipe {
	private final List<ItemStack> ingredients = new ArrayList<ItemStack>();

	public ShapelessRecipe(ItemStack result) {
		super(result);
	}

	/**
	 * Adds an ingredient required to craft this recipe.
	 *
	 * @param ingredient to add
	 * @return true if the ingredient already was present
	 */
	public boolean addIngredient(ItemStack ingredient) {
		return ingredients.add(ingredient);
	}

	/**
	 * Adds an ingredient required to craft this recipe.
	 *
	 * @param material to add
	 * @param amount of material to add
	 * @return true if the ingredient already was present
	 */
	public boolean addIngredient(Material material, int amount) {
		return addIngredient(new ItemStack(material, amount));
	}

	/**
	 * Adds an ingredient required to craft this recipe.
	 *
	 * @param material
	 * @return true if the ingredient already was present
	 */
	public boolean addIngredient(Material material) {
		return addIngredient(material, 1);
	}

	/**
	 * Adds all specified ingredients.
	 *
	 * @param ingredient
	 * @return true if all ingredients were added.
	 */
	public boolean addIngredients(Collection<ItemStack> ingredient) {
		return ingredients.addAll(ingredient);
	}

	/**
	 * Removes an ingredient that is required to craft this recipe.
	 *
	 * @param ingredient
	 * @return true if ingredient was present
	 */
	public boolean removeIngredient(ItemStack ingredient) {
		return ingredients.remove(ingredient);
	}

	/**
	 * Removes all occurrences of the material
	 *
	 * @param material
	 */
	public void removeIngredient(Material material) {
		for (ItemStack item : ingredients) {
			if (item.getMaterial() == material) {
				ingredients.remove(item);
			}
		}
	}

	@Override
	public List<ItemStack> getIngredients() {
		return Collections.unmodifiableList(ingredients);
	}

	@Override
	public boolean handle(Inventory inventory) {
		for (ItemStack ingredient : ingredients) {
			if (!inventory.contains(ingredient.getMaterial(), ingredient.getAmount())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Recipe clone() {
		ShapelessRecipe clone = new ShapelessRecipe(result);
		clone.addIngredients(ingredients);
		return clone;
	}

	@Override
	public int hashCode() {
		return ingredients.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof ShapelessRecipe && ((ShapelessRecipe) obj).ingredients == ingredients;
	}
}
