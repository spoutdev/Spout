package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.entity.Flying;

public abstract class GlowFlying extends GlowMonster implements Flying {

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowFlying(GlowServer server, GlowWorld world, int id) {
        super(server, world, id);
    }
}
