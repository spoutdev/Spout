package org.getspout.spout.entity;

import net.minecraft.server.EntitySheep;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSheep;
import org.getspout.spoutapi.entity.SpoutSheep;

public class SpoutCraftSheep extends CraftSheep implements SpoutSheep{

	public SpoutCraftSheep(CraftServer server, EntitySheep entity) {
		super(server, entity);
	}

}
