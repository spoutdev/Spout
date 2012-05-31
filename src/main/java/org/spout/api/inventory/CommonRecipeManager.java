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
package org.spout.api.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public class CommonRecipeManager implements RecipeManager {

	private final Map<Plugin, Set<Recipe>> registeredRecipes = new ConcurrentHashMap<Plugin, Set<Recipe>>();
	private final Set<Recipe> allRecipes = Collections.newSetFromMap(new ConcurrentHashMap<Recipe, Boolean>());

	public CommonRecipeManager() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean addRecipe(Recipe recipe) {
		Plugin plugin = recipe.getPlugin();
		Set<Recipe> recipes = registeredRecipes.get(plugin);
		if (recipes == null) {
			recipes = Collections.newSetFromMap(new ConcurrentHashMap<Recipe, Boolean>());
			registeredRecipes.put(plugin, recipes);
		}
		recipes.add(recipe);
		return allRecipes.add(recipe);
	}

	@Override
	public Set<Recipe> getRecipes(Plugin plugin, Material result) {
		if (!registeredRecipes.containsKey(plugin)) {
			return null;
		}
		Set<Recipe> recipes = new HashSet<Recipe>();
		for (Recipe recipe : getRecipes(plugin)) {
			if (recipe.getResult().getMaterial() == result) {
				recipes.add(recipe);
			}
		}
		return recipes;
	}

	@Override
	public Set<Recipe> getRecipes(Plugin plugin) {
		if (!registeredRecipes.containsKey(plugin)) {
			return null;
		}
		return Collections.unmodifiableSet(registeredRecipes.get(plugin));
	}
	
	@Override
	public boolean replaceRecipe(Recipe oldRecipe, Recipe newRecipe) {
		return removeRecipe(oldRecipe) && addRecipe(newRecipe);
	}
		
	@Override
	public boolean removeRecipe(Recipe recipe) {
		if (!registeredRecipes.containsKey(recipe.getPlugin())) {
			return false;
		}
		Set<Recipe> forPlugin = registeredRecipes.get(recipe.getPlugin());
		return forPlugin.remove(recipe) && allRecipes.remove(recipe);
	}
	
	@Override
	public Set<Recipe> getAllRecipes() {
		return Collections.unmodifiableSet(allRecipes);
	}
}
