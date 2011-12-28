package org.getspout.api.inventory;

public class ShapedRecipe implements Recipe {
	
	private ItemStack result;
	
	public ShapedRecipe(ItemStack result) {
		this.result = result;
	}

	public ItemStack getResult() {
		return result;
	}

	public ShapedRecipe setResult(ItemStack result) {
		this.result = result;
		return this;
	}
}
