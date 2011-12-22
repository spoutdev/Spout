package org.getspout.server.entity.monsters;

import java.util.List;

import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;

public class SpoutSilverfish extends SpoutMonster implements Silverfish {
	public SpoutSilverfish(SpoutServer server, SpoutWorld world) {
		super(server, world, 60);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		return null;
	}
}
