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
	public Point(Vector3 vector, World w){
		super(vector);
		this.world = w;
	}

	public Point(World world, float x, float y, float z) {
		super(x, y, z);
		this.world = world;
	}
	
	public Point add(Point other){
		if(this.world != other.world) throw new IllegalArgumentException("Cannot add two points in seperate worlds");
		return new Point(Vector3.add((Vector3)this, (Vector3)other), this.world);
	}
	public Point add(Vector3 other){
		return new Point(Vector3.add((Vector3)this, other), this.world);
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
