package org.getspout.spout.entity;

import net.minecraft.server.EntityWeatherLighting;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftLightningStrike;
import org.getspout.spoutapi.entity.SpoutLightningStrike;

public class SpoutCraftLightningStrike extends CraftLightningStrike implements SpoutLightningStrike{

	public SpoutCraftLightningStrike(CraftServer server, EntityWeatherLighting entity) {
		super(server, entity);
	}

}
