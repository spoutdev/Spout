package org.getspout.server.entity.animals;

import org.bukkit.entity.Animals;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutCreature;

public abstract class SpoutAnimals extends SpoutCreature implements Animals {
	/**
	 * Represents an animal.
	 *
	 * @param world The world this animal is in.
	 * @param world The world this animal is in.
	 * @param type The type of animal.
	 */
	public SpoutAnimals(SpoutServer server, SpoutWorld world, int type) {
		super(server, world, type);
	}
}
