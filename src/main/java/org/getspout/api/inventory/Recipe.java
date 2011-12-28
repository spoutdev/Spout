package org.getspout.api.inventory;

public interface Recipe {
	
	public ItemStack getResult();
	
	public Recipe setResult(ItemStack result);
}
