package org.getspout.server.entity.vehicles;

import org.bukkit.entity.Vehicle;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.SpawnVehicleMessage;
import org.getspout.server.util.Position;

public abstract class SpoutVehicle extends SpoutEntity implements Vehicle {
	private final int type;

	protected double maxSpeed;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param world The world.
	 */
	public SpoutVehicle(SpoutServer server, SpoutWorld world, int type) {
		super(server, world);
		this.type = type;
	}

	@Override
	public Message createSpawnMessage() {
		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);
		return new SpawnVehicleMessage(id, type, x, y, z);
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double speed) {
		maxSpeed = speed;
	}
}
