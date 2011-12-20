package org.getspout.api.collision;

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
	 * Checks the collision between two BoundingSpheres
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean checkCollision(BoundingSphere a, BoundingSphere b) {
		return a.radius + b.radius >= a.center.length() - b.center.length();
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

	public static boolean checkCollision(CollisionRay a, BoundingBox b) {
		return false; //TODO Implement this
	}

}
