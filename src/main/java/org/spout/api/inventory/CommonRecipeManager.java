package org.spout.api.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.spout.api.plugin.Plugin;

public class CommonRecipeManager implements RecipeManager {

	private final Map<Plugin, Map<String, Recipe>> registeredRecipes = new ConcurrentHashMap<Plugin, Map<String, Recipe>>();
	private final Set<Recipe> allRecipes = Collections.newSetFromMap(new HashMap<Recipe, Boolean>());

	public CommonRecipeManager() {
	}

	@Override
	public void addRecipe(Recipe recipe) {
		Plugin plugin = recipe.getPlugin();
		Map<String, Recipe> recipes = registeredRecipes.get(plugin);
		if (recipes == null) {
			recipes = new ConcurrentHashMap<String, Recipe>();
			registeredRecipes.put(plugin, recipes);
		}
		recipes.put(recipe.getName(), recipe);
		allRecipes.add(recipe);
	}

	@Override
	public Recipe getRecipe(Plugin plugin, String recipe) {
		if (!registeredRecipes.containsKey(plugin)) {
			return null;
		}
		return registeredRecipes.get(plugin).get(recipe);
	}

	@Override
	public Recipe removeRecipe(Plugin plugin, String recipe) {
		if (!registeredRecipes.containsKey(plugin)) {
			return null;
		}
		Map<String, Recipe> forPlugin = registeredRecipes.get(plugin);
		Recipe safe = forPlugin.get(recipe);
		forPlugin.remove(recipe);
		allRecipes.remove(safe);
		return safe;
	}
	
	@Override
	public Set<Recipe> getAllRecipes() {
		return Collections.unmodifiableSet(allRecipes);
	}
}
