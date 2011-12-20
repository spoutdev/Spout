package org.getspout.server.entity.neutrals;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Angerable;
import org.getspout.server.entity.Damager;
import org.getspout.server.entity.animals.SpoutAnimals;
import org.getspout.server.util.Parameter;

public class SpoutWolf extends SpoutAnimals implements Wolf, Angerable {
	private boolean angry, sitting, tamed;
	private String owner;

	/**
	 * Creates a new wolf.
	 *
	 * @param server This server this wolf is on.
	 * @param world The world this wolf is in.
	 */
	public SpoutWolf(SpoutServer server, SpoutWorld world) {
		super(server, world, 95);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		return null;
	}

	@Override
	public boolean isAngry() {
		return angry;
	}

	@Override
	public void setAngry(boolean angry) {
		this.angry = angry;
		setMetadataFlag(16, 0x02, angry);
	}

	@Override
	public boolean isSitting() {
		return sitting;
	}

	@Override
	public void setSitting(boolean sitting) {
		this.sitting = sitting;
		setMetadataFlag(16, 0x01, sitting);
	}

	@Override
	public boolean isTamed() {
		return tamed;
	}

	@Override
	public void setTamed(boolean tame) {
		tamed = tame;
		setMetadataFlag(16, 0x04, tame);
	}

	@Override
	public AnimalTamer getOwner() {
		return server.getOfflinePlayer(owner);
	}

	@Override
	public void setOwner(AnimalTamer tamer) {
		if (tamer == null) {
			owner = "";
		} else if (tamer instanceof OfflinePlayer) {
			owner = ((OfflinePlayer) tamer).getName();
		} else {
			throw new IllegalArgumentException("Unknown AnimalTamer type!");
		}
		if (owner != null) {
			setMetadata(new Parameter<String>(Parameter.TYPE_STRING, 17, owner));
		}
	}

	@Override
	public void setHealth(int health) {
		super.setHealth(health);
		setMetadata(new Parameter<Integer>(Parameter.TYPE_INT, 18, getHealth()));
	}
}
