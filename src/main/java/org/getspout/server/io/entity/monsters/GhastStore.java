package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutGhast;
import org.getspout.server.util.nbt.CompoundTag;

public class GhastStore extends FlyingStore<SpoutGhast> {
	public GhastStore() {
		super(SpoutGhast.class, "Ghast");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public GhastStore(Class clazz, String id) {
		super(clazz, id);
	}

	@Override
	public SpoutGhast load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutGhast entity = new SpoutGhast(server, world);

		super.load(entity, compound);

		return entity;
	}
}