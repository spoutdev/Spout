package org.getspout.unchecked.server.entity.animals;

import org.bukkit.entity.Animals;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.SpoutCreature;

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
