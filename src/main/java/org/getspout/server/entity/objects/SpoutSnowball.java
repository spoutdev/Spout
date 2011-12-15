package org.getspout.server.entity.objects;

import org.bukkit.entity.Snowball;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutSnowball extends SpoutProjectile implements Snowball{
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public SpoutSnowball(SpoutServer server, SpoutWorld world) {
        super(server, world, 11);
    }
}
