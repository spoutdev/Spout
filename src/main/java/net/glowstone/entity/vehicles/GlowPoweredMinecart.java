package net.glowstone.entity.vehicles;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.entity.PoweredMinecart;

public class GlowPoweredMinecart extends GlowMinecart implements PoweredMinecart {
    
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server
     * @param world The world.
     */
    public GlowPoweredMinecart(GlowServer server, GlowWorld world) {
        super(server, world, 12);
    }
}
