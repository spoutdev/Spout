package org.getspout.unchecked.server.entity.monsters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.Damager;
import org.getspout.unchecked.server.item.ItemID;
import org.getspout.unchecked.server.util.Parameter;

public class SpoutSlime extends SpoutMonster implements Slime {
	private int size;
	private int[] sizes = new int[] {0, 1, 2, 4};

	/**
	 * Creates a new slime.
	 *
	 * @param server This server this slime is on.
	 * @param world The world this slime is in.
	 */
	public SpoutSlime(SpoutServer server, SpoutWorld world) {
		super(server, world, 55);
		setSize(sizes[new Random().nextInt(sizes.length)]);
	}

	protected SpoutSlime(SpoutServer server, SpoutWorld world, int id) {
		super(server, world, id);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public void setSize(int sz) {
		size = sz;
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte) size));
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0 && getSize() == 1) {
			loot.add(new ItemStack(ItemID.SLIME_BALL, amount));
		}
		return loot;
	}
}
