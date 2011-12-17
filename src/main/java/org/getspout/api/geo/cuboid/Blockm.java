package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;

/**
 * Represents a movable cube with an edge length of 1.
 */
public class Blockm extends Block implements MovableCuboid {

	public Blockm(World world, double x, double y, double z) {
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
