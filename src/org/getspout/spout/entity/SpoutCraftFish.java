package org.getspout.spout.entity;

import net.minecraft.server.EntityFish;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftFish;
import org.getspout.spoutapi.entity.SpoutFish;

public class SpoutCraftFish extends CraftFish implements SpoutFish{

	public SpoutCraftFish(CraftServer server, EntityFish entity) {
		super(server, entity);
	}

}
