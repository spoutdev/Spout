package org.getspout.commons.material;

import org.getspout.commons.addon.Addon;
import org.getspout.commons.material.Block;
import org.getspout.commons.material.CustomItem;

public interface CustomBlock extends Block {
	
	public int getCustomId();
	
	public String getFullName();
	
	public Addon getAddon();
	
	public CustomItem getBlockItem();
	
	public int getBlockId();
}
