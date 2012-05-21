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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.spout.api.plugin.Plugin;

public class CommonRecipeManager implements RecipeManager {

	private Map<Plugin, Map<String, Recipe>> registeredRecipes = new ConcurrentHashMap<Plugin, Map<String, Recipe>>();
	private Set<Recipe> allRecipes = Collections.newSetFromMap(new HashMap<Recipe, Boolean>());

	public CommonRecipeManager() {
	}

	@Override
	public void addRecipe(Recipe recipe) {
		Plugin plugin = recipe.getPlugin();
		Map<String, Recipe> recipes = registeredRecipes.get(plugin);
		if (recipes == null) {
			recipes = new ConcurrentHashMap<String, Recipe>();
		}
		recipes.put(recipe.getName(), recipe);
		allRecipes.add(recipe);
		registeredRecipes.put(plugin, recipes);
	}

	@Override
	public Recipe getRecipe(Plugin plugin, String recipe) {
		if (!registeredRecipes.containsKey(plugin)) {
			return null;
		}
		return registeredRecipes.get(plugin).get(recipe);
	}

	@Override
	public Recipe removeRecipe(Plugin plugin, String recipe) {
		if (!registeredRecipes.containsKey(plugin)) {
			return null;
		}
		Map<String, Recipe> forPlugin = registeredRecipes.get(plugin);
		Recipe safe = forPlugin.get(recipe);
		forPlugin.remove(recipe);
		allRecipes.remove(safe);
		return safe;
	}
	
	@Override
	public Set<Recipe> getAllRecipes() {
		return Collections.unmodifiableSet(allRecipes);
	}
}
