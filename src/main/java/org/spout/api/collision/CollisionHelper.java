/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.collision;

import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector3m;

public class CollisionHelper {
	/**
	 * Checks the collision between two BoundingBoxes
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean checkCollision(BoundingBox a, BoundingBox b) {
		return a.min.compareTo(b.max) <= 0 && a.max.compareTo(b.min) >= 0;
	}

	/**
	 * Checks the collision between a BoundingBox and a BoundingSphere
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean checkCollision(BoundingBox a, BoundingSphere b) {
		//Implementation of Arvo's Algorithm
		//http://www.gamasutra.com/view/feature/3383/simple_intersection_tests_for_games.php?page=4
		double s, d = 0;

		if (b.center.getX() < a.min.getX()) {
			s = b.center.getX() - a.min.getX();
			d += s * s;

		} else if (b.center.getX() > a.max.getX()) {
			s = b.center.getX() - a.max.getX();
			d += s * s;
		}

		if (b.center.getY() < a.min.getY()) {
			s = b.center.getY() - a.min.getY();
			d += s * s;

		} else if (b.center.getY() > a.max.getY()) {
			s = b.center.getY() - a.max.getY();
			d += s * s;
		}

		if (b.center.getZ() < a.min.getZ()) {
			s = b.center.getZ() - a.min.getZ();
			d += s * s;

		} else if (b.center.getZ() > a.max.getZ()) {
			s = b.center.getZ() - a.max.getZ();
			d += s * s;
		}

		return d <= b.radius * b.radius;
	}

	/**
	 * Checks if a bounding box and a line segment collide.
	 * Based off of people.csail.mit.edu/amy/papers/box-jgt.ps
	 * 
	 * There must be a better way to do this.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean checkCollision(BoundingBox a, Segment b) {
		Vector3 box = a.max.subtract(a.min);
		Vector3 seg = b.endpoint.subtract(b.origin);
		Vector3 m = b.origin.add(b.endpoint).subtract(a.max).subtract(a.min);

		// Try world coordinate axes as separating axes
		float adx = Math.abs(seg.getX());
		if (Math.abs(m.getX()) > box.getX() + adx) {
			return false;
		}
		float ady = Math.abs(seg.getY());
		if (Math.abs(m.getY()) > box.getY() + ady) {
			return false;
		}
		float adz = Math.abs(seg.getZ());
		if (Math.abs(m.getZ()) > box.getZ() + adz) {
			return false;
		}

		// Add in an epsilon term to counteract arithmetic errors when segment is
		// (near) parallel to a coordinate axis (see text for detail)
		adx += MathHelper.FLT_EPSILON;
		ady += MathHelper.FLT_EPSILON;
		adz += MathHelper.FLT_EPSILON;

		// Try cross products of segment direction vector with coordinate axes
		if (Math.abs(m.getY() * seg.getZ() - m.getZ() * seg.getY()) > box.getY() * adz + box.getZ() * ady) {
			return false;
		}
		if (Math.abs(m.getZ() * seg.getX() - m.getX() * seg.getZ()) > box.getX() * adz + box.getZ() * adx) {
			return false;
		}

		if (Math.abs(m.getX() * seg.getY() - m.getY() * seg.getX()) > box.getX() * ady + box.getY() * adx) {
			return false;
		}

		// No separating axis found; segment must be overlapping AABB
		return true;

	}

	public static boolean checkCollision(BoundingBox a, Ray b) {
		return getCollision(a, b) != null;
	}

	public static boolean checkCollision(BoundingBox a, Plane b) {
		boolean pos = (b.distance(a.min) > 0);
		return pos != (b.distance(a.max) > 0) //Planes that are axis-aligned. most cases
			|| pos != (b.distance(new Vector3(a.max.getX(), a.min.getY(), a.min.getZ())) > 0)
			|| pos != (b.distance(new Vector3(a.min.getX(), a.max.getY(), a.min.getZ())) > 0)
			|| pos != (b.distance(new Vector3(a.min.getX(), a.min.getY(), a.max.getZ())) > 0)
			|| pos != (b.distance(new Vector3(a.min.getX(), a.max.getY(), a.max.getZ())) > 0)
			|| pos != (b.distance(new Vector3(a.max.getX(), a.min.getY(), a.max.getZ())) > 0)
			|| pos != (b.distance(new Vector3(a.max.getX(), a.max.getY(), a.min.getZ())) > 0);
	}

	/**
	 * Checks the collision between two BoundingSpheres
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean checkCollision(BoundingSphere a, BoundingSphere b) {
		double radsum = a.radius + b.radius;
		return radsum * radsum >= a.center.subtract(b.center).lengthSquared();
	}

	/**
	 * Checks the collision between a BoundingSphere and a Segment.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean checkCollision(BoundingSphere a, Segment b) {
		Vector3 m = b.origin.subtract(a.center);
		Vector3 l = b.endpoint.subtract(b.origin);
		float lnorm = l.fastLength();
		Vector3 d = l.multiply(1f / lnorm);

		float e = m.dot(d);
		float f = (float) (m.dot(m) - (a.radius * a.radius));

		// Exit if r’s origin outside s (c > 0) and r pointing away from s (b > 0)
		if (f > 0.0f && e > 0.0f) {
			return false;
		}
		float discr = e * e - f;

		// A negative discriminant corresponds to ray missing sphere
		if (discr < 0.0f) {
			return false;
		}

		//Check that the intersection is not past the segment 
		return (-e - MathHelper.sqrt(discr)) <= lnorm;
	}

	/**
	 * Checks collision between a BoundingSphere and a Ray.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean checkCollision(BoundingSphere a, Ray b) {
		Vector3 m = b.origin.subtract(a.center);
		float e = m.dot(b.direction);
		float f = (float) (m.dot(m) - (a.radius * a.radius));

		// Exit if r’s origin outside s (c > 0) and r pointing away from s (b > 0)
		if (f > 0.0f && e > 0.0f) {
			return false;
		}
		float discr = e * e - f;

		// A negative discriminant corresponds to ray missing sphere
		if (discr < 0.0f) {
			return false;
		}

		// Ray now found to intersect sphere
		return true;
	}

	public static boolean checkCollision(BoundingSphere a, Plane b) {
		return b.distance(a.center) <= a.radius;
	}

	/**
	 * Checks for a collision between two line segments.
	 * RoyAwesome says rays are line segments.
	 * 
	 * <Afforess_> Perhaps Roy needs to brush up on geometry then
	 * 
	 * Code based on http://www.bryceboe.com/2006/10/23/line-segment-intersection-algorithm/
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean checkCollision(Segment a, Segment b) {
		return (ccw(a.origin, b.origin, b.endpoint) != ccw(a.endpoint, b.origin, b.endpoint))
			&& (ccw(b.origin, a.origin, a.endpoint) != ccw(b.endpoint, a.origin, a.endpoint));
	}

	/**
	 * Checks if 3 points are counterclockwise.
	 * (A helper for a helper method)
	 * @param a
	 * @param b
	 * @param c
	 * @return 
	 */
	private static boolean ccw(Vector3 a, Vector3 b, Vector3 c) {
		//This is Java lisp
		return (c.getY() - a.getY()) * (b.getX() - a.getX())
			< (b.getY() - a.getY()) * (c.getX() - a.getX())
			|| (c.getY() - a.getY()) * (b.getZ() - a.getZ())
			< ((b.getY() - a.getY()) * (c.getZ() - a.getZ()));

	}

