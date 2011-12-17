package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.point.Point;

/**
 * Represents a cube with an edge length of 1/16 of the edge of a Block.
 */
public class Voxel extends Cube {
	
	protected final static double EDGE = 1/16.0;
	
	public Voxel(World world, double x, double y, double z) {
		super(new Point(world, x, y, z), EDGE);
	}
}
