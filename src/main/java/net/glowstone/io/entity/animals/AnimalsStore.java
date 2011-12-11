package net.glowstone.io.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.animals.GlowAnimals;
import net.glowstone.io.entity.CreatureStore;
import net.glowstone.util.nbt.CompoundTag;

public abstract class AnimalsStore<T extends GlowAnimals> extends CreatureStore<T> {
    
    public AnimalsStore(Class<T> clazz, String id) {
        super(clazz, id);
    }
    
    public abstract T load(GlowServer server, GlowWorld world, CompoundTag compound);
    
}