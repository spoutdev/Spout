package org.getspout.spout.entity;

import net.minecraft.server.EntityFireball;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.getspout.spoutapi.entity.SpoutFireball;

public class SpoutCraftFireball extends CraftFireball implements SpoutFireball{

	public SpoutCraftFireball(CraftServer server, EntityFireball entity) {
		super(server, entity);
	}

}
