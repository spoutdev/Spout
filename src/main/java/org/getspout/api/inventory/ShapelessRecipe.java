package org.getspout.api.inventory;

import java.util.ArrayList;

import org.getspout.api.material.Material;

public class ShapelessRecipe implements Recipe {
	private ItemStack output;
	private ArrayList<Material> ingredients = new ArrayList<Material>();

	/**
	 * Create a shapeless recipe to craft the specified ItemStack. The
	 * constructor merely determines the result and type; to set the actual
	 * recipe, you'll need to call the appropriate methods.
	 *
	 * @param result The item you want the recipe to create.
	 * @see ShapelessRecipe#addIngredient(Material)
	 * @see ShapelessRecipe#addIngredient(MaterialData)
	 */
	public ShapelessRecipe(ItemStack result) {
		output = result;
	}

	/**
	 * Adds the specified ingredient.
	 *
	 * @param ingredient The ingredient to add.
	 * @return The changed recipe, so you can chain calls.
	 */
	public ShapelessRecipe addIngredient(Material ingredient) {
		return addIngredient(1, ingredient);
	}

	/**
	 * Adds multiples of the specified ingredient.
	 *
	 * @param count How many to add (can't be more than 9!)
	 * @param ingredient The ingredient to add.
	 * @return The changed recipe, so you can chain calls.
	 */
	public ShapelessRecipe addIngredient(int count, Material ingredient) {
		if (ingredients.size() + count > 9) {
			throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
		}
		while (count-- > 0) {
			ingredients.add(ingredient);
		}
		return this;
	}

	/**
	 * Removes an ingredient from the list. If the ingredient occurs multiple
	 * times, only one instance of it is removed.
	 *
	 * @param ingredient The ingredient to remove
	 * @return The changed recipe.
	 */
	public ShapelessRecipe removeIngredient(Material ingredient) {
		ingredients.remove(ingredient);
		return this;
	}

	/**
	 * Get the result of this recipe.
	 *
	 * @return The result stack.
	 */

	public ItemStack getResult() {
		return output;
	}

	/**
	 * Get the list of ingredients used for this recipe.
	 *
	 * @return The input list
	 */
	public ArrayList<Material> getIngredientList() {
		return ingredients;
	}
}
