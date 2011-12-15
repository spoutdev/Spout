package org.getspout.server.entity.vehicles;

import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutStorageMinecart extends SpoutMinecart implements StorageMinecart{
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param world The world.
     */
    public SpoutStorageMinecart(SpoutServer server, SpoutWorld world) {
        super(server, world, 11);
    }

    public Inventory getInventory() {
        throw new UnsupportedOperationException("Not supported yet!");
    }
}
