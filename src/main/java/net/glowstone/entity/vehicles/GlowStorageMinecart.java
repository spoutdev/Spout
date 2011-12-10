package net.glowstone.entity.vehicles;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;

public class GlowStorageMinecart extends GlowMinecart implements StorageMinecart{
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param world The world.
     */
    public GlowStorageMinecart(GlowServer server, GlowWorld world) {
        super(server, world, 11);
    }

    public Inventory getInventory() {
        throw new UnsupportedOperationException("Not supported yet!");
    }
}
