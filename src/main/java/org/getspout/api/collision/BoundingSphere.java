package org.getspout.api.collision;

import org.getspout.api.math.Vector3;

public class BoundingSphere implements CollisionVolume {
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

	public boolean contains(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return containsBoundingBox((BoundingBox) other);
		} else if (other instanceof BoundingSphere) {
			return containsBoundingSphere((BoundingSphere) other);
		} else if (other instanceof Plane) {
			return containsPlane((Plane) other);
		} else if (other instanceof Ray) {
			return containsRay((Ray) other);
		} else if (other instanceof Segment) {
			return containsSegment((Segment) other);
		}
		return false;
	}

	public boolean containsBoundingBox(BoundingBox b) {
		return CollisionHelper.contains(b, this);
	}

	public boolean containsBoundingSphere(BoundingSphere b) {
		return CollisionHelper.contains(b, this);
	}

	public boolean containsPlane(Plane b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsRay(Ray b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsSegment(Segment b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsPoint(Vector3 b) {
		return CollisionHelper.contains(this, b);
	}

	public String toString() {
		return "BoundingSphere{" + "center=" + center + ", radius=" + radius + '}';
	}

	public boolean intersects(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return intersects((BoundingBox) other);
		}
		if (other instanceof BoundingSphere) {
			return intersects((BoundingSphere) other);
		}
		if (other instanceof Segment) {
			return intersects((Segment) other);
		}
		if (other instanceof Plane) {
			return intersects((Plane) other);
		}
		return false;
	}

	public Vector3 resolve(CollisionVolume start, CollisionVolume end) {
		// TODO Auto-generated method stub
		return null;
	}

}
