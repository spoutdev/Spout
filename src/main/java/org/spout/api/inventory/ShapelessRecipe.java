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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public class ShapelessRecipe implements Recipe {
	private final Plugin plugin;
	private final ItemStack result;
	private final List<Material> ingredients;
	private final boolean includeData;

	public ShapelessRecipe(RecipeBuilder builder) {
		this.plugin = builder.plugin;
		this.result = builder.result;
		this.ingredients = builder.ingredients;
		this.includeData = builder.includeData;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}
	
	@Override
	public List<Material> getIngredients() {
		return Collections.unmodifiableList(ingredients);
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public boolean getIncludeData() {
		return includeData;
	}

	@Override
	public int getNumOfMaterials() {
		Set<Material> set = new HashSet<Material>(ingredients);
		return set.size();
	}
		
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ShapelessRecipe other = (ShapelessRecipe) obj;
		if (this.plugin != other.plugin && (this.plugin == null || !this.plugin.equals(other.plugin))) {
			return false;
		}
		if (this.result != other.result && (this.result == null || !this.result.equals(other.result))) {
			return false;
		}
		List<Material> materials = new ArrayList<Material>();
		List<Material> materials2 = new ArrayList<Material>();
		materials.addAll(ingredients);
		materials2.addAll(other.ingredients);
		materials.removeAll(other.ingredients);
		materials2.removeAll(ingredients);
		if (!materials.isEmpty() || !materials2.isEmpty()) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return (new HashCodeBuilder()).append(plugin).append(result).append(ingredients).build();
	}
}
