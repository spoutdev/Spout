package org.getspout.spout.entity;

import net.minecraft.server.EntitySnowball;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSnowball;
import org.getspout.spoutapi.entity.SpoutSnowball;

public class SpoutCraftSnowball extends CraftSnowball implements SpoutSnowball{

	public SpoutCraftSnowball(CraftServer server, EntitySnowball entity) {
		super(server, entity);
	}

}
