package org.getspout.api.collision;

import org.getspout.api.math.MathHelper;
import org.getspout.api.math.Vector3;

public class CollisionRay {
	/**
	 * Maximum length for a ray. Calculated as BlockLength*BlocksPerChunk* 10
	 * chunks
	 */
	//MaxChunks (10) can be modified as we need
	static final int MAXLENGTH = 10 * 16 * 16;

	Vector3 startpoint;
	Vector3 endpoint;

	public CollisionRay(Vector3 start, Vector3 end) {
		startpoint = start;
		endpoint = end;
	}

	public CollisionRay(Vector3 start, Vector3 direction, double distance) {
		this(start, start.add(direction.scale(distance)));
	}

	public CollisionRay(Vector3 start, double pitch, double yaw, double distance) {
		this(start, MathHelper.getDirectionVector(pitch, yaw), distance);
	}

}
