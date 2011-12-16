package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutSpider;
import org.getspout.server.util.nbt.CompoundTag;

public class SpiderStore extends MonsterStore<SpoutSpider> {
	public SpiderStore() {
		super(SpoutSpider.class, "Spider");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SpiderStore(Class clazz, String id) {
		super(clazz, id);
	}

	public SpoutSpider load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSpider entity = new SpoutSpider(server, world);

		super.load(entity, compound);

		return entity;
	}
}