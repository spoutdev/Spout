package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;

/**
 * Represents a cube with an edge length of 1/16 of the edge of a Block.
 */
public abstract class Voxel extends Cube {

	protected final static float EDGE = 1.f / 16.0f;

	public Voxel(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), EDGE);
	}
}
