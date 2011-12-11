package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowGhast;
import net.glowstone.util.nbt.CompoundTag;

public class GhastStore extends FlyingStore<GlowGhast> {

    public GhastStore() {
        super(GlowGhast.class, "Ghast");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public GhastStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowGhast load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowGhast entity = new GlowGhast(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}