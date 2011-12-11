package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowCaveSpider;
import net.glowstone.util.nbt.CompoundTag;

public class CaveSpiderStore extends SpiderStore {

    public CaveSpiderStore() {
        super(GlowCaveSpider.class, "CaveSpider");
    }
    
    public GlowCaveSpider load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowCaveSpider entity = new GlowCaveSpider(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}