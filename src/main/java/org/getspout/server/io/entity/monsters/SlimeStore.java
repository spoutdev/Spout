package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutSlime;
import org.getspout.server.util.nbt.CompoundTag;

public class SlimeStore extends MonsterStore<SpoutSlime> {
	public SlimeStore() {
		super(SpoutSlime.class, "Slime");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SlimeStore(Class clazz, String id) {
		super(clazz, id);
	}

	public SpoutSlime load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSlime entity = new SpoutSlime(server, world);

		super.load(entity, compound);

		return entity;
	}
}