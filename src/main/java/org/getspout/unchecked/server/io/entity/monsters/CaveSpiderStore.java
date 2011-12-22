package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutCaveSpider;
import org.getspout.unchecked.server.util.nbt.CompoundTag;

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
