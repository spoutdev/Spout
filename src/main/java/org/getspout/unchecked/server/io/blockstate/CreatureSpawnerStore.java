package org.getspout.unchecked.server.io.blockstate;

import java.util.Map;

import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.api.util.nbt.ShortTag;
import org.getspout.api.util.nbt.StringTag;
import org.getspout.api.util.nbt.Tag;
import org.getspout.unchecked.server.block.SpoutCreatureSpawner;

public class CreatureSpawnerStore extends BlockStateStore<SpoutCreatureSpawner> {
	public CreatureSpawnerStore() {
		super(SpoutCreatureSpawner.class, "MobSpawner");
	}

	@Override
	public void load(SpoutCreatureSpawner spawner, CompoundTag compound) {
		super.load(spawner, compound);
		spawner.setCreatureTypeId(compound.getValue().get("EntityId").getValue().toString());
		if (compound.getValue().get("Delay") instanceof ShortTag) {
			spawner.setDelay(((ShortTag) compound.getValue().get("Delay")).getValue());
		}
	}

	@Override
	public Map<String, Tag> save(SpoutCreatureSpawner spawner) {
		Map<String, Tag> ret = super.save(spawner);
		ret.put("EntityId", new StringTag("EntityId", spawner.getCreatureTypeId()));
		ret.put("Delay", new ShortTag("Delay", (short) spawner.getDelay()));
		return ret;
	}
}
