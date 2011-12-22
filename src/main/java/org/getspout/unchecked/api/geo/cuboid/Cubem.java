package org.getspout.unchecked.api.geo.cuboid;

import org.getspout.unchecked.api.geo.discrete.Point;

/**
 * Represents a movable Cube that is located somewhere in a world.
 */
public class Cubem extends Cube implements MovableCuboid {

	public Cubem(Point base, double size) {
		super(base, size);
	}

	public void setX(int x) {
		base.setX(x * size.getX());
	}

	public void setY(int y) {
		base.setY(y * size.getY());
	}

	public void setZ(int z) {
		base.setZ(z * size.getZ());
	}
}
