package org.getspout.server.entity.monsters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutSpider extends SpoutMonster implements Spider {
	/**
	 * Creates a new spider.
	 *
	 * @param server This server this spider is on.
	 * @param world The world this spider is in.
	 */
	public SpoutSpider(SpoutServer server, SpoutWorld world) {
		super(server, world, 52);
	}

	protected SpoutSpider(SpoutServer server, SpoutWorld world, int id) {
		super(server, world, id);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int count = random.nextInt(3);
		if (count > 0) loot.add(new ItemStack(ItemID.STRING, count));
		count = random.nextInt(2);
		if (count > 0) loot.add(new ItemStack(ItemID.SPIDER_EYE, count));
		return loot;
	}
}
