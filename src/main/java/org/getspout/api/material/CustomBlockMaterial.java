package org.getspout.api.material;

import org.getspout.unchecked.api.plugin.Plugin;

public interface CustomBlockMaterial extends BlockMaterial {

	public int getCustomId();

	public String getFullName();

	public Plugin getAddon();

	public CustomItemMaterial getBlockItem();

	public int getBlockId();
}
