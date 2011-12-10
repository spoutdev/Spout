package net.glowstone.entity.objects;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.entity.Snowball;

public class GlowSnowball extends GlowProjectile implements Snowball{
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowSnowball(GlowServer server, GlowWorld world) {
        super(server, world, 11);
    }
}
