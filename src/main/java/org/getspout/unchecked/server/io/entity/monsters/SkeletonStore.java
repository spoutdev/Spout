package org.getspout.unchecked.server.io.entity.monsters;

import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.monsters.SpoutSkeleton;

public class SkeletonStore extends MonsterStore<SpoutSkeleton> {
	public SkeletonStore() {
		super(SpoutSkeleton.class, "Skeleton");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public SkeletonStore(Class clazz, String id) {
		super(clazz, id);
	}

	@Override
	public SpoutSkeleton load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

		SpoutSkeleton entity = new SpoutSkeleton(server, world);

		super.load(entity, compound);

		return entity;
	}
}