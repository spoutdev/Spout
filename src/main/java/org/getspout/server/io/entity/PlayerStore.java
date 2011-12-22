package org.getspout.server.io.entity;

import java.util.Map;

import org.bukkit.GameMode;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.util.nbt.CompoundTag;
import org.getspout.server.util.nbt.FloatTag;
import org.getspout.server.util.nbt.IntTag;
import org.getspout.server.util.nbt.Tag;

public class PlayerStore extends HumanEntityStore<SpoutPlayer> {
	public PlayerStore() {
		super(SpoutPlayer.class, "Player");
	}

	@Override
	public SpoutPlayer load(SpoutServer server, SpoutWorld world, CompoundTag compound) {
		throw new UnsupportedOperationException("Only existing players can be loaded to");
	}

	@Override
	public void load(SpoutPlayer entity, CompoundTag compound) {
		super.load(entity, compound);
		/*
		if (nbttagcompound.hasKey("foodLevel")) {
			this.foodTickTimer = nbttagcompound.e("foodTickTimer");
		}*/
		if (compound.getValue().containsKey("XpTotal")) {
			entity.setTotalExperience(((IntTag) compound.getValue().get("XpTotal")).getValue());
		}
		if (compound.getValue().containsKey("foodLevel")) {
			entity.setFoodLevel(((IntTag) compound.getValue().get("foodLevel")).getValue());
		}
		if (compound.getValue().containsKey("foodTickTimer")) {
			// entity.set(((IntTag)compound.getValue().get("foodTickTimer")).getValue());
		}
		if (compound.getValue().containsKey("foodSaturationLevel")) {
			entity.setSaturation(((FloatTag) compound.getValue().get("foodSaturationLevel")).getValue());
		}
		if (compound.getValue().containsKey("foodExhaustionLevel")) {
			entity.setExhaustion(((FloatTag) compound.getValue().get("foodExhaustionLevel")).getValue());
		}
		if (compound.getValue().containsKey("playerGameType")) {
			GameMode mode = GameMode.getByValue(((IntTag) compound.getValue().get("playerGameType")).getValue());
			if (mode != null) {
				entity.setGameMode(mode);
			}
		}
	}

	@Override
	public Map<String, Tag> save(SpoutPlayer entity) {
		Map<String, Tag> ret = super.save(entity);
		ret.remove("id");
		ret.put("XpTotal", new IntTag("XpTotal", entity.getTotalExperience()));
		ret.put("Xp", new IntTag("Xp", entity.getExperience()));
		ret.put("XpLevel", new IntTag("XpLevel", entity.getLevel()));
		// ret.put("foodTickTimer", new IntTag("foodTickTimer", entity.get));
		ret.put("foodSaturationLevel", new FloatTag("foodSaturationLevel", entity.getSaturation()));
		ret.put("foodExhaustionLevel", new FloatTag("foodExhaustionLevel", entity.getExhaustion()));
		ret.put("playerGameType", new IntTag("playerGameType", entity.getGameMode().getValue()));
		return ret;
	}
}
