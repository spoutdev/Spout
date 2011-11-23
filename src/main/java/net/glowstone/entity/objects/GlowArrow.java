package net.glowstone.entity.objects;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.entity.Arrow;


public class GlowArrow extends GlowProjectile implements Arrow {
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowArrow(GlowServer server, GlowWorld world) {
        super(server, world, 10);
    }
}
