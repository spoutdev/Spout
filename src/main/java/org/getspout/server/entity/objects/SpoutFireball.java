package org.getspout.server.entity.objects;

import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public class SpoutFireball extends SpoutProjectile implements Fireball {
	private Vector direction;
	private float radius;
	private boolean incendiary;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world  The world.
	 */
	public SpoutFireball(SpoutServer server, SpoutWorld world, Vector direction) {
		super(server, world, 63);
		this.direction = direction;
	}

	public void setDirection(Vector direction) {
		this.direction = direction;
	}

	public Vector getDirection() {
		return direction;
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
}
