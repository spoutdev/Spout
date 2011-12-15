package org.getspout.server.entity.vehicles;

import org.bukkit.entity.PoweredMinecart;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutPoweredMinecart extends SpoutMinecart implements PoweredMinecart {
    
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server
     * @param world The world.
     */
    public SpoutPoweredMinecart(SpoutServer server, SpoutWorld world) {
        super(server, world, 12);
    }
}
