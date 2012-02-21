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
 * Represents a inmutable Axis Aligned Bounding Box
 */
public class AABoundingBox{
	protected Vector3 min;
	protected Vector3 max;
	
	/**
	 * Constructs a bounding box from the minimum and maximum edge vectors
	 * 
	 * @param min
	 * @param max
	 */
	public AABoundingBox(Vector3 min, Vector3 max) {
		this.min = new Vector3(min);
		this.max = new Vector3(max);
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
		this.min = new Vector3(minX, minY, minZ);
		this.max = new Vector3(maxX, maxY, maxZ);
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
