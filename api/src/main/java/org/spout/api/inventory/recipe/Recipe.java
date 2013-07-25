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

import java.util.List;

import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

/**
 * Represents an arrangement of {@link ItemStack} with an outcome
 */
public abstract class Recipe implements Cloneable {
	protected final ItemStack result;
	protected final Plugin plugin;
	protected final boolean includeData;

	public Recipe(ItemStack result) {
		this(result, null);
	}

	public Recipe(ItemStack result, Plugin plugin) {
		this(result, plugin, false);
	}

	public Recipe(ItemStack result, Plugin plugin, boolean includeData) {
		this.result = result;
		this.plugin = plugin;
		this.includeData = includeData;
	}

	/**
	 * Returns the result of the Recipe if successful.
	 *
	 * @return result of recipe
	 */
	public ItemStack getResult() {
		return result;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public boolean getIncludeData() {
		return includeData;
	}

	/**
	 * Returns the required ingredients to meet the requirements of the recipe.
	 *
	 * @return List of ingredients to craft the recipe
	 */
	public abstract List<Material> getIngredients();

	public abstract RecipeBuilder toBuilder();

	@Override
	public abstract Recipe clone();
}
