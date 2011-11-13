package org.getspout.spout.entity;

import net.minecraft.server.EntityCreeper;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.getspout.spoutapi.entity.SpoutCreeper;

public class SpoutCraftCreeper extends CraftCreeper implements SpoutCreeper{

	public SpoutCraftCreeper(CraftServer server, EntityCreeper entity) {
		super(server, entity);
	}

}
