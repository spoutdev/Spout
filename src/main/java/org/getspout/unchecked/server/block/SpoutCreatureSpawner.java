package org.getspout.unchecked.server.block;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;

public class SpoutCreatureSpawner extends SpoutBlockState implements CreatureSpawner {
	private MobSpawnerWrapper wrapper = new MobSpawnerWrapper(CreatureType.PIG, 15000);

	public SpoutCreatureSpawner(SpoutBlock block) {
		super(block);
	}

	@Override
	public CreatureType getCreatureType() {
		return wrapper.type;
	}

	@Override
	public void setCreatureType(CreatureType creatureType) {
		wrapper.type = creatureType;
	}

	@Override
	public String getCreatureTypeId() {
		return wrapper.type.getName();
	}

	@Override
	public void setCreatureTypeId(String s) {
		CreatureType type = CreatureType.fromName(s);
		if (type != null) {
			wrapper.type = type;
		}
	}

	@Override
	public int getDelay() {
		return wrapper.delay;
	}

	@Override
	public void setDelay(int i) {
		if (i < 0) {
			i = 0;
		}
		wrapper.delay = i;
	}

	@Override
	public SpoutCreatureSpawner shallowClone() {
		SpoutCreatureSpawner spawner = new SpoutCreatureSpawner(getBlock());
		spawner.wrapper = wrapper;
		return spawner;
	}

	@Override
	public void destroy() {
		setDelay(-1);
		setCreatureType(CreatureType.PIG);
	}

	private static class MobSpawnerWrapper {
		public int delay;
		public CreatureType type;

		public MobSpawnerWrapper(CreatureType type, int delay) {
			this.type = type;
			this.delay = delay;
		}
	}
}
