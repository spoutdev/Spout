package org.getspout.server.entity.monsters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;
import org.getspout.server.util.Parameter;

public class SpoutCreeper extends SpoutMonster implements Creeper {
	/**
	 * Whether this creeper is powered or not.
	 */
	private boolean powered;

	public SpoutCreeper(SpoutServer server, SpoutWorld world) {
		super(server, world, 50);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) loot.add(new ItemStack(ItemID.SULPHUR, amount));
		if (damager != null && damager instanceof Arrow) {
			if (((Arrow)damager).getShooter() instanceof Skeleton) {
				loot.add(new ItemStack(ItemID.DISC_13 + random.nextInt(2), 1));
			}
		}
		return loot;
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean value) {
		this.powered = value;
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)(powered ? 1 : 0)));
	}
}
