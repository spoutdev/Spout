package org.getspout.unchecked.server.entity.objects;

import java.util.List;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.Damager;

public class SpoutPrimedTNT extends SpoutExplosive implements TNTPrimed {
	private int fuseTicks;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world The world.
	 */
	public SpoutPrimedTNT(SpoutServer server, SpoutWorld world) {
		super(server, world, 50);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		return null;
	}

	@Override
	public void setFuseTicks(int fuseTicks) {
		this.fuseTicks = fuseTicks;
	}

	@Override
	public int getFuseTicks() {
		return fuseTicks;
	}
}
