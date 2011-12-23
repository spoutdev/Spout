package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutCaveSpider;

public class CaveSpiderStore extends SpiderStore {
	public CaveSpiderStore() {
		super(SpoutCaveSpider.class, "CaveSpider");
	}

	@Override
	public SpoutCaveSpider load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutCaveSpider entity = new SpoutCaveSpider(server, world);

		super.load(entity, compound);

		return entity;
	}
}
