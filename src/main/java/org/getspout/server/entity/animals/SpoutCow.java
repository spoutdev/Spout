package org.getspout.server.entity.animals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Cow;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutCow extends SpoutAnimals implements Cow {
	/**
	 * Creates a new cow.
	 *
	 * @param server This server this cow is on.
	 * @param world The world this cow is in.
	 */
	public SpoutCow(SpoutServer server, SpoutWorld world) {
		super(server, world, 5);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) {
			loot.add(new ItemStack(ItemID.LEATHER, amount));
		}
		amount = random.nextInt(3) + 1;
		if (amount > 0) {
			loot.add(new ItemStack(getFireTicks() > 0 ? ItemID.COOKED_BEEF : ItemID.RAW_BEEF, amount));
		}
		return loot;
	}
}
