package org.getspout.spout.entity;

import net.minecraft.server.EntityFallingSand;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftFallingSand;
import org.getspout.spoutapi.entity.SpoutFallingSand;

public class SpoutCraftFallingSand extends CraftFallingSand implements SpoutFallingSand{

	public SpoutCraftFallingSand(CraftServer server, EntityFallingSand entity) {
		super(server, entity);
	}

}
