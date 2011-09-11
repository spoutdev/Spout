package org.getspout.spout.entity;

import net.minecraft.server.EntityHuman;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.getspout.spoutapi.entity.SpoutHumanEntity;

public class SpoutCraftHumanEntity extends CraftHumanEntity implements SpoutHumanEntity{
	public SpoutCraftHumanEntity(CraftServer server, EntityHuman entity) {
		super(server, entity);
	}
}
