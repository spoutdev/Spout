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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spout.api.inventory.ItemStack;
import org.spout.api.plugin.Plugin;

public class SimpleRecipeManager implements RecipeManager {
	private final Map<Plugin, Set<Recipe>> recipeMap = new HashMap<Plugin, Set<Recipe>>();

	@Override
	public boolean register(Recipe recipe, Plugin plugin) {
		if (!recipeMap.containsKey(plugin)) {
			recipeMap.put(plugin, new HashSet<Recipe>());
		}
		return recipeMap.get(plugin).add(recipe);
	}

	@Override
	public void remove(Recipe recipe) {
		for (Plugin plugin : recipeMap.keySet()) {
			if (recipeMap.get(plugin) != null) {
				recipeMap.get(plugin).remove(recipe);
			}
		}
	}

	@Override
	public void clear(Plugin plugin) {
		recipeMap.put(plugin, null);
	}

	@Override
	public void clear() {
		recipeMap.clear();
	}

	@Override
	public Set<Recipe> getRecipes(Plugin plugin) {
		return recipeMap.get(plugin);
	}

	@Override
	public Set<Recipe> getRecipes() {
		Set<Recipe> recipes = new HashSet<Recipe>();
		for (Set<Recipe> r : recipeMap.values()) {
			if (r != null) {
				recipes.addAll(r);
			}
		}
		return recipes;
	}

	@Override
	public boolean contains(Plugin plugin, Recipe recipe) {
		Set<Recipe> recipes = getRecipes(plugin);
		if (recipes == null) {
			return false;
		}
		for (Recipe r : recipes) {
			if (r.equals(recipe)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(Recipe recipe) {
		for (Recipe r : getRecipes()) {
			if (r.equals(recipe)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getProduct(Plugin plugin, Object regents) {
		Set<Recipe> recipes = getRecipes(plugin);
		if (recipes == null) {
			return null;
		}
		for (Recipe r : recipes) {
			if (r.getRegents().equals(regents)) {
				return r.getProduct();
			}
		}
		return null;
	}

	@Override
	public ItemStack getProduct(Object regents) {
		for (Recipe recipe : getRecipes()) {
			if (recipe.getRegents().equals(regents)) {
				return recipe.getProduct();
			}
		}
		return null;
	}
}
