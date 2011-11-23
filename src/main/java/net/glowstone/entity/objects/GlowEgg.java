package net.glowstone.entity.objects;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.entity.Egg;

public class GlowEgg extends GlowProjectile implements Egg {

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowEgg(GlowServer server, GlowWorld world) {
        super(server, world, 62);
    }
}
