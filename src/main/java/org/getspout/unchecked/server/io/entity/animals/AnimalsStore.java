package org.getspout.unchecked.server.io.entity.animals;

import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.animals.SpoutAnimals;
import org.getspout.unchecked.server.io.entity.CreatureStore;
import org.getspout.unchecked.server.util.nbt.CompoundTag;

public abstract class AnimalsStore<T extends SpoutAnimals> extends CreatureStore<T> {
	public AnimalsStore(Class<T> clazz, String id) {
		super(clazz, id);
	}

	@Override
	public abstract T load(SpoutServer server, SpoutWorld world, CompoundTag compound);
}