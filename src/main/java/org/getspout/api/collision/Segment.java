package org.getspout.api.collision;

import org.getspout.api.math.MathHelper;
import org.getspout.api.math.Vector3;

public class Segment {
	/**
	 * Maximum length for a ray. Calculated as BlockLength*BlocksPerChunk* 10
	 * chunks
	 */
	//MaxChunks (10) can be modified as we need
	static final int MAXLENGTH = 10 * 16 * 16;

	Vector3 origin;

	Vector3 endpoint;

	Vector3 direction;

	final float length;

	public Segment(Vector3 start, Vector3 end) {
		origin = start;
		endpoint = end;
		direction = end.subtract(start).normalize();
		length = (float) MathHelper.sqrt(endpoint.dot(origin));
	}

	public Segment(Vector3 start, Vector3 direction, float distance) {
		this(start, start.add(direction.scale(distance)));
	}

	public Segment(Vector3 start, float pitch, float yaw, float distance) {
		this(start, MathHelper.getDirectionVector(pitch, yaw), distance);
	}

	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(BoundingSphere b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(Segment b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public boolean intersects(Plane b) {
		return CollisionHelper.checkCollision(this, b);
	}

}
