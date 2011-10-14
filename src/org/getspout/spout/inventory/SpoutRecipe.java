package org.getspout.spout.inventory;

import org.bukkit.inventory.Recipe;

public interface SpoutRecipe extends Recipe {
	void addToCraftingManager();
}
