package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;

/**
 * Represents a movable cube containing 16x16x16 Blocks
 */
public abstract class Chunkm extends Chunk implements MovableCuboid {

	public Chunkm(World world, double x, double y, double z) {
		super(world, x, y, z);
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
