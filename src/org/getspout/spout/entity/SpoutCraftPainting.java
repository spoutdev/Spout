package org.getspout.spout.entity;

import net.minecraft.server.EntityPainting;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPainting;
import org.getspout.spoutapi.entity.SpoutPainting;

public class SpoutCraftPainting extends CraftPainting implements SpoutPainting{

	public SpoutCraftPainting(CraftServer server, EntityPainting entity) {
		super(server, entity);
	}

}
