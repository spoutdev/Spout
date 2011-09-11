package org.getspout.spout.entity;

import net.minecraft.server.EntityWeatherStorm;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftLightningStrike;
import org.getspout.spoutapi.entity.SpoutLightningStrike;

public class SpoutCraftLightningStrike extends CraftLightningStrike implements SpoutLightningStrike{

	public SpoutCraftLightningStrike(CraftServer server, EntityWeatherStorm entity) {
		super(server, entity);
	}

}
