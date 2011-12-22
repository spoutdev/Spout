package org.getspout.server.entity.monsters;

import org.bukkit.entity.Monster;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutCreature;

public abstract class SpoutMonster extends SpoutCreature implements Monster {
	protected SpoutMonster(SpoutServer server, SpoutWorld world, int id) {
		super(server, world, id);
	}
}
