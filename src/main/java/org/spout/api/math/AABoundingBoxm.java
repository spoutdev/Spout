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

/**
 * Represents a mutable Axis Aligned Bounding Box
 */
public class AABoundingBoxm extends AABoundingBox implements Cloneable{
	/**
	 * Constructs a bounding box from the minimum and maximum edge vectors
	 * 
	 * @param getMin()
	 * @param getMax()
	 */
	public AABoundingBoxm(Vector3 min, Vector3 max) {
		super(new Vector3m(min), new Vector3m(max));
	}

	/**
	 * Constructs a copy of the bounding box
	 * 
	 * @param box to copy
	 */
	public AABoundingBoxm(AABoundingBox box) {
		super(box.getMin(), box.getMax());
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
	public AABoundingBoxm(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		super(new Vector3m(minX, minY, minZ), new Vector3m(maxX, maxY, maxZ));
	}

	/**
	 * Gets the minimum edge vector for this bounding box. <br/>
	 * <br/>
	 * <b>Note:</b> Modifications to this vector will modify the bounding box.
	 * 
	 * @return minimum edge
	 */
	public Vector3m getMin() {
		return (Vector3m)min;
	}

	/**
	 * Gets the maximum edge vector for this bounding box. <br/>
	 * <br/>
	 * <b>Note:</b> Modifications to this vector will modify the bounding box.
	 * 
	 * @return maximum edge
	 */
	public Vector3m getMax() {
		return (Vector3m)max;
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
	public AABoundingBoxm set(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		getMin().setX(minX);
		getMin().setY(minY);
		getMin().setZ(minZ);
		getMax().setX(maxX);
		getMax().setY(maxY);
		getMax().setZ(maxZ);
		return this;
	}
	
	/**
	 * Sets the location of the bounding box edges
	 * 
	 * @param min
	 * @param max
	 * @return this bounding box
	 */
	public AABoundingBoxm set(Vector3 min, Vector3 max) {
		getMin().set(min);
		getMax().set(max);
		return this;
	}
	
	/**
	 * Sets this bounding box to the same maximum and minimum edges as the given bounding box
	 * 
	 * @param box
	 * @return this bounding box
	 */
	public AABoundingBoxm set(AABoundingBox box) {
		return set(box.getMin(), box.getMax());
	}

	/**
	 * Adds the vector components to this bounding box
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return this bounding box
	 */
	public AABoundingBoxm add(float x, float y, float z) {
		if (x < 0.0D) {
			getMin().add(x, 0, 0);
		}
		else {
			getMax().add(x, 0, 0);
		}
		if (y < 0.0D) {
			getMin().add(0, y, 0);
		}
		else {
			getMax().add(0, y, 0);
		}
		if (z < 0.0D) {
			getMin().add(0, 0, z);
		}
		else {
			getMax().add(0, 0, z);
		}
		return this;
	}

	/**
	 * Adds the vector to this bounding box
	 * 
	 * @param vec to add
	 * @return this bounding box
	 */
	public AABoundingBoxm add(Vector3 vec) {
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
	public AABoundingBoxm expand(float x, float y, float z) {
		getMin().add(-x, -y, -z);
		getMax().add(x, y, z);
		return this;
	}

	/**
	 * Expands this bounding box in both directions by the given vector
	 * 
	 * @param vec to expand by
	 * @return this bounding box
	 */
	public AABoundingBoxm expand(Vector3 vec) {
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
	public AABoundingBoxm contract(float x, float y, float z) {
		return expand(-x, -y, -z);
	}

	/**
	 * Contracts this bounding box in both directions by the given vector
	 * 
	 * @param vec to contract by
	 * @return this bounding box
	 */
	public AABoundingBoxm contract(Vector3 vec) {
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
	public AABoundingBoxm offset(float x, float y, float z) {
		getMin().add(x, y, z);
		getMax().add(x, y, z);
		return this;
	}

	/**
	 * Offsets this bounding box in both directions by the given vector
	 * 
	 * @param vec to offset by
	 * @return this bounding box
	 */
	public AABoundingBoxm offset(Vector3 vec) {
		return offset(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	public AABoundingBoxm clone() {
		return new AABoundingBoxm(this);
	}
}
