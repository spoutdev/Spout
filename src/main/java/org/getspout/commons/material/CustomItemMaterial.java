package org.getspout.commons.material;

import org.getspout.commons.material.CustomItemMaterial;
import org.getspout.commons.material.ItemMaterial;
import org.getspout.commons.plugin.Plugin;

public interface CustomItemMaterial extends ItemMaterial {
	
	public int getCustomId();
	
	public String getFullName();
	
	public Plugin getPlugin();
	
	public CustomItemMaterial setTexture(String texture);
	
	public String getTexture();
}
