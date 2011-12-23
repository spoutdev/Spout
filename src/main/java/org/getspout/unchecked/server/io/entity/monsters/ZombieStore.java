package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutZombie;

public class ZombieStore extends MonsterStore<SpoutZombie> {
	public ZombieStore() {
		super(SpoutZombie.class, "Zombie");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public ZombieStore(Class clazz, String id) {
		super(clazz, id);
	}

	@Override
	public SpoutZombie load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutZombie entity = new SpoutZombie(server, world);

		super.load(entity, compound);

		return entity;
	}
}