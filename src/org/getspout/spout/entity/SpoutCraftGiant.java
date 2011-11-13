package org.getspout.spout.entity;

import net.minecraft.server.EntityGiantZombie;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftGiant;
import org.getspout.spoutapi.entity.SpoutGiant;

public class SpoutCraftGiant extends CraftGiant implements SpoutGiant{

	public SpoutCraftGiant(CraftServer server, EntityGiantZombie entity) {
		super(server, entity);
	}

}
