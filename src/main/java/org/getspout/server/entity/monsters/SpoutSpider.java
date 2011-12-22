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
		return loot;
	}
}
