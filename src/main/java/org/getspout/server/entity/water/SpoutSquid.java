package org.getspout.server.entity.water;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Squid;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutSquid extends SpoutWaterMob implements Squid {
	/**
	 * Creates a new squid.
	 *
	 * @param server This server this squid is on.
	 * @param world The world this squid is in.
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
