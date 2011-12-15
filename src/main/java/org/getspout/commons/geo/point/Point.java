package org.getspout.commons.geo.point;

import org.getspout.commons.geo.World;
import org.getspout.commons.math.Vector3;

/**
 * Represents an point located in a World
 */

public class Point extends Vector3 {

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
