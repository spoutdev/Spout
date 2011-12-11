package net.glowstone.io.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.animals.GlowSheep;
import net.glowstone.util.nbt.CompoundTag;

public class SheepStore extends AnimalsStore<GlowSheep> {

    public SheepStore() {
        super(GlowSheep.class, "Sheep");
    }
    
    public GlowSheep load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowSheep entity = new GlowSheep(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}