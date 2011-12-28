package org.getspout.api.collision;

import org.getspout.api.math.Vector3;

public class Plane {
	Vector3 point;

	Vector3 normal;

	public Plane(Vector3 point, Vector3 normal) {
		this.point = point;
		this.normal = normal;
	}

	public Plane(Vector3 point) {
		this(point, Vector3.Up);
	}

	public Plane() {
		this(Vector3.ZERO, Vector3.Up);
	}

	public float distance(Vector3 b) {
		return b.subtract(point).dot(normal); 
	}
	
	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(BoundingSphere b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(Segment b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(Plane b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public static Plane fromTwoVectors(Vector3 a, Vector3 b) {
		return new Plane(a, a.cross(b));
	}

}
