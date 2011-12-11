package net.glowstone.io.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.animals.GlowChicken;
import net.glowstone.util.nbt.CompoundTag;

public class ChickenStore extends AnimalStore<GlowChicken> {

    public ChickenStore() {
        super(GlowChicken.class, "Chicken");
    }
    
    public GlowChicken load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowChicken entity = new GlowChicken(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}
