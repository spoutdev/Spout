package org.getspout.api.material;

import org.getspout.api.plugin.Plugin;

public interface CustomItemMaterial extends ItemMaterial {

	public int getCustomId();

	public String getFullName();

	public Plugin getPlugin();

	public CustomItemMaterial setTexture(String texture);

	public String getTexture();
}
