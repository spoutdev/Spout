package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;

/**
 * Represents a cube with an edge length of 1/16 of the edge of a Block.
 */
public class Voxelm extends Voxel implements MovableCuboid {

	public Voxelm(World world, double x, double y, double z) {
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
