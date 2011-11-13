package org.getspout.spout.entity;

import net.minecraft.server.EntityWolf;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.getspout.spoutapi.entity.SpoutWolf;

public class SpoutCraftWolf extends CraftWolf implements SpoutWolf{

	public SpoutCraftWolf(CraftServer server, EntityWolf wolf) {
		super(server, wolf);
	}

}
