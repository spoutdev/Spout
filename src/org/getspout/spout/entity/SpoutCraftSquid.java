package org.getspout.spout.entity;

import net.minecraft.server.EntitySquid;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSquid;
import org.getspout.spoutapi.entity.SpoutSquid;

public class SpoutCraftSquid extends CraftSquid implements SpoutSquid{

	public SpoutCraftSquid(CraftServer server, EntitySquid entity) {
		super(server, entity);
	}

}
