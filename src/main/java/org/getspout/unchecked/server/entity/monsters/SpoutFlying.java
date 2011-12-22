package org.getspout.unchecked.server.entity.monsters;

import org.bukkit.entity.Flying;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

public abstract class SpoutFlying extends SpoutMonster implements Flying {
	/**
	 * Represents a flying entity.
	 *
	 * @param server The server this flying entity is on.
	 * @param world The world this flying entity is in.
	 */
	public SpoutFlying(SpoutServer server, SpoutWorld world, int id) {
		super(server, world, id);
	}
}
