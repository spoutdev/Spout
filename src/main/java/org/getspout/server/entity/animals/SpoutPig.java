package org.getspout.server.entity.animals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Pig;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;
import org.getspout.server.util.Parameter;

public class SpoutPig extends SpoutAnimals implements Pig {
	/**
	 * Whether this pig has a saddle
	 */
	private boolean saddled;

	/**
	 * Creates a new pig.
	 *
	 * @param server This server this pig is on.
	 * @param world The world this pig is in.
	 */
	public SpoutPig(SpoutServer server, SpoutWorld world) {
		super(server, world, 90);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) {
			loot.add(new ItemStack(getFireTicks() > 0 ? ItemID.GRILLED_PORK : ItemID.PORK, amount));
		}
		return loot;
	}

	public boolean hasSaddle() {
		return saddled;
	}

	public void setSaddle(boolean saddled) {
		this.saddled = saddled;
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)(this.saddled ? 1 : 0)));
	}
}
