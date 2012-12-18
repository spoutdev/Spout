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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.material.Material;

public class ShapelessRecipe extends Recipe {
	private static final long serialVersionUID = 1L;
	private final List<Material> ingredients;

	public ShapelessRecipe(RecipeBuilder builder) {
		super(builder.result, builder.plugin, builder.includeData);
		this.ingredients = builder.ingredients;
	}

	@Override
	public List<Material> getIngredients() {
		return Collections.unmodifiableList(ingredients);
	}

	@Override
	public Recipe clone() {
		return new RecipeBuilder().clone(this).buildShapelessRecipe();
	}

	@Override
	public RecipeBuilder toBuilder() {
		return new RecipeBuilder().clone(this);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ShapelessRecipe)) {
			return false;
		}
		final ShapelessRecipe other = (ShapelessRecipe) obj;
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
