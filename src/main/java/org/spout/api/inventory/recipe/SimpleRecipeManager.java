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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.plugin.Plugin;

public class SimpleRecipeManager implements RecipeManager {
	private final Map<Plugin, Set<Recipe>> recipeMap = new HashMap<Plugin, Set<Recipe>>();

	@Override
	public boolean register(Plugin plugin, Recipe recipe) {
		if (!recipeMap.containsKey(plugin)) {
			recipeMap.put(plugin, new HashSet<Recipe>());
		}
		return recipeMap.get(plugin).add(recipe);
	}

	@Override
	public boolean registerAll(Plugin plugin, Set<Recipe> recipes) {
		if (!recipeMap.containsKey(plugin)) {
			recipeMap.put(plugin, recipes);
			return false;
		}
		return recipeMap.get(plugin).addAll(recipes);
	}

	@Override
	public boolean remove(Plugin plugin, Recipe recipe) {
		if (!recipeMap.containsKey(plugin)) {
			recipeMap.put(plugin, new HashSet<Recipe>());
		}
		return recipeMap.get(plugin).remove(recipe);
	}

	@Override
	public void removeAll(Plugin plugin) {
		recipeMap.put(plugin, null);
	}

	@Override
	public void clear() {
		recipeMap.clear();
	}

	@Override
	public Set<Recipe> getRecipes(Plugin plugin) {
		if (!recipeMap.containsKey(plugin)) {
			return Collections.unmodifiableSet(Collections.<Recipe>emptySet());
		}
		return Collections.unmodifiableSet(recipeMap.get(plugin));
	}

	@Override
	public Set<Recipe> getAllRecipes() {
		Set<Recipe> recipes = new HashSet<Recipe>();
		for (Set<Recipe> r : recipeMap.values()) {
			recipes.addAll(r);
		}
		return Collections.unmodifiableSet(recipes);
	}

	@Override
	public Map<Plugin, Set<Recipe>> getRecipeMap() {
		return Collections.unmodifiableMap(recipeMap);
	}

	@Override
	public ItemStack handle(Set<Recipe> recipes, Inventory inventory) {
		for (Recipe recipe : recipes) {
			if (recipe.handle(inventory)) {
				return recipe.getResult();
			}
		}
		return null;
	}

	@Override
	public ItemStack handle(Inventory inventory) {
		return handle(getAllRecipes(), inventory);
	}

	@Override
	public ItemStack handle(Plugin plugin, Inventory inventory) {
		return handle(getRecipes(plugin), inventory);
	}
}
