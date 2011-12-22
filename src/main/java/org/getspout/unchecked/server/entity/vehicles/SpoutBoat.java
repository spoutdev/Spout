package org.getspout.unchecked.server.entity.vehicles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Boat;
import org.bukkit.inventory.ItemStack;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.block.BlockID;
import org.getspout.unchecked.server.entity.Damager;
import org.getspout.unchecked.server.item.ItemID;

public class SpoutBoat extends SpoutVehicle implements Boat {
	private boolean workOnLand;
	private double unoccupiedDeceleration, occupiedDeceleration;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world The world.
	 */
	public SpoutBoat(SpoutServer server, SpoutWorld world) {
		super(server, world, 41);
		maxSpeed = 0.4;
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		loot.add(new ItemStack(BlockID.WOOD, 3));
		loot.add(new ItemStack(ItemID.STICK, 2));
		return loot;
	}

	@Override
	public double getOccupiedDeceleration() {
		return occupiedDeceleration;
	}

	@Override
	public void setOccupiedDeceleration(double rate) {
		occupiedDeceleration = rate;
	}

	@Override
	public double getUnoccupiedDeceleration() {
		return unoccupiedDeceleration;
	}

	@Override
	public void setUnoccupiedDeceleration(double rate) {
		unoccupiedDeceleration = rate;
	}

	@Override
	public boolean getWorkOnLand() {
		return workOnLand;
	}

	@Override
	public void setWorkOnLand(boolean workOnLand) {
		this.workOnLand = workOnLand;
	}
}
