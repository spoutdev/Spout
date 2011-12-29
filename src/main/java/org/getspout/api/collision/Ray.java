package org.getspout.api.collision;

import org.getspout.api.math.MathHelper;
import org.getspout.api.math.Vector3;

public class Ray implements CollisionVolume {
	/**
	 * Maximum length for a ray. Calculated as BlockLength*BlocksPerChunk* 10
	 * chunks
	 */
	//MaxChunks (10) can be modified as we need
	static final int MAXLENGTH = 10 * 16 * 16;

	Vector3 origin;

	Vector3 direction;

	public Ray(Vector3 start, Vector3 direction) {
		this.origin = start;
		this.direction = direction;
	}

	public Ray(Vector3 start, float pitch, float yaw) {
		this(start, MathHelper.getDirectionVector(pitch, yaw));
	}

//	public boolean intersects(BoundingBox b) {
//		return CollisionHelper.checkCollision(b, this);
//	}
//
	public boolean intersects(BoundingSphere b) {
		return CollisionHelper.checkCollision(b, this);
	}
//
//	public boolean intersects(Ray b) {
//		return CollisionHelper.checkCollision(this, b);
//	}
//
//	public boolean intersects(Plane b) {
//		return CollisionHelper.checkCollision(this, b);
//	}

	public boolean intersects(CollisionVolume other) {
		if(other instanceof BoundingBox){
			return intersects((BoundingBox)other);
		}
		if(other instanceof BoundingSphere){
			return intersects((BoundingSphere)other);
		}
		if(other instanceof Segment){
			//return intersects((Segment)other);
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
