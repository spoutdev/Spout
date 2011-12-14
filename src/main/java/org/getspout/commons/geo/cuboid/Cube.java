package org.getspout.commons.geo.cuboid;

import org.getspout.commons.geo.point.Point;
import org.getspout.commons.geo.point.Vector;

/**
 * Represents a Cubic volume that is located somewhere in a world.
 */
public class Cube extends Cuboid {
	
	public Cube(Point base, double size) {
		super(base, new Vector(size, size, size));
	}

}
