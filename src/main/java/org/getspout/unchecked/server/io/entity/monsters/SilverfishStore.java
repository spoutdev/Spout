package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.api.io.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutSilverfish;

public class SilverfishStore extends MonsterStore<SpoutSilverfish> {
	public SilverfishStore() {
		super(SpoutSilverfish.class, "Silverfish");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public SilverfishStore(Class clazz, String id) {
		super(clazz, id);
	}

	@Override
	public SpoutSilverfish load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSilverfish entity = new SpoutSilverfish(server, world);

		super.load(entity, compound);

		return entity;
	}
}