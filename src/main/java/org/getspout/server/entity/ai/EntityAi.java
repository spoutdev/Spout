package org.getspout.server.entity.ai;

import org.getspout.server.entity.SpoutEntity;

public abstract class EntityAi<T extends SpoutEntity> {
    protected final T entity;
    
    public EntityAi(T entity) {
        this.entity = entity;
    }
    
    public abstract boolean shouldRun();
    
    public abstract void runAi();
}
