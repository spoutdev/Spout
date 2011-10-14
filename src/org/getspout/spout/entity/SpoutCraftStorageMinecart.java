package org.getspout.spout.entity;

import net.minecraft.server.EntityMinecart;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftStorageMinecart;
import org.getspout.spoutapi.entity.SpoutStorageMinecart;

public class SpoutCraftStorageMinecart extends CraftStorageMinecart implements SpoutStorageMinecart{

	public SpoutCraftStorageMinecart(CraftServer server, EntityMinecart entity) {
		super(server, entity);
	}

}
