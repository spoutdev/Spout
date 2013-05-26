/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.collision;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.math.Vector3;

/**
 * Represents the amount of volume an object takes up in the {@link World} for purposes of providing collision detection.
 * A bounding box is made of 2 {@link Vector3}s which represent its minimum x,y,z and maximum x,y,z.
 */
public final class BoundingBox extends CollisionVolume implements Cloneable {
	protected final Vector3 min;
	protected final Vector3 max;

	/**
	 * Constructs a bounding box with the minimum and maximum x, y, z values
	 * @param minX
	 * @param minY
	 * @param minZ
	 * @param maxX
	 * @param maxY
	 * @param maxZ
	 */
	public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
	}

	/**
	 * Constructs a bounding box with a center of 0, 0, 0 and half-extents of the x, y, z values
	 * 
	 * @param hx half extent in x
	 * @param hy half extent in y
	 * @param hz half extent in z
	 */
	public BoundingBox(float hx, float hy, float hz) {
		this(new Vector3(hx, hy, hz));
	}
	/**
	 * Constructs a bounding box with a center of 0, 0, 0 and half-extents of the given vector
	 * 
	 * @param halfExtents
	 */
	public BoundingBox(Vector3 halfExtents) {
		this(halfExtents.multiply(-0.5F), halfExtents.multiply(0.5F));
	}

	/**
	 * Constructs a bounding box from 0, 0, 0 to 1, 1, 1.
	 */
	public BoundingBox() {
		this(Vector3.ZERO, Vector3.ONE);
	}

	/**
	 * Constructs a copy of the bounding box
	 * 
	 * @param box to copy
	 */
	public BoundingBox(BoundingBox box) {
		this(box.min, box.max);
	}

	/**
	 * Constructs a bounding box with the given minimum and maximum vectors
	 * 
	 * @param min
	 * @param max
	 */
	public BoundingBox(Vector3 min, Vector3 max) {
		this.min = Vector3.min(min, max);
		this.max = Vector3.max(min, max);
	}

	/**
	 * Gets the minimum edge vector for this bounding box.
	 * 
	 * @return minimum edge
	 */
	public Vector3 getMin() {
		return min;
	}

	/**
	 * Gets the maximum edge vector for this bounding box.
	 * 
	 * @return maximum edge
	 */
	public Vector3 getMax() {
		return max;
	}

	/**
	 * Gets the size vector for this bounding box.
	 * 
	 * @return size
	 */
	public Vector3 getSize() {
		return max.subtract(min);
	}

	/**
	 * Scales this bounding box
	 * Multiplies both the minimum and maximum vectors by the given float
	 * 
	 * @param scale
	 * @return new {@link BoundingBox} scaled
	 */
	public BoundingBox scale(float scale) {
		return new BoundingBox(min.multiply(scale), max.multiply(scale));
	}

	/**
	 * Scales this bounding box
	 * Multiplies both the minimum and maximum vectors by the given {@link Vector3} 
	 * 
	 * @param scale
	 * @return new {@link BoundingBox} scaled
	 */
	public BoundingBox scale(Vector3 scale) {
		return new BoundingBox(min.multiply(scale), max.multiply(scale));
	}

	/**
	 * Scales this bounding box
	 * Multiplies both the minimum and maximum vectors by the given x, y and z.
	 * 
	 * @param scaleX
	 * @param scaleY
	 * @param scaleZ
	 * @return new {@link BoundingBox} scaled
	 */
	public BoundingBox scale(float scaleX, float scaleY, float scaleZ) {
		return new BoundingBox(min.multiply(scaleX, scaleY, scaleZ), max.multiply(scaleX, scaleY, scaleZ));
	}

	/**
	 * Adds the vector components to this bounding box
	 * 
	 * @param x to add
	 * @param y to add
	 * @param z to add
	 * @return new {@link BoundingBox} with the new values
	 */
	public BoundingBox add(float x, float y, float z) {
		Vector3 newMin = min;
		Vector3 newMax = max;
		if (x < 0.0D) {
			newMin = min.add(x, 0, 0);
		}
		else {
			newMax = max.add(x, 0, 0);
		}
		if (y < 0.0D) {
			newMin = min.add(0, y, 0);
		}
		else {
			newMax = max.add(0, y, 0);
		}
		if (z < 0.0D) {
			newMin = min.add(0, 0, z);
		}
		else {
			newMax = max.add(0, 0, z);
		}
		return new BoundingBox(newMin, newMax);
	}

	/**
	 * Adds the vector to this bounding box
	 * 
	 * @param vec to add
	 * @return this bounding box
	 */
	public BoundingBox add(Vector3 vec) {
		return add(vec.getX(), vec.getY(), vec.getZ());
	}

	/**
	 * Expands this bounding box in both directions by the given vector components
	 * 
	 * @param x to expand
	 * @param y to expand
	 * @param z to expand
	 * @return this bounding box
	 */
	public BoundingBox expand(float x, float y, float z) {
		return new BoundingBox(min.add(-x, -y, -z), max.add(x, y, z));
	}

	/**
	 * Expands this bounding box in both directions by the given vector
	 * 
	 * @param vec to expand by
	 * @return this bounding box
	 */
	public BoundingBox expand(Vector3 vec) {
		return expand(vec.getX(), vec.getY(), vec.getZ());
	}

	/**
	 * Contracts this bounding box in both directions by the given vector components
	 * 
	 * @param x to contract
	 * @param y to contract
	 * @param z to contract
	 * @return this bounding box
	 */
	public BoundingBox contract(float x, float y, float z) {
		return expand(-x, -y, -z);
	}

	/**
	 * Contracts this bounding box in both directions by the given vector
	 * 
	 * @param vec to contract by
	 * @return this bounding box
	 */
	public BoundingBox contract(Vector3 vec) {
		return contract(vec.getX(), vec.getY(), vec.getZ());
	}

	/**
	 * Offsets this bounding box in both directions by the given vector components
	 * 
	 * @param x to offset
	 * @param y to offset
	 * @param z to offset
	 * @return this bounding box
	 */
	public BoundingBox offset(float x, float y, float z) {
		return offset(new Vector3(x, y, z));
	}

	/**
	 * Offsets this bounding box in both directions by the given vector
	 * 
	 * @param vec to offset by
	 * @return this bounding box
	 */
	@Override
	public BoundingBox offset(Vector3 vec) {
		return new BoundingBox(min.add(vec), max.add(vec));
	}

	@Override
	public BoundingBox clone() {
		return new BoundingBox(this);
	}

	/**
	 * Checks if the bounding boxes intersect at all.
	 * 
	 * @param bounding box to check
	 * @return true if the bounding boxes collide, false if they do not.
	 */
	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(this, b);
	}

	/**
	 * Checks if this bounding box intersects with the given {@link BoundingSphere}.
	 * 
	 * @param the bounding sphere
	 * @return true if they collide, false if they do not.
	 */
	public boolean intersects(BoundingSphere b) {
		return CollisionHelper.checkCollision(this, b);
	}

	/**
	 * Checks if the given {@link Segment} collides with this bounding box.
	 * 
	 * @param the segment
	 * @return true if they collide, false if they do not.
	 */
	public boolean intersects(Segment b) {
		return CollisionHelper.checkCollision(this, b);
	}

	/**
	 * Checks if the given {@link Plane} collides with this bounding box.
	 * 
	 * @param the plane
	 * @return true if they collide, false if they do not.
	 */
	public boolean intersects(Plane b) {
		return CollisionHelper.checkCollision(this, b);
	}

	/**
	 * Checks if the given {@link CollisionVolume} collides with this bounding box
	 * 
	 * @param the CollisionVolume
	 * @return true if they collide, false if they do not.
	 */
	@Override
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
	
	/**
	 * Checks if the given {@link CollisionVolume} is wholly contained within this bounding box.
	 * This will always return false for any {@link Plane}, or {@link Ray}.
	 * 
	 * @param the CollisionVolume to check
	 * @return true if it is contained, false if it is not.
	 */
	@Override
	public boolean contains(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return containsBoundingBox((BoundingBox) other);
		}

		if (other instanceof BoundingSphere) {
			return containsBoundingSphere((BoundingSphere) other);
		}

		if (other instanceof Plane) {
			return containsPlane((Plane) other);
		}

		if (other instanceof Ray) {
			return containsRay((Ray) other);
		}

		if (other instanceof Segment) {
			return containsSegment((Segment) other);
		}

		return false;
	}

	/**
	 * Checks if this bounding box wholly contains the given BoundingBox.
	 * 
	 * @param boundingbox to check
	 * @return true if it is contained, otherwise false.
	 */
	public boolean containsBoundingBox(BoundingBox b) {
		return CollisionHelper.contains(this, b);
	}

	/**
	 * Checks if this bounding box wholly contains the given {@link BoundingSphere}
	 * 
	 * @param boundingsphere to check
	 * @return true if it is contained, otherwise false.
	 */
	public boolean containsBoundingSphere(BoundingSphere b) {
		return CollisionHelper.contains(this, b);
	}

	/**
	 * Checks if this BoundingBox contains the given {@link Plane}.
	 * 
	 * Will always return false, boxes can not wholly contain a plane as they are finite.
	 * 
	 * @param the plane.
	 * @return false
	 */
	public boolean containsPlane(Plane b) {
		return CollisionHelper.contains(this, b);
	}

	/**
	 * Checks if this BoundingBox contains the given {@link Ray}.
	 * 
	 * Will always return false, boxes can not wholly contain a ray as they are finite.
	 * 
	 * @param the ray.
	 * @return false
	 */
	public boolean containsRay(Ray b) {
		return CollisionHelper.contains(this, b);
	}

	/**
	 * Checks if this BoundingBox wholly contains the given {@link Segment}.
	 * 
	 * @param the segment to check
	 * @return true if it is contained in the boundingbox, otherwise false.
	 */
	public boolean containsSegment(Segment b) {
		return CollisionHelper.contains(this, b);
	}

	/**
	 * Checks if this BoundingBox wholly contains the given {@link Vector3}
	 * 
	 * @param the Vector3 to check
	 * @return true if it is contained in the boundingbox, otherwise false
	 */
	@Override
	public boolean containsPoint(Vector3 b) {
		return CollisionHelper.contains(this, b);
	}

	/**
	 * Gets a {@link Vector3} representation of the collision point between this
	 * and another BoundingBox. <br/>
	 * This will return null for any {@link CollisionVolume} passed in that is not also a {@link BoundingBox}.
	 * 
	 * @param CollisionVolume to check
	 * @return Vector3 where the BoundingBoxes collide, or null if they do not collide.
	 */
	@Override
	public Vector3 resolve(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return CollisionHelper.getCollision(this, (BoundingBox)other);
		}

		return null;
	}

	public Vector3 resolveStatic(BoundingBox other) {
		return CollisionHelper.getCollisionStatic(this, other);
	}

	@Override
	public Vector3 getPosition() {
		return min;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 27).append(min).append(max).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof BoundingBox)) {
			return false;
		}

		if (other == this) {
			return true;
		}

		BoundingBox b = (BoundingBox) other;
		return b.min.equals(this.min) && b.max.equals(this.max);
	}

	@Override
	public String toString() {
		return "(min=" + this.min + ", max=" + this.max + ')';
	}
}