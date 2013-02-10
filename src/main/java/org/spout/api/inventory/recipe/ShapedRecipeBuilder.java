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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spout.api.material.Material;

public class ShapedRecipeBuilder extends RecipeBuilder {
	protected final Map<Character, Material> ingredientMap = new HashMap<Character, Material>();
	protected List<String> rows = new ArrayList<String>();

	public List<List<Material>> getRows() {
		List<List<Material>> rows = new ArrayList<List<Material>>();
		for (String row : this.rows) {
			List<Material> r = new ArrayList<Material>();
			for (char c : row.toCharArray()) {
				r.add(ingredientMap.get(c));
			}
			rows.add(r);
		}
		return Collections.unmodifiableList(rows);
	}

	public List<String> getShape() {
		return Collections.unmodifiableList(rows);
	}

	public void setShape(String... rows) {
		this.rows = Arrays.asList(rows);
	}

	public Map<Character, Material> getIngredientMap() {
		return Collections.unmodifiableMap(ingredientMap);
	}

	public Material getIngredient(char c) {
		return ingredientMap.get(c);
	}

	public Material setIngredient(char c, Material ingredient) {
		return ingredientMap.put(c, ingredient);
	}

	@Override
	public ShapedRecipe build() {
		return new ShapedRecipe(product, getRows());
	}
}
