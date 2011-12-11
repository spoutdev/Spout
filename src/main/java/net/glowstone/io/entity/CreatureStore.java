package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowCreature;
import net.glowstone.util.nbt.CompoundTag;

public abstract class CreatureStore<T extends GlowCreature> extends EntityStore<T> {
    
    @SuppressWarnings("unchecked")
    public CreatureStore(Class<T> clazz, String id) {
        super(clazz, id);
    }
    
    public abstract T load(GlowServer server, GlowWorld world, CompoundTag compound);
    
}
