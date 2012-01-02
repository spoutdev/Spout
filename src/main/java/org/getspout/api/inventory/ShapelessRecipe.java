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
