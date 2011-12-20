package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutZombie;
import org.getspout.server.util.nbt.CompoundTag;

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