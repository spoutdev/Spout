package org.getspout.unchecked.api.collision;

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

	public boolean intersects(BoundingSphere a, BoundingSphere b) {
		return CollisionHelper.checkCollision(a, b);
	}

	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(b, this);
	}

}
