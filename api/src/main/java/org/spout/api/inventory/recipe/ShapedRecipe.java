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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;

/**
 * Represents a {@link Recipe} with a definite shape.
 */
public class ShapedRecipe extends Recipe {
	private final List<List<Character>> rows;
	private final Map<Character, Material> ingredientsMap;

	public ShapedRecipe(RecipeBuilder builder) {
		super(builder.result, builder.plugin, builder.includeData);
		this.rows = builder.rows;
		this.ingredientsMap = builder.ingredientsMap;
	}

	/**
	 * Returns a {@link List} of a list of {@link Character}s that represent the rows of the recipe.
	 *
	 * @return rows of the recipe
	 */
	public List<List<Character>> getRows() {
		return Collections.unmodifiableList(rows);
	}

	/**
	 * Removes a row from the recipe at the given index. <code>0</code> being the last row and <code>{@link java.util.List#size()} - 1</code> being the last row.
	 *
	 * @param index of row
	 * @return row at specified index
	 */
	public List<Character> removeRow(int index) {
		return Collections.unmodifiableList(rows.remove(index));
	}

	/**
	 * Returns the length of the rows of the recipe or <code>-1</code> if there are no rows. In addition, this method verifies that all rows have the same length.
	 *
	 * @return length of each row
	 * @throws IllegalStateException if the length of the rows is inconsistent.
	 */
	public int getRowLength() {
		if (rows.isEmpty()) {
			return -1;
		}

		int length = rows.get(0).size();
		for (int i = 0; i < rows.size(); i++) {
			List<Character> row = rows.get(i);
			if (row.size() != length) {
				throw new IllegalStateException("Row " + (i + 1) + "'s length is incosistent with the rest.");
			}
		}

		return length;
	}

	// Ingredient management

	/**
	 * Returns a {@link Map} with {@link ItemStack}s mapped to the {@link Character} they are represented by.
	 *
	 * @return character item mapping
	 */
	public Map<Character, Material> getIngredientsMap() {
		return Collections.unmodifiableMap(ingredientsMap);
	}

	/**
	 * Returns the recipe's rows with {@link ItemStack} in place of the characters which represent them.
	 *
	 * @return rows of the recipe
	 */
	public List<List<Material>> getIngredientRows() {
		List<List<Material>> ingredientRows = new ArrayList<List<Material>>();
		for (List<Character> row : rows) {
			List<Material> ingredientRow = new ArrayList<Material>();
			for (char c : row) {
				ingredientRow.add(ingredientsMap.get(c));
			}
			ingredientRows.add(ingredientRow);
		}
		return Collections.unmodifiableList(ingredientRows);
	}

	// Overridden methods

	@Override
	public List<Material> getIngredients() {
		return Collections.unmodifiableList(new ArrayList<Material>(ingredientsMap.values()));
	}

	@Override
	public ShapedRecipe clone() {
		return new RecipeBuilder().clone(this).buildShapedRecipe();
	}

	@Override
	public RecipeBuilder toBuilder() {
		return new RecipeBuilder().clone(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ShapedRecipe)) {
			return false;
		}
		final ShapedRecipe other = (ShapedRecipe) obj;
		if (this.result != other.result && (this.result == null || !this.result.equals(other.result))) {
			return false;
		}
		if (getIngredientsMap().equals(other.getIngredientsMap())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (new HashCodeBuilder()).append(plugin).append(result).append(ingredientsMap).append(rows).build();
	}
}
