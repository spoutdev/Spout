package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowGiant;
import net.glowstone.util.nbt.CompoundTag;

public class GiantStore extends ZombieStore {

    public GiantStore() {
        super(GlowGiant.class, "Giant");
    }
    
    public GiantStore(Class<?> clazz, String id) {
        super(clazz, id);
    }
    
    public GlowGiant load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowGiant entity = new GlowGiant(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}