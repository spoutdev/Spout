package org.getspout.spout.entity;

import net.minecraft.server.EntityMinecart;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPoweredMinecart;
import org.getspout.spoutapi.entity.SpoutPoweredMinecart;

public class SpoutCraftPoweredMinecart extends CraftPoweredMinecart implements SpoutPoweredMinecart{

	public SpoutCraftPoweredMinecart(CraftServer server, EntityMinecart entity) {
		super(server, entity);
	}

}
