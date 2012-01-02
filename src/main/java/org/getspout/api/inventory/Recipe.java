package org.getspout.api.inventory;

import org.getspout.api.plugin.Plugin;

public interface Recipe {

	public Plugin getPlugin();

	public String getName();
	
	public ItemStack getResult();

	public Recipe setResult(ItemStack result);
	
	public String getType();
	
	public String getSubType();
	
	public Recipe setSubType(String subType);
}
