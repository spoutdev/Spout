package org.getspout.spout.entity;

import net.minecraft.server.EntityPig;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.getspout.spoutapi.entity.SpoutPig;

public class SpoutCraftPig extends CraftPig implements SpoutPig{

	public SpoutCraftPig(CraftServer server, EntityPig entity) {
		super(server, entity);
	}

}
