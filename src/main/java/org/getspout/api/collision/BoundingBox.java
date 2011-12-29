package org.getspout.api.collision;

import org.getspout.api.math.Vector3;

public class BoundingBox implements CollisionVolume {
	Vector3 min;

	Vector3 max;

	public BoundingBox(Vector3 min, Vector3 max) {
		this.min = min;
		this.max = max;

	}

	public BoundingBox(Vector3 pos) {
		min = pos;
		max = pos.add(Vector3.ONE);
	}

	public BoundingBox() {
		this(Vector3.ZERO, Vector3.ONE);
	}

	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(this, b);
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

	public boolean intersects(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return intersects((BoundingBox) other);
		} else if (other instanceof BoundingSphere) {
			return intersects((BoundingSphere) other);
		} else if (other instanceof Segment) {
			return intersects((Segment) other);
		} else if (other instanceof Plane) {
			return intersects((Plane) other);
		}
		return false;
	}


	public boolean contains(CollisionVolume other) {
		return other.containsBoundingBox(this);
	}

	public boolean containsBoundingBox(BoundingBox b) {
		return CollisionHelper.contains(b, this);
	}

	public boolean containsBoundingSphere(BoundingSphere b) {
		return CollisionHelper.contains(this, b);
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

	public Vector3 resolve(CollisionVolume start, CollisionVolume end) {
		// TODO Auto-generated method stub
		return null;
	}

}
