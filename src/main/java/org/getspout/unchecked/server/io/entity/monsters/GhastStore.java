package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutGhast;
import org.getspout.unchecked.server.util.nbt.CompoundTag;

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