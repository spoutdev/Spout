package org.getspout.spout.entity;

import net.minecraft.server.EntityTNTPrimed;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftTNTPrimed;
import org.getspout.spoutapi.entity.SpoutTNTPrimed;

public class SpoutCraftTNTPrimed extends CraftTNTPrimed implements SpoutTNTPrimed{

	public SpoutCraftTNTPrimed(CraftServer server, EntityTNTPrimed entity) {
		super(server, entity);
	}

}
