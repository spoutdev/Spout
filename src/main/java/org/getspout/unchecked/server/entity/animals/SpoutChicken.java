package org.getspout.unchecked.server.entity.animals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.Damager;
import org.getspout.unchecked.server.item.ItemID;

public class SpoutChicken extends SpoutAnimals implements Chicken {
	/**
	 * Creates a new chicken.
	 *
	 * @param server The server this chicken is on.
	 * @param world The world this chicken is in.
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
		}
		loot.add(new ItemStack(getFireTicks() > 0 ? ItemID.COOKED_CHICKEN : ItemID.RAW_CHICKEN, 1));
		return loot;
	}
}
