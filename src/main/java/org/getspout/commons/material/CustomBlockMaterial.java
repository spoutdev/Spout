package org.getspout.commons.material;

import org.getspout.commons.addon.Addon;
import org.getspout.commons.material.BlockMaterial;
import org.getspout.commons.material.CustomItem;

public interface CustomBlockMaterial extends BlockMaterial {
	
	public int getCustomId();
	
	public String getFullName();
	
	public Addon getAddon();
	
	public CustomItem getBlockItem();
	
	public int getBlockId();
}
