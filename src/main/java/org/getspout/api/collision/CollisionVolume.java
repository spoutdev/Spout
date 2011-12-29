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
	 * Checks for containing
	 * @param other
	 * @return 
	 */
	public boolean contains(CollisionVolume other);

	public boolean containsBoundingBox(BoundingBox b);

	/**
	 * Checks for containing the given bounding sphere.
	 * 
	 * @param b
	 * @return 
	 */
	public boolean containsBoundingSphere(BoundingSphere b);

	public boolean containsPlane(Plane b);

	public boolean containsRay(Ray b);

	public boolean containsSegment(Segment b);

	/**
	 * Checks if the volume contains the other Vector3.
	 * 
	 * @param p
	 * @return 
	 */
	public boolean containsPoint(Vector3 b);

	/**
	 * Defines a sweep test from one start to an end
	 * @param start
	 * @param end
	 * @return
	 */
	public Vector3 resolve(CollisionVolume start, CollisionVolume end);

}
