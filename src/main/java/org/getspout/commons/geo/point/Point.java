package org.getspout.commons.geo.point;

import org.getspout.commons.geo.World;

/**
 * Represents an point located in a World
 */

public class Point extends Vector {

	private World world;
	
	public Point(World world, double x, double y, double z) {
		super(x, y, z);
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
}
