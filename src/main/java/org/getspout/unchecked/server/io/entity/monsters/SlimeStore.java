package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutSlime;

public class SlimeStore extends MonsterStore<SpoutSlime> {
	public SlimeStore() {
		super(SpoutSlime.class, "Slime");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public SlimeStore(Class clazz, String id) {
		super(clazz, id);
	}

	@Override
	public SpoutSlime load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSlime entity = new SpoutSlime(server, world);

		super.load(entity, compound);

		return entity;
	}
}