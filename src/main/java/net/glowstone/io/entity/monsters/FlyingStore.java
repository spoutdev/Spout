package net.glowstone.io.entity.monsters;

import net.glowstone.entity.monsters.GlowMonster;

public abstract class FlyingStore<T extends GlowMonster> extends MonsterStore<T> {
    
    public FlyingStore(Class<T> clazz, String id) {
        super(clazz, id);
    }
}