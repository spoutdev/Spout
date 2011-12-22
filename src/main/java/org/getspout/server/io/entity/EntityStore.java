package org.getspout.server.io.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.io.nbt.NbtSerialization;
import org.getspout.server.util.nbt.ByteTag;
import org.getspout.server.util.nbt.CompoundTag;
import org.getspout.server.util.nbt.IntTag;
import org.getspout.server.util.nbt.ListTag;
import org.getspout.server.util.nbt.LongTag;
import org.getspout.server.util.nbt.StringTag;
import org.getspout.server.util.nbt.Tag;

public abstract class EntityStore<T extends SpoutEntity> {
	private final String id;
	private final Class<T> clazz;

	public EntityStore(Class<T> clazz, String id) {
		this.id = id;
		this.clazz = clazz;
	}

	public abstract T load(SpoutServer server, SpoutWorld world, CompoundTag compound);

	public void load(T entity, CompoundTag compound) {
		if (compound.getValue().containsKey("id")) {
			String checkId = ((StringTag) compound.getValue().get("id")).getValue();
			if (!id.equalsIgnoreCase(checkId)) {
				throw new IllegalArgumentException("Invalid ID loading entity, expected " + id + " got " + checkId);
			}
		}
		World world = null;
		if (compound.getValue().containsKey("WorldUUIDLeast") && compound.getValue().containsKey("WorldUUIDMost")) {
			long uuidLeast = ((LongTag) compound.getValue().get("WorldUUIDLeast")).getValue();
			long uuidMost = ((LongTag) compound.getValue().get("WorldUUIDMost")).getValue();
			world = entity.getServer().getWorld(new UUID(uuidLeast, uuidMost));
		}
		if (world == null && compound.getValue().containsKey("World")) {
			world = entity.getServer().getWorld(((StringTag) compound.getValue().get("World")).getValue());
		}
		if (world == null  && compound.getValue().containsKey("Dimension")) {
			int dim = ((IntTag) compound.getValue().get("Dimension")).getValue();
			for (World sWorld : entity.getServer().getWorlds()) {
				if (sWorld.getEnvironment().getId() == dim)
					world = sWorld;
			}
		}
		if (world == null) {
			world = entity.getWorld();
		}
		if (compound.getValue().containsKey("Pos") && compound.getValue().containsKey("Rotation")) {
			ListTag posTag = (ListTag) compound.getValue().get("Pos");
			ListTag rotTag = (ListTag) compound.getValue().get("Rotation");
			entity.teleport(NbtSerialization.listTagsToLocation(world, posTag, rotTag));
		} else {
			entity.teleport(world.getSpawnLocation());
		}
		if (compound.getValue().containsKey("Motion")) {
			// entity.setVelocity(NbtFormattingUtils.listTagToVector((ListTag<DoubleTag>) compound.getValue().get("Motion")));
		}
		if (compound.getValue().containsKey("Air")) {
			// entity.setRemainingAir(((ShortTag) compound.getValue().get("Air")).getValue());
		}
		if (compound.getValue().containsKey("Fire")) {
			// entity.setFireTicks(((ShortTag) compound.getValue().get("Fire")).getValue());
		}
		if (compound.getValue().containsKey("OnGround")) {
			entity.setOnGround(((ByteTag) compound.getValue().get("OnGround")).getValue() == 1);
		}

		/* if (playerData.containsKey("HurtTime")) {
			ShortTag hurtTimeTag = (ShortTag) playerData.get("HurtTime");
			ret.put(PlayerData.HURT_TICKS, hurtTimeTag.getValue());
		}
		if (playerData.containsKey("AttackTime")) {
			ShortTag attackTimeTag = (ShortTag) playerData.get("AttackTime");
			ret.put(PlayerData.ATTACK_TICKS, attackTimeTag.getValue());
		}
		if (playerData.containsKey("DeathTime")) {
			ShortTag deathTimeTag = (ShortTag) playerData.get("DeathTime");
			ret.put(PlayerData.DEATH_TICKS, deathTimeTag.getValue());
		} */
	}

	public Map<String, Tag> save(T entity) {
		Map<String, Tag> result = new HashMap<String, Tag>();
		result.put("id", new StringTag("id", id));
		Location loc = entity.getLocation();
		UUID worldUUID = loc.getWorld().getUID();
		result.put("WorldUUIDLeast", new LongTag("WorldUUIDLeast", worldUUID.getLeastSignificantBits()));
		result.put("WorldUUIDMost", new LongTag("WorldUUIDMost", worldUUID.getMostSignificantBits()));
		result.put("World", new StringTag("World", loc.getWorld().getName()));
		result.put("Dimension", new IntTag("Dimension", loc.getWorld().getEnvironment().getId()));
		result.putAll(NbtSerialization.locationToListTags(loc));
		// result.put("UUIDLeast", new LongTag("UUIDLeast", entity.getUniqueId().getLeastSignificantBits()));
		// result.put("UUIDMost", new LongTag("UUIDMost", entity.getUniqueId().getMostSignificantBits()));
		// result.put("HurtTime", new ShortTag("HurtTime", (short) 0)); // NYI
		// result.put("Air", new ShortTag("Air", (short) entity.getRemainingAir()));
		// result.put("Fire", new ShortTag("Fire", (short) entity.getFireTicks()));
		result.put("Motion", NbtSerialization.vectorToListTag(entity.getVelocity()));
		result.put("OnGround", new ByteTag("OnGround", (byte) (entity.isOnGround() ? 1 : 0)));
		return result;
	}

	public String getId() {
		return id;
	}

	public Class<T> getType() {
		return clazz;
	}
}
