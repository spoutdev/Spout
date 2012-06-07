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
import java.util.HashMap;

import java.util.List;
import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

@SuppressWarnings("unchecked")
public class RecipeBuilder<T extends RecipeBuilder<?>>{
	public Plugin plugin; // TODO null check? Is it needed?
	public ItemStack result;
	public HashMap<Character, Material> ingredientsMap = new HashMap<Character, Material>();
	public List<List<Character>> rows = new ArrayList<List<Character>>();
	public List<Material> ingredients = new ArrayList<Material>();

	public ShapedRecipe buildShapedRecipe() {
		return new ShapedRecipe(this);
	}
	
	public ShapelessRecipe buildShapelessRecipe() {
		return new ShapelessRecipe(this);
	}
    
	public T addPlugin(Plugin plugin) {
		this.plugin = plugin;
		return (T) this;
	}
	
	public T setResult(ItemStack result) {
		this.result = result;
		return (T) this;
	}
	
	public T setResult(Material material, int amount) {
		setResult(new ItemStack(material, amount));
		return (T) this;
	}
	
	/**
	 * Should be used only in recipes that will end up being shaped.
	 * @param symbol character to represent the material
	 * @param ingredient material to use
	 * @return this
	 */
	public T addIngredient(Character symbol, Material ingredient) {
		if (ingredient == null) {
			return (T)this;
		}
		ingredientsMap.put(symbol, ingredient);
		return (T) this;
	}
	
	/**
	 * Should be used only in recipes that will end up being shapeless.
	 * 
	 * @param ingredient ingredient to add
	 * @return this
	 */
	public T addIngredient(Material ingredient) {
		if (ingredient == null) {
			return (T)this;
		}
		ingredients.add(ingredient);
		return (T)this;
	}
	
	/**
	 * Should be used only in recipes that will end up being shapeless.
	 * 
	 * @param ingredient ingredient to add
	 * @param amount amount to add
	 * @return this
	 */
	public T addIngredient(Material ingredient, int amount) {
		if (ingredient == null) {
			return (T) this;
		}
		for (int i = 0; i < amount; i++) {
			ingredients.add(ingredient);
		}
		return (T) this;
	}
	
	public T addRow(List<Character> row) {
		rows.add(row);
		return (T) this;
	}
	
	public T addRow(String str) {
		char[] chars = str.toCharArray();
		ArrayList<Character> row = new ArrayList<Character>();
		for (char c : chars) {
			row.add(c);
		}
		if (!row.isEmpty()) {
			addRow(row);
		}
		return (T) this;
	}
	
	public T removeRow(int rowNumber) {
		if (rows.size() < rowNumber) {
		    return (T) this;
		}
		rows.remove(rowNumber);
		return (T) this;
	}
	
	public T replaceRow(int rowNumber, List<Character> newRow) {
		if (rows.size() < rowNumber || newRow == null) {
		    return (T) this;
		}
		rows.remove(rowNumber);
		rows.add(rowNumber, newRow);
		return (T) this;
	}
	
	public T replaceRow(int rowNumber, String str) {
		if (rows.size() < rowNumber || str == null) {
		    return (T) this;
		}
		rows.remove(rowNumber);
		char[] chars = str.toCharArray();
		ArrayList<Character> row = new ArrayList<Character>();
		for (char c : chars) {
			row.add(c);
		}
		if (!row.isEmpty()) {
			rows.add(rowNumber, row);
		}
		return (T) this;
	}
	
	public T clone(Recipe recipe) {
		plugin = recipe.getPlugin();
		result = recipe.getResult();
		ingredients = recipe.getIngredients();
		if (recipe instanceof ShapedRecipe) {
			ShapedRecipe shaped = (ShapedRecipe) recipe;
			this.ingredientsMap = shaped.getIngredientsMap();
			rows.addAll(shaped.getRows());
		} else if (recipe instanceof ShapelessRecipe) {
			// if needed
		}
		return (T) this;
	}
}