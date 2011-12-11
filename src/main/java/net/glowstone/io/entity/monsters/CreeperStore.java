package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowCreeper;
import net.glowstone.util.nbt.CompoundTag;

public class CreeperStore extends MonsterStore<GlowCreeper> {

    public CreeperStore() {
        super(GlowCreeper.class, "Creeper");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public CreeperStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowCreeper load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowCreeper entity = new GlowCreeper(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}