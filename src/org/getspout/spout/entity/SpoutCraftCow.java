package org.getspout.spout.entity;

import net.minecraft.server.EntityCow;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftCow;
import org.getspout.spoutapi.entity.SpoutCow;

public class SpoutCraftCow extends CraftCow implements SpoutCow{
	
	public SpoutCraftCow(CraftServer server, EntityCow entity) {
		super(server, entity);
	}

}
