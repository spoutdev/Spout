package org.getspout.spout.entity;

import net.minecraft.server.EntitySlime;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.getspout.spoutapi.entity.SpoutSlime;

public class SpoutCraftSlime extends CraftSlime implements SpoutSlime{

	public SpoutCraftSlime(CraftServer server, EntitySlime entity) {
		super(server, entity);
	}

}
