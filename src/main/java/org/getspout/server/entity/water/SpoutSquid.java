package org.getspout.server.entity.water;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Squid;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutSquid extends SpoutWaterMob implements Squid {
	/**
	 * Creates a new monster.
	 *
	 * @param world The world this monster is in.
	 * @param server The server this entity is part of
	 */
	public SpoutSquid(SpoutServer server, SpoutWorld world) {
		super(server, world, 94);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int count = random.nextInt(3) + 1;
		if (count > 0) {
			loot.add(new ItemStack(ItemID.INK_SACK, count));
		}
		return loot;
	}
}
