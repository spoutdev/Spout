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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public class ShapedRecipe implements Recipe {
	private final Plugin plugin;
	private final ItemStack result;
	private final HashMap<Character, Material> ingredientsMap;
	private final List<List<Character>> rows;
	private final boolean includeData;

	public ShapedRecipe(RecipeBuilder builder) {
		this.plugin = builder.plugin;
		this.result = builder.result;
		this.ingredientsMap = builder.ingredientsMap;
		this.rows = builder.rows;
		this.includeData = builder.includeData;
	}
	
	@Override
	public boolean getIncludeData() {
		return includeData;
	}
	
	@Override
	public ItemStack getResult() {
		return result;
	}
	
	@Override
	public Plugin getPlugin() {
		return plugin;
	}
	
	@Override
	public List<Material> getIngredients() {
		List<Material> list = new ArrayList<Material>();
		for (Material m : ingredientsMap.values()) {
			list.add(m);
		}
		return list;
	}
	
	@Override
	public int getNumOfMaterials() {
		Set<Material> set = new HashSet<Material>();
		for (Material m : ingredientsMap.values()) {
			if (m.isSubMaterial()) {
				m = m.getParentMaterial();
			}
			set.add(m);
		}
		set.removeAll(Collections.singletonList(null));
		return set.size();
	}
    
	public HashMap<Character, Material> getIngredientsMap() {
		return ingredientsMap;
	}

	public List<List<Character>> getRows() {
		return Collections.unmodifiableList(rows);
	}

	public List<List<Material>> getRowsAsMaterials() {
		List<List<Material>> materials = new ArrayList<List<Material>>();
		for (List<Character> row : getRows()) {
			List<Material> rowAsMaterials = new ArrayList<Material>();
			for (Character c : row) {
				rowAsMaterials.add(ingredientsMap.get(c));
			}
			materials.add(rowAsMaterials);
		}
		return materials;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ShapedRecipe other = (ShapedRecipe) obj;
		if (this.plugin != other.plugin && (this.plugin == null || !this.plugin.equals(other.plugin))) {
			return false;
		}
		if (this.result != other.result && (this.result == null || !this.result.equals(other.result))) {
			return false;
		}
		// TODO extend this to allow different characters that map to the same material to be equal?
		if (this.ingredientsMap != other.ingredientsMap && (this.ingredientsMap == null || !this.ingredientsMap.equals(other.ingredientsMap))) {
			return false;
		}
		if (this.rows != other.rows && (this.rows == null || !this.rows.equals(other.rows))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (new HashCodeBuilder()).append(plugin).append(result).append(ingredientsMap).append(rows).build();
	}
}
