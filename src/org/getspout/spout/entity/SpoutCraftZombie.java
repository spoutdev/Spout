package org.getspout.spout.entity;

import net.minecraft.server.EntityZombie;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.getspout.spoutapi.entity.SpoutZombie;

public class SpoutCraftZombie extends CraftZombie implements SpoutZombie{

	public SpoutCraftZombie(CraftServer server, EntityZombie entity) {
		super(server, entity);
	}

}
