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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.spout.api.material.Material;
import org.spout.api.plugin.Plugin;

public class CommonRecipeManager implements RecipeManager {

	private final Map<Plugin, Map<Integer, RecipeTree>> registeredShapedRecipes = new ConcurrentHashMap<Plugin, Map<Integer, RecipeTree>>();
	private final Map<Plugin, Map<Integer, Set<ShapelessRecipe>>> registeredShapelessRecipes = new ConcurrentHashMap<Plugin, Map<Integer, Set<ShapelessRecipe>>>();
	private final Map<Integer, Set<Recipe>> allRecipes = new ConcurrentHashMap<Integer, Set<Recipe>>();
	private final Map<Integer, RecipeTree> allShapedRecipes = new ConcurrentHashMap<Integer, RecipeTree>();
	private final Map<Integer, Set<ShapelessRecipe>> allShapelessRecipes = new ConcurrentHashMap<Integer, Set<ShapelessRecipe>>();

	public CommonRecipeManager() {
	}

	@Override
	public boolean addRecipe(Recipe recipe) {
		boolean failed = false;
		if (recipe instanceof ShapedRecipe) {
			failed = !addShapedRecipe((ShapedRecipe) recipe);
		} else if (recipe instanceof ShapelessRecipe) {
			failed = !addShapelessRecipe((ShapelessRecipe) recipe);
		}
		if (allRecipes.get(recipe.getNumOfMaterials()) == null) {
			allRecipes.put(recipe.getNumOfMaterials(), Collections.newSetFromMap(new ConcurrentHashMap<Recipe, Boolean>()));
		}
		failed = !allRecipes.get(recipe.getNumOfMaterials()).add(recipe) || failed;
		return !failed;
	}
	
	private boolean addShapedRecipe(ShapedRecipe recipe) {
		Plugin plugin = recipe.getPlugin();
		ConcurrentHashMap<Integer, RecipeTree> recipesMap = (ConcurrentHashMap<Integer, RecipeTree>) registeredShapedRecipes.get(plugin);
		if (recipesMap == null) {
			recipesMap = new ConcurrentHashMap<Integer, RecipeTree>();
			registeredShapedRecipes.put(plugin, recipesMap);
		}
		if (recipesMap.get(recipe.getNumOfMaterials()) == null) {
			RecipeTree recipes = new RecipeTree();
			registeredShapedRecipes.get(plugin).put(recipe.getNumOfMaterials(), recipes);
		}
		return registeredShapedRecipes.get(plugin).get(recipe.getNumOfMaterials()).addRecipe(recipe);
	}
	
	private boolean addShapelessRecipe(ShapelessRecipe recipe) {
		Plugin plugin = recipe.getPlugin();
		ConcurrentHashMap<Integer, Set<ShapelessRecipe>> recipesMap = (ConcurrentHashMap<Integer, Set<ShapelessRecipe>>) registeredShapelessRecipes.get(plugin);
		if (recipesMap == null) {
			recipesMap = new ConcurrentHashMap<Integer, Set<ShapelessRecipe>>();
			registeredShapelessRecipes.put(plugin, recipesMap);
		}
		if (recipesMap.get(recipe.getNumOfMaterials()) == null) {
			Set<ShapelessRecipe> recipes = Collections.newSetFromMap(new ConcurrentHashMap<ShapelessRecipe, Boolean>());
			registeredShapelessRecipes.get(plugin).put(recipe.getNumOfMaterials(), recipes);
		}
		return registeredShapelessRecipes.get(plugin).get(recipe.getNumOfMaterials()).add(recipe);
	}
	
	@Override
	public Set<Recipe> getRecipes(Plugin plugin, Material result) {
		Set<Recipe> recipes = new HashSet<Recipe>();
		for (Recipe recipe : getRecipes(plugin)) {
			if (recipe.getResult().getMaterial() == result) {
				recipes.add(recipe);
			}
		}
		return recipes;
	}

	@Override
	public Set<Recipe> getRecipes(Plugin plugin) {
		Set<Recipe> recipes = new HashSet<Recipe>();
		recipes.addAll(getShapedRecipes(plugin));
		recipes.addAll(getShapelessRecipes(plugin));
		return recipes;
	}
	
	@Override
	public Set<Recipe> getShapedRecipes(Plugin plugin) {
		Set<Recipe> set = new HashSet<Recipe>();
		if (registeredShapedRecipes.containsKey(plugin)) {
			for (int i : registeredShapedRecipes.get(plugin).keySet()) {
				set.addAll(registeredShapedRecipes.get(plugin).get(i).getAllRecipes());
			}
		}
		return set;
	}
	
	@Override
	public Set<Recipe> getShapelessRecipes(Plugin plugin) {
		Set<Recipe> set = new HashSet<Recipe>();
		if (registeredShapelessRecipes.containsKey(plugin)) {
			for (int i : registeredShapelessRecipes.get(plugin).keySet()) {
				set.addAll(registeredShapelessRecipes.get(plugin).get(i));
			}
		}
		return set;
	}
	
