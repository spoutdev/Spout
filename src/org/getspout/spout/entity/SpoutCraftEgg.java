package org.getspout.spout.entity;

import net.minecraft.server.EntityEgg;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEgg;
import org.getspout.spoutapi.entity.SpoutEgg;

public class SpoutCraftEgg extends CraftEgg implements SpoutEgg{

	public SpoutCraftEgg(CraftServer server, EntityEgg entity) {
		super(server, entity);
	}

}
