package org.getspout.unchecked.server.entity.objects;

import org.bukkit.entity.Egg;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

public class SpoutEgg extends SpoutProjectile implements Egg {
	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world The world.
	 */
	public SpoutEgg(SpoutServer server, SpoutWorld world) {
		super(server, world, 62);
	}
}
