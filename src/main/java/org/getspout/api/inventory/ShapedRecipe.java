package org.getspout.api.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import org.getspout.api.material.Material;
import org.getspout.api.plugin.Plugin;

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
		this.ingredients = new HashMap<Character, Material>();
		this.rows = new ArrayList<ArrayList<Character>>();
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
