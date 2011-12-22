package org.getspout.unchecked.server.io.entity.animals;

import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.animals.SpoutChicken;
import org.getspout.unchecked.server.util.nbt.CompoundTag;

public class ChickenStore extends AnimalsStore<SpoutChicken> {
	public ChickenStore() {
		super(SpoutChicken.class, "Chicken");
	}

	@Override
	public SpoutChicken load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutChicken entity = new SpoutChicken(server, world);

		super.load(entity, compound);

		return entity;
	}
}
