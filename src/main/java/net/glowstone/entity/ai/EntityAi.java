package net.glowstone.entity.ai;

import net.glowstone.entity.GlowEntity;

public abstract class EntityAi<T extends GlowEntity> {
    protected final T entity;
    
    public EntityAi(T entity) {
        this.entity = entity;
    }
    
    public abstract boolean shouldRun();
    
    public abstract void runAi();
}
