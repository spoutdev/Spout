package org.getspout.spout.entity;

import net.minecraft.server.EntityArrow;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.getspout.spoutapi.entity.SpoutArrow;

public class SpoutCraftArrow extends CraftArrow implements SpoutArrow{

	public SpoutCraftArrow(CraftServer server, EntityArrow entity) {
		super(server, entity);
	}

}
