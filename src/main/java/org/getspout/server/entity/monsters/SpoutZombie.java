package org.getspout.server.entity.monsters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutZombie extends SpoutMonster implements Zombie {
	public SpoutZombie(SpoutServer server, SpoutWorld world) {
		super(server, world, 54);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) loot.add(new ItemStack(ItemID.ROTTEN_FLESH, amount));
		return loot;
	}

	protected SpoutZombie(SpoutServer server, SpoutWorld world, int id) {
		super(server, world, id);
	}
}
