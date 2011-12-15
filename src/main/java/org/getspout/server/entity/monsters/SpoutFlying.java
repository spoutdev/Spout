package org.getspout.server.entity.monsters;

import org.bukkit.entity.Flying;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public abstract class SpoutFlying extends SpoutMonster implements Flying {

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public SpoutFlying(SpoutServer server, SpoutWorld world, int id) {
        super(server, world, id);
    }
}
