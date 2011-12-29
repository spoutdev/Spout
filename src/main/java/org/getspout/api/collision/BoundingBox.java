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
		if(other instanceof BoundingBox){
			return intersects((BoundingBox)other);
		}
		if(other instanceof BoundingSphere){
			return intersects((BoundingSphere)other);
		}
		if(other instanceof Segment){
			return intersects((Segment)other);
		}
		if(other instanceof Plane){
			return intersects((Plane)other);
		}
		return false;
	}

	public Vector3 resolve(CollisionVolume start, CollisionVolume end) {
		// TODO Auto-generated method stub
		return null;
	}
}