	public static boolean checkCollision(Segment a, Plane b) {
		return (b.distance(a.origin) > 0) != (b.distance(a.endpoint) > 0);
	}

	public static boolean checkCollision(Plane a, Plane b) {
		return !a.normal.equals(b.normal) && !a.normal.equals(b.normal.multiply(-1));
	}

	
	public static Vector3 getCollision(BoundingSphere a, BoundingBox b){
		//TODO implement this
		return null;
	}
	
	public static Vector3 getCollision(BoundingBox a, Plane b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(BoundingSphere a, BoundingSphere b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(BoundingBox a, Segment b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(BoundingSphere a, Plane b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(Plane a, Plane b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(Plane a, Ray b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(Plane a, Segment b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(Ray a, Ray b){
		//TODO this
		return null;
	}
	/**
	 * Gets the intersection between two BoundingBoxes.
	 * Null will be returned if there's no collision.
	 * 
	 * Inspiration taken from:
	 * http://clb.demon.fi/MathGeoLib/docs/AABB.cpp_code.html#876
	 * http://tekpool.wordpress.com/2006/10/12/rectangle-intersection-find-the-intersecting-rectangle/
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static BoundingBox getIntersection(BoundingBox a, BoundingBox b) {
		if ( ! checkCollision(a, b)) return null;
		
		Vector3 intersectionMin = new Vector3(
			Math.max(a.min.getX(), b.min.getX()),
			Math.max(a.min.getY(), b.min.getY()),
			Math.max(a.min.getZ(), b.min.getZ())
		);
		
		Vector3 intersectionMax = new Vector3(
			Math.min(a.max.getX(), b.max.getX()),
			Math.min(a.max.getY(), b.max.getY()),
			Math.min(a.max.getZ(), b.max.getZ())
		);

		return new BoundingBox(intersectionMin, intersectionMax);
	}
	
	/**
	 * Gets the collision point between two BoundingBoxes.
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 getCollision(BoundingBox a, BoundingBox b) {
		BoundingBox intersection = getIntersection(a, b);
		if (intersection == null) return null;
		Vector3m ret = new Vector3m(intersection.min);
		ret.add(intersection.max);
		ret.multiply(0.5f);
		return ret;
	}
	
	
	public static Vector3 getCollision(Segment a, Segment b){
		//TODO this
		return null;
	}
	
	public static Vector3 getCollision(BoundingBox a, Ray b) {
		float tmin = 0.0f;
		float tmax = Float.MAX_VALUE;

		//Check X slab
		if ((Math.abs(b.direction.getX()) < MathHelper.FLT_EPSILON)
			&& (b.origin.getX() < a.min.getX() || b.origin.getX() > a.max.getX())) {
			return null;
		}
		float ood = 1.0f / b.direction.getX();
		float t1 = (a.min.getX() - b.origin.getX()) * ood;
		float t2 = (a.max.getX() - b.origin.getX()) * ood;
		if (t1 > t2) {
			float t2b = t2;
			t2 = t1;
			t1 = t2b;
		}
		if (t1 > tmin) {
			tmin = t1;
		}
		if (t2 > tmax) {
			tmax = t2;
		}
		if (tmin > tmax) {
			return null;
		}

		//Check Y slab
		if ((Math.abs(b.direction.getY()) < MathHelper.FLT_EPSILON)
			&& (b.origin.getY() < a.min.getY() || b.origin.getY() > a.max.getY())) {
			return null;
		}
		ood = 1.0f / b.direction.getY();
		t1 = (a.min.getY() - b.origin.getY()) * ood;
		t2 = (a.max.getY() - b.origin.getY()) * ood;
		if (t1 > t2) {
			float t2b = t2;
			t2 = t1;
			t1 = t2b;
		}
		if (t1 > tmin) {
			tmin = t1;
		}
		if (t2 > tmax) {
			tmax = t2;
		}
		if (tmin > tmax) {
			return null;
		}

		//Check Z slab
		if ((Math.abs(b.direction.getZ()) < MathHelper.FLT_EPSILON)
			&& (b.origin.getZ() < a.min.getZ() || b.origin.getZ() > a.max.getZ())) {
			return null;
		}
		ood = 1.0f / b.direction.getZ();
		t1 = (a.min.getZ() - b.origin.getZ()) * ood;
		t2 = (a.max.getZ() - b.origin.getZ()) * ood;
		if (t1 > t2) {
			float t2b = t2;
			t2 = t1;
			t1 = t2b;
		}
		if (t1 > tmin) {
			tmin = t1;
		}
		if (t2 > tmax) {
			tmax = t2;
		}
		if (tmin > tmax) {
			return null;
		}

		// Ray intersects all 3 slabs. Return point (q) and intersection t value (tmin)
		Vector3 q = b.origin.add(b.direction.multiply(tmin));
		return q;
	}

	/**
	 * Gets the collision point between a
	 * BoundingSphere and a Segment.
	 * 
	 * Taken from Real-Time Collision Detection.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static Vector3 getCollision(BoundingSphere a, Segment b) {
		Vector3 m = b.origin.subtract(a.center);
		Vector3 l = b.endpoint.subtract(b.origin);
		Vector3 d = l.multiply(1f / l.fastLength());

		float e = m.dot(d);
		float f = (float) (m.dot(m) - (a.radius * a.radius));

		// Exit if r’s origin outside s (c > 0) and r pointing away from s (b > 0)
		if (f > 0.0f && e > 0.0f) {
			return null;
		}
		float discr = e * e - f;

		// A negative discriminant corresponds to ray missing sphere
		if (discr < 0.0f) {
			return null;
		}

		// Ray now found to intersect sphere, compute smallest t value of intersection
		float t = (float) (-e - MathHelper.sqrt(discr));

		// If t is negative, ray started inside sphere so clamp t to zero
		if (t < 0.0f) {
			t = 0.0f;
		}
		return b.origin.add(d.multiply(t));
	}

	/**
	 * Gets the point of collision between a BoundingSphere
	 * and a Ray.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static Vector3 getCollision(BoundingSphere a, Ray b) {
		Vector3 m = b.origin.subtract(a.center);
		float e = m.dot(b.direction);
		float f = (float) (m.dot(m) - (a.radius * a.radius));

		// Exit if r’s origin outside s (c > 0) and r pointing away from s (b > 0)
		if (f > 0.0f && e > 0.0f) {
			return null;
		}
		float discr = e * e - f;

		// A negative discriminant corresponds to ray missing sphere
		if (discr < 0.0f) {
			return null;
		}

		// Ray now found to intersect sphere, compute smallest t value of intersection
		float t = (float) (-e - MathHelper.sqrt(discr));

		// If t is negative, ray started inside sphere so clamp t to zero
		if (t < 0.0f) {
			t = 0.0f;
		}
		return b.origin.add(b.direction.multiply(t));
	}

	/**
	 * Returns true if the BoundingBox contains the other BoundingBox.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean contains(BoundingBox a, BoundingBox b) {
		return (a.min.compareTo(b.min) >= 0 && a.max.compareTo(b.max) >= 0);
	}

	/**
	 * Returns true if the BoundingBox contains the BoundingSphere.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean contains(BoundingBox a, BoundingSphere b) {
		Vector3 zeroed = a.max.subtract(a.min);
		Vector3 newCenter = b.center.subtract(a.min);
		return (newCenter.getX() - b.radius) <= 0
			&& zeroed.getX() <= (newCenter.getX() + b.radius)
			&& (newCenter.getY() - b.radius) <= 0
			&& zeroed.getY() <= (newCenter.getY() + b.radius)
			&& (newCenter.getZ() - b.radius) <= 0
			&& zeroed.getZ() <= (newCenter.getZ() + b.radius);
	}

	/**
	 * Checks if a box contains a Plane
	 * 
	 * Will always return false.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean contains(BoundingBox a, Plane b) {
		return false;
	}
	
	/**
	 * Checks if a BoundingBox will contain a Ray
	 * 
	 * Will always return false.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean contains(BoundingBox a, Ray b) {
		return false;
	}
	
	/**
	 * Returns true if the BoundingBox contains the Segment.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean contains(BoundingBox a, Segment b) {
		return (a.containsPoint(b.origin) && a.containsPoint(b.endpoint));
	}

	/**
	 * Returns true if the BoundingSphere contains the BoundingSphere.
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean contains(BoundingSphere a, BoundingSphere b) {
		return b.center.subtract(a.center).lengthSquared() 
			+ (b.radius * b.radius) < a.radius * a.radius;
	}

	public static boolean contains(BoundingSphere a, Plane b) {
		return false;
	}

	/**
	 * Joke's getting old.
	 * @param a
	 * @param b
	 * @return 
	 */
	public static boolean contains(BoundingSphere a, Ray b) {
		return false;
	}

	public static boolean contains(BoundingSphere a, Segment b) {
		return a.containsPoint(b.origin) && a.containsPoint(b.endpoint);
	}

	public static boolean contains(Plane a, Plane b) {
		return a.normal.equals(b.normal) || a.normal.equals(b.normal.multiply(-1));
	}

	public static boolean contains(Plane a, Ray b) {
		return a.containsPoint(b.origin) && a.containsPoint(b.origin.add(b.direction));
	}

	public static boolean contains(Plane a, Segment b) {
		return a.containsPoint(b.origin) && a.containsPoint(b.endpoint);
	}

	public static boolean contains(Ray a, Ray b) {
		return a.containsPoint(b.origin) && a.containsPoint(b.origin.add(b.direction));
	}

	public static boolean contains(Ray a, Segment b) {
		return a.contains(a) && a.contains(b);
	}

	public static boolean contains(Segment a, Segment b) {
		return a.containsPoint(b.origin) && a.containsPoint(b.endpoint);
	}

	public static boolean contains(BoundingBox a, Vector3 b) {
		return a.max.subtract(a.min).compareTo(b.subtract(a.min)) > 0;
	}

	public static boolean contains(BoundingSphere a, Vector3 b) {
		return a.center.subtract(b).lengthSquared() <= a.radius * a.radius;
	}

	public static boolean contains(Plane a, Vector3 b) {
		return a.distance(b) < MathHelper.FLT_EPSILON;
	}

	public static boolean contains(Ray a, Vector3 b) {
		return b.subtract(a.origin).normalize().equals(a.direction);
	}

	public static boolean contains(Segment a, Vector3 b) {
		return a.endpoint.subtract(a.origin).normalize().equals(b.subtract(a.origin).normalize()); //There must be a better way
	}
}
