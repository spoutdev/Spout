package org.getspout.server.entity.animals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutChicken extends SpoutAnimals implements Chicken {
	/**
	 * Creates a new monster.
	 *
	 * @param world The world this monster is in.
	 * @param type  The type of monster.
	 */
	public SpoutChicken(SpoutServer server, SpoutWorld world) {
		super(server, world, 93);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) {
			loot.add(new ItemStack(ItemID.FEATHER, amount));
			loot.add(new ItemStack(getFireTicks() > 0 ? ItemID.COOKED_CHICKEN : ItemID.RAW_CHICKEN, 1));
		}
		return loot;
	}
}
