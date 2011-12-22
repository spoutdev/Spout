package org.getspout.unchecked.server.entity.vehicles;

import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

public class SpoutStorageMinecart extends SpoutMinecart implements StorageMinecart {
	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param world The world.
	 */
	public SpoutStorageMinecart(SpoutServer server, SpoutWorld world) {
		super(server, world, 11);
	}

	@Override
	public Inventory getInventory() {
		throw new UnsupportedOperationException("Not supported yet!");
	}
}
