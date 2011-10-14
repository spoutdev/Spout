package org.getspout.spout.entity;

import net.minecraft.server.EntityGhast;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.getspout.spoutapi.entity.SpoutGhast;

public class SpoutCraftGhast extends CraftGhast implements SpoutGhast{

	public SpoutCraftGhast(CraftServer server, EntityGhast entity) {
		super(server, entity);
	}

}
