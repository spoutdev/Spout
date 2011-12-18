package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.discrete.Point;
import org.getspout.api.math.Vector3;

/**
 * Represents a Cuboid that can be moved in discrete steps based on its size
 * 
 * The size of the Cuboid may also be altered and all new movements will be made relative to a grid based on that size.
 */
public interface MutableCuboid extends MovableCuboid {
	
	/**
	 * Sets the base of the Cuboid
	 * 
	 * @param base the base of the Cuboid
	 */
	public void setBase(Point base);
	
	/**
	 * Sets the Size of the Cuboid
	 * 
	 * @param size the size of the Cuboid
	 */
	public void setSize(Vector3 size);
}
