/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.inventory;

import java.util.HashMap;
import java.util.List;

import org.getspout.api.material.Material;
import org.getspout.api.plugin.Plugin;

public class ShapelessRecipe implements Recipe {

	private final String name;
	private final Plugin plugin;
	private ItemStack result;
	private HashMap<Character, Material> ingredients;
	private List<Character> amounts;
	private final String type = "SHAPED";
	private String subType;

	public ShapelessRecipe(Plugin plugin, String name, ItemStack result) {
		this.plugin = plugin;
		this.name = name;
		this.result = result;
		this.ingredients = new HashMap<Character, Material>();
	}

	public ItemStack getResult() {
		return result;
	}

	public ShapelessRecipe setResult(ItemStack result) {
		this.result = result;
		return this;
	}

	public ShapelessRecipe addIngredient(Character symbol, Material ingredient) {
		ingredients.put(symbol, ingredient);
		return this;
	}

	public ShapelessRecipe setAmounts(List<Character> amounts) {
		this.amounts = amounts;
		return this;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String getName() {
		return name;
	}

	public List<Character> getAmounts() {
		return amounts;
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
