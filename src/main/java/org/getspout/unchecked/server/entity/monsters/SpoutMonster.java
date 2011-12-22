package org.getspout.unchecked.server.entity.monsters;

import org.bukkit.entity.Monster;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.SpoutCreature;

public abstract class SpoutMonster extends SpoutCreature implements Monster {
	/**
	 * Represents a monster.
	 *
	 * @param server This server this monster is on.
	 * @param world The world this monster is in.
	 * @param type The type of monster.
	 */
	protected SpoutMonster(SpoutServer server, SpoutWorld world, int type) {
		super(server, world, type);
	}
}