	@Override
	public boolean replaceRecipe(Recipe oldRecipe, Recipe newRecipe) {
		return removeRecipe(oldRecipe) && addRecipe(newRecipe);
	}
		
	@Override
	public boolean removeRecipe(Recipe recipe) {
		if (allRecipes.get(recipe.getNumOfMaterials()) == null) {
			return false;
		}
		boolean failed = false;
		if (recipe instanceof ShapedRecipe) {
			failed = !removeShapedRecipe((ShapedRecipe) recipe);
		} else if (recipe instanceof ShapelessRecipe) {
			failed = !removeShapelessRecipe((ShapelessRecipe) recipe);
		}
		failed = !allRecipes.get(recipe.getNumOfMaterials()).remove(recipe) || failed;
		return !failed;
	}
	
	private boolean removeShapedRecipe(ShapedRecipe recipe) {
		if (!registeredShapedRecipes.containsKey(recipe.getPlugin())) {
			return false;
		}
		if (!registeredShapedRecipes.get(recipe.getPlugin()).containsKey(recipe.getNumOfMaterials()) && allShapedRecipes.containsKey(recipe.getNumOfMaterials())) {
			return false;
		}
		return registeredShapedRecipes.get(recipe.getPlugin()).get(recipe.getNumOfMaterials()).removeRecipe(recipe) && allShapedRecipes.get(recipe.getNumOfMaterials()).removeRecipe(recipe);
	}
	
	private boolean removeShapelessRecipe(ShapelessRecipe recipe) {
		if (!registeredShapelessRecipes.containsKey(recipe.getPlugin())) {
			return false;
		}
		if (!registeredShapelessRecipes.get(recipe.getPlugin()).containsKey(recipe.getNumOfMaterials()) && allShapelessRecipes.containsKey(recipe.getNumOfMaterials())) {
			return false;
		}
		if (!registeredShapelessRecipes.get(recipe.getPlugin()).get(recipe.getNumOfMaterials()).contains(recipe) && !allShapelessRecipes.get(recipe.getNumOfMaterials()).contains(recipe)) {
			return false;
		}
		return registeredShapelessRecipes.get(recipe.getPlugin()).get(recipe.getNumOfMaterials()).remove(recipe) && allShapelessRecipes.get(recipe.getNumOfMaterials()).remove(recipe);
	}
	
	@Override
	public Set<Recipe> getAllRecipes() {
		Set<Recipe> recipes = new HashSet<Recipe>();
		for (int i : allRecipes.keySet()) {
		    recipes.addAll(allRecipes.get(i));
		}
		return recipes;
	}
	
	@Override
	public ShapedRecipe matchShapedRecipe(List<List<Material>> materials) {
		Set<Material> set = new HashSet<Material>();
		for (List<Material> row : materials) {
			for (Material m : row) {
				if (m != null) {
					set.add(m);
				}
			}
		}
		if (!allShapedRecipes.containsKey(set.size())) {
			return null;
		}
		return allShapedRecipes.get(set.size()).matchShapedRecipe(materials);
	}
	
	@Override
	public ShapelessRecipe matchShapelessRecipe(List<Material> materials) {
		Set<Material> set = new HashSet<Material>();
		set.addAll(materials);
		ShapelessRecipe recipe = null;
		if (!allShapelessRecipes.containsKey(set.size())) {
			return null;
		}
		for (ShapelessRecipe r : allShapelessRecipes.get(set.size())) {
			if (materials.containsAll(r.getIngredients()) && r.getIngredients().containsAll(materials)) {
				recipe = r;
				break;
			}
		}
		return recipe;
	}

	@Override
	public ShapedRecipe matchShapedRecipe(Plugin plugin, List<List<Material>> materials) {
		Set<Material> set = new HashSet<Material>();
		for (List<Material> row : materials) {
			for (Material m : row) {
				if (m != null) {
					set.add(m);
				}
			}
		}
		if (!registeredShapedRecipes.containsKey(plugin) || !registeredShapedRecipes.get(plugin).containsKey(set.size())) {
			return null;
		}
		return registeredShapedRecipes.get(plugin).get(set.size()).matchShapedRecipe(materials);
	}

	@Override
	public ShapelessRecipe matchShapelessRecipe(Plugin plugin, List<Material> materials) {
		Set<Material> set = new HashSet<Material>();
		set.addAll(materials);
		ShapelessRecipe recipe = null;
		if (!registeredShapelessRecipes.containsKey(plugin) || !registeredShapelessRecipes.get(plugin).containsKey(set.size())) {
			return null;
		}
		for (ShapelessRecipe r : registeredShapelessRecipes.get(plugin).get(set.size())) {
			if (materials.containsAll(r.getIngredients()) && r.getIngredients().containsAll(materials)) {
				recipe = r;
				break;
			}
		}
		return recipe;
	}
}
