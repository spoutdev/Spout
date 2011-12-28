package org.getspout.api.inventory;

public class ShapelessRecipe implements Recipe {
	
	private ItemStack result;
	
	public ShapelessRecipe(ItemStack result) {
		this.result = result;
	}

	public ItemStack getResult() {
		return result;
	}

	public ShapelessRecipe setResult(ItemStack result) {
		this.result = result;
		return this;
	}

}
