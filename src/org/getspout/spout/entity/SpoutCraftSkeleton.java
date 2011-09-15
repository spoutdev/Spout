package org.getspout.spout.entity;

import net.minecraft.server.EntitySkeleton;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.getspout.spoutapi.entity.SpoutSkeleton;

public class SpoutCraftSkeleton extends CraftSkeleton implements SpoutSkeleton{

	public SpoutCraftSkeleton(CraftServer server, EntitySkeleton entity) {
		super(server, entity);
	}

}
