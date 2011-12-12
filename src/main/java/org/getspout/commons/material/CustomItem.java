package org.getspout.commons.material;

import org.getspout.commons.addon.Addon;
import org.getspout.commons.material.CustomItem;
import org.getspout.commons.material.Item;

public interface CustomItem extends Item {
	
	public int getCustomId();
	
	public String getFullName();
	
	public Addon getAddon();
	
	public CustomItem setTexture(String texture);
	
	public String getTexture();
}
