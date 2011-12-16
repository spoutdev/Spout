package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutCreeper;
import org.getspout.server.util.nbt.CompoundTag;

public class CreeperStore extends MonsterStore<SpoutCreeper> {
	public CreeperStore() {
		super(SpoutCreeper.class, "Creeper");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CreeperStore(Class clazz, String id) {
		super(clazz, id);
	}

	public SpoutCreeper load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutCreeper entity = new SpoutCreeper(server, world);

		super.load(entity, compound);

		return entity;
	}
}
