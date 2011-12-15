package org.getspout.server.entity.monsters;


import org.bukkit.entity.CaveSpider;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutCaveSpider extends SpoutSpider implements CaveSpider {
    
    public SpoutCaveSpider(SpoutServer server, SpoutWorld world) {
        super(server, world, 59);
    }
}
