package org.getspout.unchecked.server.io.entity.animals;

import org.getspout.api.io.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.animals.SpoutSheep;

public class SheepStore extends AnimalsStore<SpoutSheep> {
	public SheepStore() {
		super(SpoutSheep.class, "Sheep");
	}

	@Override
	public SpoutSheep load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSheep entity = new SpoutSheep(server, world);

		super.load(entity, compound);

		return entity;
	}
}