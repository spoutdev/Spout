package org.getspout.unchecked.server.entity.water;

import org.bukkit.entity.WaterMob;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.SpoutCreature;

public abstract class SpoutWaterMob extends SpoutCreature implements WaterMob {
	/**
	 * Represents a water mob.
	 *
	 * @param server This server this water mob is on.
	 * @param world The world this water mob is in.
	 * @param type The type of water mob.
	 */
	public SpoutWaterMob(SpoutServer server, SpoutWorld world, int type) {
		super(server, world, type);
	}
}
