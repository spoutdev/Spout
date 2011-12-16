package org.getspout.server.io.entity.animals;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.animals.SpoutAnimals;
import org.getspout.server.io.entity.CreatureStore;
import org.getspout.server.util.nbt.CompoundTag;

public abstract class AnimalsStore<T extends SpoutAnimals> extends CreatureStore<T> {
	public AnimalsStore(Class<T> clazz, String id) {
		super(clazz, id);
	}

	public abstract T load(SpoutServer server, SpoutWorld world, CompoundTag compound);
}