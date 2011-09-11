package org.getspout.spout.entity;

import net.minecraft.server.EntityWeather;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftWeather;
import org.getspout.spoutapi.entity.SpoutWeather;

public class SpoutCraftWeather extends CraftWeather implements SpoutWeather{

	public SpoutCraftWeather(CraftServer server, EntityWeather entity) {
		super(server, entity);
	}

}
