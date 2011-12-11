package net.glowstone.io.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.animals.GlowPig;
import net.glowstone.util.nbt.CompoundTag;

public class PigStore extends AnimalsStore<GlowPig> {

    public PigStore() {
        super(GlowPig.class, "Pig");
    }
    
    public GlowPig load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowPig entity = new GlowPig(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}