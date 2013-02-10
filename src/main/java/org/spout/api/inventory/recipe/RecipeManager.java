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

import java.util.Set;

import org.spout.api.inventory.ItemStack;
import org.spout.api.plugin.Plugin;

/**
 * Interface for registering {@link Recipe} implementations.
 */
public interface RecipeManager {
	/**
	 * Registers a new {@link Recipe} to the specified {@link Plugin}.
	 *
	 * @param recipe to register
	 * @param plugin to bind recipe to
	 * @return whether the recipe existed
	 */
	public boolean register(Recipe recipe, Plugin plugin);

	/**
	 * Removes all recipes that are the same for every plugin. Note that there
	 * can be multiple recipes that are the same in the manager bound to
	 * different plugins.
	 *
	 * @param recipe to remove
	 */
	public void remove(Recipe recipe);

	/**
	 * Clears all recipes bound to the specified plugin.
	 *
	 * @param plugin to clear all recipes from
	 */
	public void clear(Plugin plugin);

	/**
	 * Removes every recipe.
	 */
	public void clear();

	/**
	 * Returns all the {@link Recipe}s bound to the specified {@link Plugin}.
	 *
	 * @param plugin to get recipes for
	 * @return recipes bound to the specified plugin
	 */
	public Set<Recipe> getRecipes(Plugin plugin);

	/**
	 * Returns all recipes.
	 *
	 * @return all recipes
	 */
	public Set<Recipe> getRecipes();

	/**
	 * Whether the manager contains a {@link Recipe} that matches the specified
	 * recipe that is also bound to the specified {@link Plugin}.
	 *
	 * @see Recipe#equals(Object)
	 * @param plugin to get recipes from
	 * @param recipe to look for
	 * @return true if the recipe has a match that's bound to the plugin
	 */
	public boolean contains(Plugin plugin, Recipe recipe);

	/**
	 * Whether the manager contains a {@link Recipe} that matches the specified
	 * recipe.
	 *
	 * @see Recipe#equals(Object)
	 * @param recipe to look for
	 * @return true if the recipe has a match
	 */
	public boolean contains(Recipe recipe);

	/**
	 * Returns a product for the specified regents among the registered
	 * recipes bound to the specified plugin.
	 *
	 * @param plugin to get recipes from
	 * @param regents for recipe
	 * @return product of recipe
	 */
	public ItemStack getProduct(Plugin plugin, Object regents);

	/**
	 * Returns a product for the specified regents among the registered
	 * recipes.
	 *
	 * @param regents for recipe
	 * @return product of recipe
	 */
	public ItemStack getProduct(Object regents);
}
