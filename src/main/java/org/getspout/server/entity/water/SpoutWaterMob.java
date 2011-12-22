package org.getspout.server.entity.water;

import org.bukkit.entity.WaterMob;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutCreature;

public abstract class SpoutWaterMob extends SpoutCreature implements WaterMob {
	/**
	 * Creates a new monster.
	 *
	 * @param world The world this monster is in.
	 * @param type  The type of monster.
	 */
	public SpoutWaterMob(SpoutServer server, SpoutWorld world, int type) {
		super(server, world, type);
	}
}
