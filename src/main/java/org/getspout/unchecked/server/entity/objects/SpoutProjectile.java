package org.getspout.unchecked.server.entity.objects;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.Damager;
import org.getspout.unchecked.server.entity.SpoutEntity;
import org.getspout.unchecked.server.msg.Message;
import org.getspout.unchecked.server.msg.SpawnVehicleMessage;
import org.getspout.unchecked.server.util.Position;

public abstract class SpoutProjectile extends SpoutEntity implements Projectile {
	private final int type;

	private LivingEntity shooter;

	private boolean bounces;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world The world.
	 */
	public SpoutProjectile(SpoutServer server, SpoutWorld world, int id) {
		super(server, world);
		type = id;
	}

	@Override
	public Message createSpawnMessage() {
		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);
		return new SpawnVehicleMessage(id, type, x, y, z);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		return null;
	}

	@Override
	public LivingEntity getShooter() {
		return shooter;
	}

	@Override
	public void setShooter(LivingEntity shooter) {
		this.shooter = shooter;
	}

	@Override
	public boolean doesBounce() {
		return bounces;
	}

	@Override
	public void setBounce(boolean doesBounce) {
		bounces = doesBounce;
	}
}
