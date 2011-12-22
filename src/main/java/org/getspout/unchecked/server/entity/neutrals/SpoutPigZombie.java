package org.getspout.unchecked.server.entity.neutrals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.Angerable;
import org.getspout.unchecked.server.entity.Damager;
import org.getspout.unchecked.server.entity.monsters.SpoutZombie;
import org.getspout.unchecked.server.item.ItemID;

public class SpoutPigZombie extends SpoutZombie implements PigZombie, Angerable {
	private int anger;
	private boolean angry;

	/**
	 * Creates a new pig zombie.
	 *
	 * @param server This server this pig zombie is on.
	 * @param world The world this pig zombie is in.
	 */
	public SpoutPigZombie(SpoutServer server, SpoutWorld world) {
		super(server, world, 57);
	}

	@Override
	public int getAnger() {
		return anger;
	}

	@Override
	public void setAnger(int level) {
		anger = level;
	}

	@Override
	public void setAngry(boolean angry) {
		this.angry = angry;
	}

	@Override
	public boolean isAngry() {
		return angry;
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) {
			loot.add(new ItemStack(ItemID.ROTTEN_FLESH, amount));
		}
		return loot;
	}
}
