package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowSpider;
import net.glowstone.util.nbt.CompoundTag;

public class SpiderStore extends MonsterStore<GlowSpider> {

    public SpiderStore() {
        super(GlowSpider.class, "Spider");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SpiderStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowSpider load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowSpider entity = new GlowSpider(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}