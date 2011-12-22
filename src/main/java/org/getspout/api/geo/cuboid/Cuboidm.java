package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.discrete.Point;
import org.getspout.api.math.Vector3;

/**
 * Represents a movable Cuboid that is located somewhere in a world.
 */
public class Cuboidm extends Cuboid implements MovableCuboid {

	public Cuboidm(Point base, Vector3 size) {
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
