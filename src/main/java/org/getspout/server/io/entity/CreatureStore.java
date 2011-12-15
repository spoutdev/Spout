package org.getspout.server.io.entity;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutCreature;
import org.getspout.server.util.nbt.CompoundTag;

public abstract class CreatureStore<T extends SpoutCreature> extends EntityStore<T> {
    
    @SuppressWarnings("unchecked")
    public CreatureStore(Class<T> clazz, String id) {
        super(clazz, id);
    }
    
    public abstract T load(SpoutServer server, SpoutWorld world, CompoundTag compound);
    
}
