package org.getspout.commons.material;

import org.getspout.commons.material.BlockMaterial;
import org.getspout.commons.material.CustomItem;
import org.getspout.commons.plugin.Plugin;

public interface CustomBlockMaterial extends BlockMaterial {
	
	public int getCustomId();
	
	public String getFullName();
	
	public Plugin getAddon();
	
	public CustomItem getBlockItem();
	
	public int getBlockId();
}
