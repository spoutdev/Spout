package org.getspout.api.geo.discrete;

import org.getspout.api.geo.World;
import org.getspout.api.math.Vector3;

/**
 * Represents a position in a World
 */

public class Point extends Vector3 {

	protected World world;
	
	public Point(Point point) {
		super(point);
		world = point.getWorld();
	}
	
	public Point(World world, double x, double y, double z) {
		super(x, y, z);
		this.world = world;
	}
	
	/**
	 * Gets the world this point is locate in
	 * 
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}
	
}
