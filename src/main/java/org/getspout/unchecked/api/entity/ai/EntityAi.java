package org.getspout.unchecked.api.entity.ai;

import org.getspout.unchecked.api.entity.Entity;

public abstract class EntityAi<T extends Entity> {
	protected final T entity;

	public EntityAi(T entity) {
		this.entity = entity;
	}

	public abstract boolean shouldRun();

	public abstract void runAi();

}
