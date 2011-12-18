package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;

/**
 * Represents a cube containing 16x16x16 Blocks
 */
public class Chunk extends Cube {
	
	private final static double EDGE = 16.0;
	
	public Chunk(World world, double x, double y, double z) {
		super(new Point(world, x, y, z), EDGE);
	}
}
