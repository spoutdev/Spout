package org.getspout.spout.entity;

import net.minecraft.server.EntityBoat;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftBoat;
import org.getspout.spoutapi.entity.SpoutBoat;

public class SpoutCraftBoat extends CraftBoat implements SpoutBoat{

	public SpoutCraftBoat(CraftServer server, EntityBoat entity) {
		super(server, entity);
	}

}
