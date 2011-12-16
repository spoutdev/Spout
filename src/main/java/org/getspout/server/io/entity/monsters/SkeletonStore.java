package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutSkeleton;
import org.getspout.server.util.nbt.CompoundTag;

public class SkeletonStore extends MonsterStore<SpoutSkeleton> {
	public SkeletonStore() {
		super(SpoutSkeleton.class, "Skeleton");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SkeletonStore(Class clazz, String id) {
		super(clazz, id);
	}

	public SpoutSkeleton load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSkeleton entity = new SpoutSkeleton(server, world);

		super.load(entity, compound);

		return entity;
	}
}