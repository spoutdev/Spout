package org.getspout.unchecked.server.io.entity;

import org.getspout.api.io.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.SpoutCreature;

public abstract class CreatureStore<T extends SpoutCreature> extends EntityStore<T> {
	@SuppressWarnings("unchecked")
	public CreatureStore(Class<T> clazz, String id) {
		super(clazz, id);
	}

	@Override
	public abstract T load(SpoutServer server, SpoutWorld world, CompoundTag compound);
}
