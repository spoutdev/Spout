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

import java.util.Map;
import java.util.Set;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.plugin.Plugin;

public interface RecipeManager {
	/**
	 * Registers a recipe to be stored.
	 *
	 * @param plugin to register recipe to
	 * @param recipe to register
	 * @return true if recipe already existed
	 */
	public boolean register(Plugin plugin, Recipe recipe);

	/**
	 * Registers all the specified recipes to the specified plugin.
	 *
	 * @param plugin to register recipes to
	 * @param recipes to register
	 * @return true if the set of recipes was changed
	 */
	public boolean registerAll(Plugin plugin, Set<Recipe> recipes);

	/**
	 * Removes a recipe.
	 *
	 * @param plugin to remove recipe from
	 * @param recipe to remove
	 * @return whether the recipe existed
	 */
	public boolean remove(Plugin plugin, Recipe recipe);

	/**
	 * Removes all recipes of the specified {@link Plugin}
	 *
	 * @param plugin to remove recipes
	 */
	public void removeAll(Plugin plugin);

	/**
	 * Clears all recipes.
	 */
	public void clear();

	/**
	 * Returns all recipes registered to the specified {@link Plugin}
	 *
	 * @param plugin to get recipes from
	 * @return recipes of plugin
	 */
	public Set<Recipe> getRecipes(Plugin plugin);

	/**
	 * Returns all recipes of all registered {@link Plugin}s
	 *
	 * @return all recipes
	 */
	public Set<Recipe> getAllRecipes();

	/**
	 * Returns a {@link java.util.Map} of {@link Plugin}s to their registered
	 * {@link Recipe}s
	 *
	 * @return recipe map
	 */
	public Map<Plugin, Set<Recipe>> getRecipeMap();

	/**
	 * Tries to craft a result from specified recipes using the specified
	 * {@link org.spout.api.inventory.Inventory} as ingredients.
	 *
	 * @param recipes to check
	 * @param inventory to test
	 * @return result of craft, null if no result
	 */
	public ItemStack handle(Set<Recipe> recipes, Inventory inventory);

	/**
	 * Tries to craft a result from all available recipes.
	 *
	 * @param inventory to test
	 * @return result of craft
	 */
	public ItemStack handle(Inventory inventory);

	/**
	 * Tries to craft a result from all available recipes registered to the
	 * specified {@link Plugin}.
	 *
	 * @param plugin to get recipes from
	 * @param inventory to test
	 * @return result of craft
	 */
	public ItemStack handle(Plugin plugin, Inventory inventory);
}
