package org.getspout.unchecked.server.io.entity.animals;

import org.getspout.api.io.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.animals.SpoutCow;

public class CowStore extends AnimalsStore<SpoutCow> {
	public CowStore() {
		super(SpoutCow.class, "Cow");
	}

	@Override
	public SpoutCow load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutCow entity = new SpoutCow(server, world);

		super.load(entity, compound);

		return entity;
	}
}
