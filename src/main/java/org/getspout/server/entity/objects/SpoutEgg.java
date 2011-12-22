package org.getspout.server.entity.objects;

import org.bukkit.entity.Egg;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutEgg extends SpoutProjectile implements Egg {
	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world  The world.
	 */
	public SpoutEgg(SpoutServer server, SpoutWorld world) {
		super(server, world, 62);
	}
}
