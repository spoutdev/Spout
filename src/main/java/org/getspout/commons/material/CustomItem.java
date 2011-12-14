package org.getspout.commons.material;

import org.getspout.commons.material.CustomItem;
import org.getspout.commons.material.ItemMaterial;
import org.getspout.commons.plugin.Plugin;

public interface CustomItem extends ItemMaterial {
	
	public int getCustomId();
	
	public String getFullName();
	
	public Plugin getAddon();
	
	public CustomItem setTexture(String texture);
	
	public String getTexture();
}
