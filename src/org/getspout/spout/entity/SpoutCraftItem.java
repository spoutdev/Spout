package org.getspout.spout.entity;

import net.minecraft.server.EntityItem;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.getspout.spoutapi.entity.SpoutItem;

public class SpoutCraftItem extends CraftItem implements SpoutItem{

	public SpoutCraftItem(CraftServer server, EntityItem entity) {
		super(server, entity);
	}

}
