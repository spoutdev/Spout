package org.getspout.unchecked.server.entity.ai;

import org.getspout.unchecked.server.entity.SpoutEntity;

public abstract class EntityAi<T extends SpoutEntity> {
	protected final T entity;

	public EntityAi(T entity) {
		this.entity = entity;
	}

	public abstract boolean shouldRun();

	public abstract void runAi();
}
