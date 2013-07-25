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

import java.util.List;
import java.util.Set;

import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public interface RecipeManager {

	/**
	 * Registers a recipe to be stored.
	 *
	 * @param recipe to register
	 * @return true if recipe already existed
	 */
	public boolean register(Recipe recipe);

	/**
	 * Registers all the specified recipes to the specified plugin.
	 *
	 * @param plugin to register recipes to
	 * @param recipes to register
	 * @return true if the set of recipes was changed
	 */
	public boolean registerAll(Set<Recipe> recipes);

	/**
	 * Removes a recipe.
	 *
	 * @param recipe to remove
	 * @return whether the recipe existed
	 */
	public boolean remove(Recipe recipe);
	
	/**
	 * Replaces an old recipe with a new one.
	 * @param oldRecipe
	 * @param newRecipe
	 * @return true if fully successful
	 */
	public boolean replaceRecipe(Recipe oldRecipe, Recipe newRecipe);

	/**
	 * Clears all recipes.
	 */
	public void clear();

	/**
	 * Returns all recipes of all registered {@link Plugin}s
	 *
	 * @return all recipes
	 */
	public Set<Recipe> getAllRecipes();

	/**
	 * Gets all the shaped recipes registered for a plugin.
	 * @param plugin that the recipes belongs to
	 * @return the recipes if they're found, otherwise an empty set
	 */
	public Set<Recipe> getShapedRecipes(Plugin plugin);

	/**
	 * Gets all the shapeless recipes registered for a plugin.
	 * @param plugin that the recipes belongs to
	 * @return the recipes if they're found, otherwise an empty set
	 */
	public Set<Recipe> getShapelessRecipes(Plugin plugin);

	/**
	 * Match the materials to any ShapedRecipe
	 * @param materials by rows
	 * @return ShapedRecipe
	 */
	public ShapedRecipe matchShapedRecipe(List<List<Material>> materials);

	/**
	 * Match the materials to any ShapelessRecipe
	 * @param materials
	 * @return ShapelesRecipe
	 */
	public ShapelessRecipe matchShapelessRecipe(List<Material> materials);

	/**
	 * Match the materials to any ShapedRecipe for a given plugin
	 * @param plugin
	 * @param materials by rows
	 * @return ShapedRecipe
	 */
	public ShapedRecipe matchShapedRecipe(Plugin plugin, List<List<Material>> materials);

	/**
	 * Match the materials to any ShapelessRecipe for a given plugin
	 * @param plugin
	 * @param materials
	 * @return ShapelesRecipe
	 */
	public ShapelessRecipe matchShapelessRecipe(Plugin plugin, List<Material> materials);
}
