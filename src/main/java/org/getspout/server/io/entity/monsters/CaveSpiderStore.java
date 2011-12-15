package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutCaveSpider;
import org.getspout.server.util.nbt.CompoundTag;

public class CaveSpiderStore extends SpiderStore {

    public CaveSpiderStore() {
        super(SpoutCaveSpider.class, "CaveSpider");
    }
    
    public SpoutCaveSpider load(SpoutServer server, SpoutWorld world, CompoundTag compound) {
        
        SpoutCaveSpider entity = new SpoutCaveSpider(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}