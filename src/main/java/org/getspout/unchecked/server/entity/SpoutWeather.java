package org.getspout.unchecked.server.entity;

import org.bukkit.entity.Weather;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

/**
 * Represents a Weather related entity, such as a storm.
 */
public abstract class SpoutWeather extends SpoutEntity implements Weather {
	public SpoutWeather(SpoutServer server, SpoutWorld world) {
		super(server, world);
	}
}
