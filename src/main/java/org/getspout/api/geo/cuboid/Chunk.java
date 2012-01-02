package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.BlockAccess;
import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;

/**
 * Represents a cube containing 16x16x16 Blocks
 */
public abstract class Chunk extends Cube implements BlockAccess {

	private final static float EDGE = 16.0f;

	public Chunk(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), EDGE);
	}
}
