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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Set;
import org.spout.api.material.Material;

/**
 * Although this can technically be used by any class, it really should only be used internally in a RecipeManager. 
 * 
 */
public class RecipeTree {
	private RecipeNode root = new RecipeNode(-1, 0);
    
	private class RecipeNode {
		private final Map<Material, RecipeNode> children;
		private RecipeNode nextRow = null;
		private final int x, y;
		private final RecipeNode parent;
		private ShapedRecipe recipe;

		public RecipeNode(int x, int y) {
			this(null, x, y, null);
		}

		public RecipeNode(RecipeNode parent, int x, int y) {
			this(parent, x, y, null);
		}
		
		public RecipeNode(RecipeNode parent, int x, int y, ShapedRecipe recipe) {
			this.children = new HashMap<Material, RecipeNode>();
			this.x = x;
			this.y = y;
			this.parent = parent == null ? this : parent;
			this.recipe = recipe;
		}
		
		public RecipeNode getOrAddChild(Material material) {
			if (children.containsKey(material) && children.get(material) != null) {
				return children.get(material);
			}
			children.put(material, new RecipeNode(this, x + 1, y));
			return children.get(material);
		}
		
		public boolean setRecipe(ShapedRecipe recipe) {
			if (x == -1) {
				return false;
			}
			this.recipe = recipe;
			return true;
		}

		public ShapedRecipe getRecipe() {
			return recipe;
		}
		
		public RecipeNode getNextRow() {
			if (nextRow == null) {
				nextRow = new RecipeNode(this, -1, this.y + 1);
			}
			return nextRow;
		}

		public RecipeNode getParent() {
			return parent;
		}
		
		public Set<ShapedRecipe> getAllRecipes() {
			Set<ShapedRecipe> recipes = new HashSet<ShapedRecipe>();
			for (RecipeNode child : children.values()) {
				recipes.addAll(child.getAllRecipes());
			}
			if (recipe != null) {
				recipes.add(recipe);
			}
			return recipes;
		}
	}
    
	public ShapedRecipe matchShapedRecipe(List<List<Material>> materials) {
		RecipeNode current = root;
		for (List<Material> list : materials) {
			for (Material m : list) {
				current = current.getOrAddChild(m);
			}
			current = current.getNextRow();
		}
		return current.getParent().getRecipe();
	}
	
	public boolean addRecipe(ShapedRecipe recipe) {
		RecipeNode current = root;
		for (List<Material> list : recipe.getRowsAsMaterials()) {
			for (Material m : list) {
				current = current.getOrAddChild(m);
			}
			current = current.getNextRow();
		}
		return current.getParent().setRecipe(recipe);
	}
	
	public Set<ShapedRecipe> getAllRecipes() {
		return Collections.unmodifiableSet(root.getAllRecipes());
	}
	
	public boolean removeRecipe(ShapedRecipe recipe) {
		RecipeNode current = root;
		for (List<Material> list : recipe.getRowsAsMaterials()) {
			for (Material m : list) {
				current = current.getOrAddChild(m);
			}
			current = current.getNextRow();
		}
		return current.getParent().setRecipe(null);
	}
}
