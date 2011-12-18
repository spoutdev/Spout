package org.getspout.server.entity.monsters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutSkeleton extends SpoutMonster implements Skeleton {
	/**
	 * Creates a new skeleton.
	 *
	 * @param server This server this skeleton is on.
	 * @param world The world this skeleton is in.
	 */
	public SpoutSkeleton(SpoutServer server, SpoutWorld world) {
		super(server, world, 51);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();

		int count = this.random.nextInt(3);
		if (count > 0) loot.add(new ItemStack(ItemID.ARROW, count));
		count = this.random.nextInt(3);
		if (count > 0) loot.add(new ItemStack(ItemID.BONE, count));
		return loot;
	}
}
