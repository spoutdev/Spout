/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.collision;

import org.spout.api.Spout;
import org.spout.api.math.Vector3;

public class BoundingBox extends CollisionVolume implements Cloneable {
	protected Vector3 min;
	protected Vector3 max;

	public BoundingBox(Vector3 min, Vector3 max) {
		this.min = new Vector3(min);
		this.max = new Vector3(max);
	}
	
	public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.min = new Vector3(minX, minY, minZ);
		this.max = new Vector3(maxX, maxY, maxZ);
	}

	public BoundingBox(Vector3 pos) {
		min = new Vector3(pos);
		max = new Vector3(pos.add(Vector3.ONE));
	}

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
	 * 
	 * @param scale
	 * @return this bounding box
	 */
	public BoundingBox scale(float scale) {
		min = min.multiply(scale);
		max = max.multiply(scale);
		return this;
	}

	/**
	 * Scales this bounding box
	 * 
	 * @param scale
	 * @return this bounding box
	 */
	public BoundingBox scale(Vector3 scale) {
		min = min.multiply(scale);
		max = max.multiply(scale);
		return this;
	}

	/**
	 * Scales this bounding box
	 * 
	 * @param scaleX
	 * @param scaleY
	 * @param scaleZ
	 * @return this bounding box
	 */
	public BoundingBox scale(float scaleX, float scaleY, float scaleZ) {
		min = min.multiply(scaleX, scaleY, scaleZ);
		max = max.multiply(scaleX, scaleY, scaleZ);
		return this;
	}

	/**
	 * Sets the location of the bounding box edges
	 * 
	 * @param minX
	 * @param minY
	 * @param minZ
	 * @param maxX
	 * @param maxY
	 * @param maxZ
	 * @return this bounding box
	 */
	public BoundingBox set(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		min = new Vector3(minX, minY, minZ);
		max = new Vector3(maxX, maxY, maxZ);
		return this;
	}
	
	/**
	 * Sets the location of the bounding box edges
	 * 
	 * @param min
	 * @param max
	 * @return this bounding box
	 */
	public BoundingBox set(Vector3 min, Vector3 max) {
		this.min = min;
		this.max = max;
		return this;
	}
	
	/**
	 * Sets this bounding box to the same maximum and minimum edges as the given bounding box
	 * 
	 * @param box
	 * @return this bounding box
	 */
	public BoundingBox set(BoundingBox box) {
		return set(box.min, box.max);
	}

	/**
	 * Adds the vector components to this bounding box
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return this bounding box
	 */
	public BoundingBox add(float x, float y, float z) {
		if (x < 0.0D) {
			this.min = min.add(x, 0, 0);
		}
		else {
			this.max = max.add(x, 0, 0);
		}
		if (y < 0.0D) {
			this.min = min.add(0, y, 0);
		}
		else {
			this.max = max.add(0, y, 0);
		}
		if (z < 0.0D) {
			this.min = min.add(0, 0, z);
		}
		else {
			this.max = max.add(0, 0, z);
		}
		return this;
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
		this.min = min.add(-x, -y, -z);
		this.max = max.add(x, y, z);
		return this;
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
		this.min = min.add(x, y, z);
		this.max = max.add(x, y, z);
		return this;
	}

	/**
	 * Offsets this bounding box in both directions by the given vector
	 * 
	 * @param vec to offset by
	 * @return this bounding box
	 */
	public BoundingBox offset(Vector3 vec) {
		return offset(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	public BoundingBox clone() {
		return new BoundingBox(this);
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
		Spout.log(other.getClass().toString());
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

	public Vector3 resolve(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return CollisionHelper.getCollision(this, (BoundingBox)other);
		}
		return null;
	}

	@Override
	public Vector3 getPosition() {
		
		return min;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BoundingBox) {
			if (other == this) {
				return true;
			} else {
				BoundingBox b = (BoundingBox) other;
				return b.min.equals(this.min) && b.max.equals(this.max);
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "(min=" + this.min + ", max=" + this.max + ')';
	}
	
}
