package org.getspout.server.io.entity.animals;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.animals.SpoutSheep;
import org.getspout.server.util.nbt.CompoundTag;

public class SheepStore extends AnimalsStore<SpoutSheep> {
	public SheepStore() {
		super(SpoutSheep.class, "Sheep");
	}

	public SpoutSheep load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSheep entity = new SpoutSheep(server, world);

		super.load(entity, compound);

		return entity;
	}
}