package org.getspout.server.entity.monsters;

import org.bukkit.entity.Giant;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutGiant extends SpoutZombie implements Giant {
	/**
	 * Creates a new giant.
	 *
	 * @param server This server this giant is on.
	 * @param world The world this giant is in.
	 */
	public SpoutGiant(SpoutServer server, SpoutWorld world) {
		super(server, world, 53);
	}

	public boolean isStupid() {
		return true;
	}
}
