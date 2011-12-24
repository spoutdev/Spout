package org.getspout.unchecked.server.entity.monsters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.getspout.api.util.Parameter;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.Damager;
import org.getspout.unchecked.server.item.ItemID;

public class SpoutCreeper extends SpoutMonster implements Creeper {
	/**
	 * Whether this creeper is powered or not.
	 */
	private boolean powered;

	/**
	 * Creates a new creeper.
	 *
	 * @param server This server this creeper is on.
	 * @param world The world this creeper is in.
	 */
	public SpoutCreeper(SpoutServer server, SpoutWorld world) {
		super(server, world, 50);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) {
			loot.add(new ItemStack(ItemID.SULPHUR, amount));
		}
		if (damager != null && damager instanceof Arrow) {
			if (((Arrow) damager).getShooter() instanceof Skeleton) {
				loot.add(new ItemStack(ItemID.DISC_13 + random.nextInt(2), 1));
			}
		}
		return loot;
	}

	@Override
	public boolean isPowered() {
		return powered;
	}

	@Override
	public void setPowered(boolean value) {
		powered = value;
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte) (powered ? 1 : 0)));
	}
}
