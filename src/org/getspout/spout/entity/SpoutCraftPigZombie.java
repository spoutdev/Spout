package org.getspout.spout.entity;

import net.minecraft.server.EntityPigZombie;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPigZombie;
import org.getspout.spoutapi.entity.SpoutPigZombie;

public class SpoutCraftPigZombie extends CraftPigZombie implements SpoutPigZombie{

	public SpoutCraftPigZombie(CraftServer server, EntityPigZombie entity) {
		super(server, entity);
	}

}
