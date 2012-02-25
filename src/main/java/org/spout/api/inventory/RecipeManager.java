package org.spout.api.inventory;

import org.spout.api.plugin.Plugin;

public interface RecipeManager {

	/**
	 * Registers a recipe to this games recipe database, then stores the recipe
	 * in the associated plugins recipe.yml. If a recipe for that plugin of that
	 * name already exists, it will update the database and the recipe.yml
	 * 
	 * @param recipe to register
	 */
	public void addRecipe(Recipe recipe);

	/**
	 * Gets a recipe registered to this games recipe database, based on the
	 * plugin and name of the recipe.
	 * 
	 * @param plugin that the recipe belongs to
	 * @param recipe name
	 * @return the recipe if it's found, otherwise null
	 */
	public Recipe getRecipe(Plugin plugin, String recipe);

	/**
	 * Removes a recipe from the games recipes database, then returns the
	 * instance of it if you want to back it up.
	 * 
	 * *WARNING* This will also remove the recipe from the plugins recipe.yml!
	 * It returns a reference to the removed recipe if you want to back it up
	 * for safe keeping still. *WARNING*
	 * 
	 * @param plugin that the recipe belongs to
	 * @param recipe name
	 * @return recipe that was removed
	 */
	public Recipe removeRecipe(Plugin plugin, String recipe);
}
