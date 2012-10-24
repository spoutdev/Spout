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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.inventory.shape.Grid;
import org.spout.api.inventory.util.GridIterator;
import org.spout.api.material.Material;

/**
 * Represents a {@link Recipe} with a definite shape.
 */
public class ShapedRecipe extends Recipe {
	private final List<List<Character>> rows = new ArrayList<List<Character>>();
	private final Map<Character, ItemStack> ingredientMap = new LinkedHashMap<Character, ItemStack>();

	public ShapedRecipe(ItemStack result) {
		super(result);
	}

	// Row management

	/**
	 * Returns a {@link List} of a list of {@link Character}s that represent
	 * the rows of the recipe.
	 *
	 * @return rows of the recipe
	 */
	public List<List<Character>> getRows() {
		return Collections.unmodifiableList(rows);
	}

	/**
	 * Adds a row to the recipe.
	 *
	 * @param chars characters to represent {@link ItemStack}s
	 * @return true if the row was added successfully
	 */
	public boolean addRow(Character... chars) {
		return rows.add(Arrays.asList(chars));
	}

	/**
	 * Adds the specified rows to the recipe.
	 *
	 * @param rows to add
	 * @return true if all rows were added successfully
	 */
	public boolean addRows(List<List<Character>> rows) {
		return rows.addAll(rows);
	}

	/**
	 * Removes a row from the recipe at the given index. <code>0</code> being the last row
	 * and <code>{@link java.util.List#size()} - 1</code> being the last row.
	 *
	 * @param index of row
	 * @return row at specified index
	 */
	public List<Character> removeRow(int index) {
		return Collections.unmodifiableList(rows.remove(index));
	}

	/**
	 * Returns the length of the rows of the recipe or <code>-1</code> if there
	 * are no rows. In addition, this method verifies that all rows have the
	 * same length.
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
	 * Sets the {@link Character} that the {@link ItemStack} ingredient is
	 * represented by.
	 *
	 * @param c to represent specified ingredient
	 * @param ingredient to be represented by specified character
	 * @return previous ingredient represented by the specified character
	 */
	public ItemStack setIngredient(char c, ItemStack ingredient) {
		return ingredientMap.put(c, ingredient);
	}

	/**
	 * Sets the {@link Character} that the {@link ItemStack} ingredient is
	 * represented by.
	 *
	 * @param c to represent specified ingredient
	 * @param ingredient material to be represented by specified character
	 * @param amount amount of material required
	 * @return previous ingredient represented by the specified character
	 */
	public ItemStack setIngredient(char c, Material ingredient, int amount) {
		return setIngredient(c, new ItemStack(ingredient, amount));
	}

	/**
	 * Sets the {@link Character} that the {@link ItemStack} ingredient is
	 * represented by.
	 *
	 * @param c character to represent one of the ingredient
	 * @param ingredient to be represented by specified character
	 * @return ingredient previously represented by specified character
	 */
	public ItemStack setIngredient(char c, Material ingredient) {
		return setIngredient(c, ingredient, 1);
	}

	/**
	 * Sets all {@link Character} in the map to represent all {@link ItemStack}
	 * in the map.
	 *
	 * @param chars character map to represent values
	 */
	public void setIngredients(Map<Character, ItemStack> chars) {
		ingredientMap.putAll(chars);
	}

	/**
	 * Returns a {@link Map} with {@link ItemStack}s mapped to the
	 * {@link Character} they are represented by.
	 *
	 * @return character item mapping
	 */
	public Map<Character, ItemStack> getIngredientMap() {
		return Collections.unmodifiableMap(ingredientMap);
	}

	/**
	 * Returns the recipe's rows with {@link ItemStack} in place of the
	 * characters which represent them.
	 *
	 * @return rows of the recipe
	 */
	public List<List<ItemStack>> getIngredientRows() {
		List<List<ItemStack>> ingredientRows = new ArrayList<List<ItemStack>>();
		for (List<Character> row : rows) {
			List<ItemStack> ingredientRow = new ArrayList<ItemStack>();
			for (char c : row) {
				ingredientRow.add(ingredientMap.get(c));
			}
			ingredientRows.add(ingredientRow);
		}
		return Collections.unmodifiableList(ingredientRows);
	}

	// Overridden methods

	@Override
	public Collection<ItemStack> getIngredients() {
		return Collections.unmodifiableCollection(ingredientMap.values());
	}

	@Override
	public boolean handle(Inventory inventory) {

		final Grid grid = inventory.grid(getRowLength());
		final GridIterator i = grid.iterator();
		final List<List<ItemStack>> rows = getIngredientRows();
		final int lastRow = rows.size() - 1;

		while (i.hasNext()) {
			ItemStack actual = inventory.get(i.next());
			ItemStack expected = rows.get(lastRow - i.getY()).get(i.getX());
			if (actual == null && expected == null) {
				continue;
			}

			if (((actual == null) != (expected == null)) || (!actual.equalsIgnoreSize(expected) || actual.getAmount() < expected.getAmount())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ShapedRecipe clone() {
		ShapedRecipe clone = new ShapedRecipe(result);
		clone.addRows(rows);
		clone.setIngredients(ingredientMap);
		return clone;
	}

	@Override
	public int hashCode() {
		return getIngredientRows().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof ShapedRecipe && ((ShapedRecipe) obj).getIngredientRows() == getIngredientRows();
	}
}
