package org.getspout.unchecked.server.entity.monsters;

import java.util.List;

import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.ItemStack;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.Damager;

public class SpoutSilverfish extends SpoutMonster implements Silverfish {
	/**
	 * Creates a new silverfish.
	 *
	 * @param server This server this silverfish is on.
	 * @param world The world this silverfish is in.
	 */
	public SpoutSilverfish(SpoutServer server, SpoutWorld world) {
		super(server, world, 60);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		return null;
	}
}
