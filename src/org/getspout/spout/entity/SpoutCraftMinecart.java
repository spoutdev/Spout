package org.getspout.spout.entity;

import net.minecraft.server.EntityMinecart;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftMinecart;
import org.getspout.spoutapi.entity.SpoutMinecart;

public class SpoutCraftMinecart extends CraftMinecart implements SpoutMinecart{

	public SpoutCraftMinecart(CraftServer server, EntityMinecart entity) {
		super(server, entity);
	}

}
