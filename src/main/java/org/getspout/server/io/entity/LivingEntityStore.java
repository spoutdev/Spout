package org.getspout.server.io.entity;

import org.getspout.server.entity.SpoutLivingEntity;

public abstract class LivingEntityStore<T extends SpoutLivingEntity> extends EntityStore<T> {
	public LivingEntityStore(Class<T> clazz, String id) {
		super(clazz, id);
	}
}
