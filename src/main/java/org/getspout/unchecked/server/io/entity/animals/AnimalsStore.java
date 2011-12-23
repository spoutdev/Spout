package org.getspout.unchecked.server.io.entity.animals;

import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.animals.SpoutAnimals;
import org.getspout.unchecked.server.io.entity.CreatureStore;

public abstract class AnimalsStore<T extends SpoutAnimals> extends CreatureStore<T> {
	public AnimalsStore(Class<T> clazz, String id) {
		super(clazz, id);
	}

	@Override
	public abstract T load(SpoutServer server, SpoutWorld world, CompoundTag compound);
}