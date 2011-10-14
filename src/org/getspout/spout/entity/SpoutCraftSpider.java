package org.getspout.spout.entity;

import net.minecraft.server.EntitySpider;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.getspout.spoutapi.entity.SpoutSpider;

public class SpoutCraftSpider extends CraftSpider implements SpoutSpider{

	public SpoutCraftSpider(CraftServer server, EntitySpider entity) {
		super(server, entity);
	}

}
