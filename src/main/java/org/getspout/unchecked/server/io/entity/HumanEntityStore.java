package org.getspout.unchecked.server.io.entity;

import java.util.Map;

import org.bukkit.Location;
import org.getspout.api.io.nbt.CompoundTag;
import org.getspout.api.io.nbt.IntTag;
import org.getspout.api.io.nbt.ListTag;
import org.getspout.api.io.nbt.Tag;
import org.getspout.unchecked.server.entity.SpoutHumanEntity;
import org.getspout.unchecked.server.io.nbt.NbtSerialization;

public abstract class HumanEntityStore<T extends SpoutHumanEntity> extends LivingEntityStore<T> {
	public HumanEntityStore(Class<T> clazz, String id) {
		super(clazz, id);
	}

	@Override
	public void load(T entity, CompoundTag compound) {
		super.load(entity, compound);
		/*
		this.sleeping = nbttagcompound.m("Sleeping");
		this.sleepTicks = nbttagcompound.d("SleepTimer");
		*/
		if (compound.getValue().containsKey("Inventory")) {
			entity.getInventory().setContents(NbtSerialization.tagToInventory((ListTag<CompoundTag>) compound.getValue().get("Inventory"), entity.getInventory().getSize()));
		}
		if (compound.getValue().containsKey("SpawnX") && compound.getValue().containsKey("SpawnY") && compound.getValue().containsKey("SpawnZ")) {
			IntTag spawnXTag = (IntTag) compound.getValue().get("SpawnX");
			IntTag spawnYTag = (IntTag) compound.getValue().get("SpawnY");
			IntTag spawnZTag = (IntTag) compound.getValue().get("SpawnZ");
			entity.setBedSpawnLocation(new Location(entity.getWorld(), spawnXTag.getValue(), spawnYTag.getValue(), spawnZTag.getValue()));
		}
	}

	@Override
	public Map<String, Tag> save(T entity) {
		/*
		this.sleeping = nbttagcompound.m("Sleeping");
		this.sleepTicks = nbttagcompound.d("SleepTimer");
		*/
		Map<String, Tag> ret = super.save(entity);
		ret.put("Inventory", NbtSerialization.inventoryToTag(entity.getInventory().getContents()));
		Location bed = entity.getBedSpawnLocation();
		if (bed != null) {
			ret.put("SpawnX", new IntTag("SpawnX", bed.getBlockX()));
			ret.put("SpawnY", new IntTag("SpawnY", bed.getBlockY()));
			ret.put("SpawnZ", new IntTag("SpawnZ", bed.getBlockZ()));
		}
		return ret;
	}
}
