package net.glowstone.io.entity.monsters;

import net.glowstone.entity.GlowCreature;
import net.glowstone.io.entity.CreatureStore;

public abstract class MonsterStore<T extends GlowCreature> extends CreatureStore<T> {
    
    public MonsterStore(Class<T> clazz, String id) {
        super(clazz, id);
    }
}
