package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutGiant;

public class GiantStore extends ZombieStore {
	public GiantStore() {
		super(SpoutGiant.class, "Giant");
	}

	public GiantStore(Class<?> clazz, String id) {
		super(clazz, id);
	}

	@Override
	public SpoutGiant load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutGiant entity = new SpoutGiant(server, world);

		super.load(entity, compound);

		return entity;
	}
}