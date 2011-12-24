package org.getspout.unchecked.server.entity.objects;

import org.bukkit.entity.Explosive;
import org.getspout.server.util.Position;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.entity.SpoutEntity;
import org.getspout.unchecked.server.msg.Message;
import org.getspout.unchecked.server.msg.SpawnVehicleMessage;

public abstract class SpoutExplosive extends SpoutEntity implements Explosive {
	private float radius;
	private boolean incendiary;
	private final int type;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world The world.
	 */
	public SpoutExplosive(SpoutServer server, SpoutWorld world, int type) {
		super(server, world);
		this.type = type;
	}

	@Override
	public void setYield(float yield) {
		radius = yield;
	}

	@Override
	public float getYield() {
		return radius;
	}

	@Override
	public void setIsIncendiary(boolean isIncendiary) {
		incendiary = isIncendiary;
	}

	@Override
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
