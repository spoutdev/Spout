package org.getspout.server.entity.monsters;

import org.bukkit.entity.CaveSpider;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutCaveSpider extends SpoutSpider implements CaveSpider {
	/**
	 * Creates a new cave spider.
	 *
	 * @param server This server this cave spider is on.
	 * @param world The world this cave spider is in.
	 */
	public SpoutCaveSpider(SpoutServer server, SpoutWorld world) {
		super(server, world, 59);
	}
}
