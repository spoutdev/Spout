package org.getspout.api.collision;

import org.getspout.api.math.Vector3;

public class BoundingSphere {
	Vector3 center;

	double radius;

	public BoundingSphere(Vector3 center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public BoundingSphere(Vector3 center) {
		this(center, 1.0);
	}

	public BoundingSphere() {
		this(Vector3.ZERO, 1);
	}

	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(BoundingSphere b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public boolean intersects(Segment b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public boolean intersects(Plane b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public String toString() {
		return "BoundingSphere{" + "center=" + center + ", radius=" + radius + '}';
	}

}
