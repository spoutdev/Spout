package org.getspout.api.geo.point;

import org.getspout.api.geo.World;
import org.getspout.api.math.Vector3;

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
