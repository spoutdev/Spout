package org.getspout.commons.material;

import org.getspout.commons.material.BlockMaterial;
import org.getspout.commons.material.CustomItemMaterial;
import org.getspout.commons.plugin.Plugin;

public interface CustomBlockMaterial extends BlockMaterial {
	
	public int getCustomId();
	
	public String getFullName();
	
	public Plugin getAddon();
	
	public CustomItemMaterial getBlockItem();
	
	public int getBlockId();
}
