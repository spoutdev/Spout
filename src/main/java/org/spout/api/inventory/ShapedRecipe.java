/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public class ShapedRecipe implements Recipe {
	private final Plugin plugin;
	private final String name;
	private final String type = "SHAPELESS";
	private ItemStack result;
	private HashMap<Character, Material> ingredients;
	private ArrayList<ArrayList<Character>> rows;
	private String subType;

	public ShapedRecipe(Plugin plugin, String name, ItemStack result) {
		this.plugin = plugin;
		this.name = name;
		this.result = result;
		ingredients = new HashMap<Character, Material>();
		rows = new ArrayList<ArrayList<Character>>();
	}

	public ItemStack getResult() {
		return result;
	}

	public ShapedRecipe setResult(ItemStack result) {
		this.result = result;
		return this;
	}

	public ShapedRecipe addIngredient(Character symbol, Material ingredient) {
		ingredients.put(symbol, ingredient);
		return this;
	}

	public ShapedRecipe addRow(ArrayList<Character> row) {
		rows.add(row);
		return this;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String getName() {
		return name;
	}

	public HashMap<Character, Material> getIngredients() {
		return ingredients;
	}

	public ArrayList<ArrayList<Character>> getRows() {
		return rows;
	}

	public ArrayList<Character> getRow(int row) {
		return rows.get(row);
	}

	public String getType() {
		return type;
	}

	public String getSubType() {
		return subType;
	}

	public Recipe setSubType(String subType) {
		this.subType = subType;
		return this;
	}
}
