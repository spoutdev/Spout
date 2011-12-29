package org.getspout.api.collision;

import org.getspout.api.math.Vector3;

/**
 * Defines a Volume that can collide with another Volume
 * 
 *
 */
public interface CollisionVolume {
	/**
	 * Checks for Intersection
	 * @param other
	 * @return
	 */
	public boolean intersects(CollisionVolume other);
	
	/**
	 * Defines a sweep test from one start to an end
	 * @param start
	 * @param end
	 * @return
	 */
	public Vector3 resolve(CollisionVolume start, CollisionVolume end);
}
