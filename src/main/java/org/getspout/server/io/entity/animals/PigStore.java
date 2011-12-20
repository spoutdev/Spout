package org.getspout.server.io.entity.animals;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.animals.SpoutPig;
import org.getspout.server.util.nbt.CompoundTag;

public class PigStore extends AnimalsStore<SpoutPig> {
	public PigStore() {
		super(SpoutPig.class, "Pig");
	}

	@Override
	public SpoutPig load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutPig entity = new SpoutPig(server, world);

		super.load(entity, compound);

		return entity;
	}
}