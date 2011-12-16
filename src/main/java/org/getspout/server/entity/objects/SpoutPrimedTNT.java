package org.getspout.server.entity.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;

public class SpoutPrimedTNT extends SpoutExplosive implements TNTPrimed {
	private int fuseTicks;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world  The world.
	 */
	public SpoutPrimedTNT(SpoutServer server, SpoutWorld world) {
		super(server, world, 50);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		return null;
	}

	public void setFuseTicks(int fuseTicks) {
		this.fuseTicks = fuseTicks;
	}

	public int getFuseTicks() {
		return fuseTicks;
	}
}
