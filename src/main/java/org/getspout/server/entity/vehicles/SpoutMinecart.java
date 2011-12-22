package org.getspout.server.entity.vehicles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

public class SpoutMinecart extends SpoutVehicle implements Minecart {
	private int damage = 0;

	protected boolean slowWhenEmpty;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world The world.
	 */
	public SpoutMinecart(SpoutServer server, SpoutWorld world) {
		super(server, world, 10);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(ItemID.MINECART));
		return items;
	}

	protected SpoutMinecart(SpoutServer server, SpoutWorld world, int id) {
		super(server, world, id);
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getDamage() {
		return damage;
	}

	public boolean isSlowWhenEmpty() {
		return slowWhenEmpty;
	}

	public void setSlowWhenEmpty(boolean slow) {
		slowWhenEmpty = slow;
	}

	public Vector getFlyingVelocityMod() {
		throw new UnsupportedOperationException("Not supported yet!");
	}

	public void setFlyingVelocityMod(Vector flying) {
		throw new UnsupportedOperationException("Not supported yet!");
	}

	public Vector getDerailedVelocityMod() {
		throw new UnsupportedOperationException("Not supported yet!");
	}

	public void setDerailedVelocityMod(Vector derailed) {
		throw new UnsupportedOperationException("Not supported yet!");
	}
}
