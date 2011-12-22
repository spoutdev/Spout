package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.discrete.Point;
import org.getspout.api.math.Vector3;

/**
 * Represents a Cube that is located somewhere in a world.
 */
public class Cube extends Cuboid {

	public Cube(Point base, double size) {
		super(base, new Vector3(size, size, size));
	}
}
