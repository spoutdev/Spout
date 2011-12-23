package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.api.io.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutSpider;

public class SpiderStore extends MonsterStore<SpoutSpider> {
	public SpiderStore() {
		super(SpoutSpider.class, "Spider");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public SpiderStore(Class clazz, String id) {
		super(clazz, id);
	}

	@Override
	public SpoutSpider load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSpider entity = new SpoutSpider(server, world);

		super.load(entity, compound);

		return entity;
	}
}