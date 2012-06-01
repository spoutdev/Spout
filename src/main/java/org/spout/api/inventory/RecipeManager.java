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

import java.util.Set;

import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public interface RecipeManager {

	/**
	 * Registers a recipe to this games recipe database, then stores the recipe
	 * in the associated plugins recipe.yml. If a recipe for that plugin of that
	 * name already exists, it will update the database and the recipe.yml
	 * 
	 * @param recipe to register
	 * @return  true if successful
	 */
	public boolean addRecipe(Recipe recipe);

	/**
	 * Gets a set of recipe registered to this game recipe database, based on the
	 * plugin and result of the recipe.
	 * 
	 * @param plugin that the recipe belongs to
	 * @param result 
	 * @return the recipes if they're found, otherwise null
	 */
	public Set<Recipe> getRecipes(Plugin plugin, Material result);
		
	/**
	 * Gets a recipe registered to this games recipe database, based on the
	 * plugin.
	 * 
	 * @param plugin that the recipes belongs to
	 * @return the recipes if they're found, otherwise null
	 */
	public Set<Recipe> getRecipes(Plugin plugin);
		
	/**
	 * Replaces an old recipe with a new one.
	 * 
	 * @param oldRecipe 
	 * @param newRecipe 
	 * @return true if fully successful
	 */
	public boolean replaceRecipe(Recipe oldRecipe, Recipe newRecipe);

	/**
	 * Removes a recipe from the games recipes database, then returns the
	 * instance of it if you want to back it up.
	 * 
	 * *WARNING* This will also remove the recipe from the plugins recipe.yml!
	 * It returns a reference to the removed recipe if you want to back it up
	 * for safe keeping still. *WARNING*
	 * 
	 * @param recipe what to remove
	 * @return true if successful, false if not
	 */
	public boolean removeRecipe(Recipe recipe);
	
	
	/**
	 * Get all the registered recipes, from all the plugins.
	 * @return All the registered recipes.
	 */
	public Set<Recipe> getAllRecipes();
}
