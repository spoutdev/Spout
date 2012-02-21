/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.math;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an Axis Aligned Bounding Box
 */
public class AABoundingBox implements Cloneable{
	private Vector3m min;
	private Vector3m max;
	
	/**
	 * Constructs a bounding box from the minimum and maximum edge vectors
	 * 
	 * @param min
	 * @param max
	 */
	public AABoundingBox(Vector3 min, Vector3 max) {
		this.min = new Vector3m(min);
		this.max = new Vector3m(max);
	}
	
	/**
	 * Constructs a copy of the bounding box
	 * 
	 * @param box to copy
	 */
	public AABoundingBox(AABoundingBox box) {
		this(box.min, box.max);
	}

	/**
	 * Constructs a bounding box from the components of minimum and maximum vectors
	 * 
	 * @param minX
	 * @param minY
	 * @param minZ
	 * @param maxX
	 * @param maxY
	 * @param maxZ
	 */
	public AABoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.min = new Vector3m(minX, minY, minZ);
		this.max = new Vector3m(maxX, maxY, maxZ);
	}
	
	/**
	 * Gets the minimum edge vector for this bounding box. <br/>
	 * <br/>
	 * <b>Note:</b> Modifications to this vector will modify the bounding box.
	 * 
	 * @return minimum edge
	 */
	public Vector3m getMin() {
		return min;
	}
	
	/**
	 * Gets the maximum edge vector for this bounding box. <br/>
	 * <br/>
	 * <b>Note:</b> Modifications to this vector will modify the bounding box.
	 * 
	 * @return maximum edge
	 */
	public Vector3m getMax() {
		return max;
	}
	
	/**
	 * Adds the vector components to this bounding box
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return this bounding box
	 */
	public AABoundingBox add(float x, float y, float z) {
		if (x < 0.0D) {
			min.add(x, 0, 0);
		}
		else {
			max.add(x, 0, 0);
		}
		if (y < 0.0D) {
			min.add(0, y, 0);
		}
		else {
			max.add(0, y, 0);
		}
		if (z < 0.0D) {
			min.add(0, 0, z);
		}
		else {
			max.add(0, 0, z);
		}
		return this;
	}
	
	/**
	 * Adds the vector to this bounding box
	 * 
	 * @param vec to add
	 * @return this bounding box
	 */
	public AABoundingBox add(Vector3 vec) {
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
	public AABoundingBox expand(float x, float y, float z) {
		min.add(-x, -y, -z);
		max.add(x, y, z);
		return this;
	}
	
	/**
	 * Expands this bounding box in both directions by the given vector
	 * 
	 * @param vec to expand by
	 * @return this bounding box
	 */
	public AABoundingBox expand(Vector3 vec) {
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
	public AABoundingBox contract(float x, float y, float z) {
		return expand(-x, -y, -z);
	}
	
	/**
	 * Contracts this bounding box in both directions by the given vector
	 * 
	 * @param vec to contract by
	 * @return this bounding box
	 */
	public AABoundingBox contract(Vector3 vec) {
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
	public AABoundingBox offset(float x, float y, float z) {
		min.add(x, y, z);
		max.add(x, y, z);
		return this;
	}
	
	/**
	 * Offsets this bounding box in both directions by the given vector
	 * 
	 * @param vec to offset by
	 * @return this bounding box
	 */
	public AABoundingBox offset(Vector3 vec) {
		return offset(vec.getX(), vec.getY(), vec.getZ());
	}
	
	/**
	 * True if the given vector is contained fully inside of this bounding box
	 * 
	 * @param vec
	 * @return inside
	 */
	public boolean inside(Vector3 vec) {
		if (vec.getX() > min.getX() && vec.getX() < max.getX()) {
			if (vec.getY() > min.getY() && vec.getY() < max.getY()) {
				if (vec.getZ() > min.getZ() && vec.getZ() < max.getZ()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * True if the bounding box intersects with any part of this bounding box
	 * 
	 * @param box
	 * @return intersects
	 */
	public boolean intersects(AABoundingBox box) {
		if (box.getMax().getX() > min.getX() && box.getMin().getX() < max.getX()) {
			if (box.getMax().getY() > min.getY() && box.getMin().getY() < max.getY()) {
				if (box.getMax().getZ() > min.getZ() && box.getMin().getZ() < max.getZ()) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public AABoundingBox clone() {
		return new AABoundingBox(this);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(min).append(max).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AABoundingBox)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		return equals((AABoundingBox)obj);
	}
	
	public boolean equals(AABoundingBox box) {
		return this.max.equals(box.max) && this.min.equals(box.min);
	}
	
	public String toString() {
		return "Axis Aligned Bounding Box { " + min.toString() + ", " + max.toString() + " }";
	}

}
