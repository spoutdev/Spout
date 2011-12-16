package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutSilverfish;
import org.getspout.server.util.nbt.CompoundTag;

public class SilverfishStore extends MonsterStore<SpoutSilverfish> {
	public SilverfishStore() {
		super(SpoutSilverfish.class, "Silverfish");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SilverfishStore(Class clazz, String id) {
		super(clazz, id);
	}

	public SpoutSilverfish load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSilverfish entity = new SpoutSilverfish(server, world);

		super.load(entity, compound);

		return entity;
	}
}