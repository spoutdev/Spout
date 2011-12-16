package org.getspout.server.entity.objects;

import org.bukkit.entity.Explosive;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.SpawnVehicleMessage;
import org.getspout.server.util.Position;

public abstract class SpoutExplosive extends SpoutEntity implements Explosive {
	private float radius;
	private boolean incendiary;
	private final int type;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world  The world.
	 */
	public SpoutExplosive(SpoutServer server, SpoutWorld world, int type) {
		super(server, world);
		this.type = type;
	}

	public void setYield(float yield) {
		this.radius = yield;
	}

	public float getYield() {
		return radius;
	}

	public void setIsIncendiary(boolean isIncendiary) {
		this.incendiary = isIncendiary;
	}

	public boolean isIncendiary() {
		return incendiary;
	}

	@Override
	public Message createSpawnMessage() {
		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);
		return new SpawnVehicleMessage(id, type, x, y, z);
	}
}
