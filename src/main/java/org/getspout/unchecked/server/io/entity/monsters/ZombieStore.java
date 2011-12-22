package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutZombie;
import org.getspout.unchecked.server.util.nbt.CompoundTag;

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