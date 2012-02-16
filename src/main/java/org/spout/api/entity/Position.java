package org.spout.api.entity;

import org.spout.api.geo.discrete.Point;

/**
 * Container for an entity position. Can be passed around to make passing 
 * entity locations around more easy for plugins. Should not be used where lots of instances 
 * will be created.
 */
public class Position {
	private final Point position;
	private final float pitch, yaw, roll;

	public Position(Point position, float pitch, float yaw, float roll) {
		this.roll = roll;
		this.yaw = yaw;
		this.pitch = pitch;
		this.position = position;
	}

	public Point getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
}
