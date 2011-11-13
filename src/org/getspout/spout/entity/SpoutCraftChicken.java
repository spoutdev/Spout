package org.getspout.spout.entity;

import net.minecraft.server.EntityChicken;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftChicken;
import org.getspout.spoutapi.entity.SpoutChicken;

public class SpoutCraftChicken extends CraftChicken implements SpoutChicken{

	public SpoutCraftChicken(CraftServer server, EntityChicken entity) {
		super(server, entity);
	}

}
