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
package org.spout.api.math;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.util.StringUtil;

/**
 * A 3-dimensional vector represented by int-precision x,y coordinates
 */
public class IntVector3 extends IntVector2 {

	private int z;

	public IntVector3(BlockFace face) {
		this(face.getOffset());
	}

	public IntVector3(Vector3 vector) {
		this(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ());
	}

	public IntVector3(int x, int y, int z) {
		super(x, y);
		this.z = z;
	}
	
	/**
	 * Sets the Z coordinate
	 * 
	 * @param z
	 */
	public void setZ(int z) {
		this.z = z;
	}
	
	/**
	 * Gets the Z coordinate
	 *
	 * @return The Z coordinate
	 */
	public int getZ() {
		return z;
	}
	
	/**
	 * Sets this vector equal to the given vector
	 * 
	 * @param other
	 */
	public void set(IntVector3 v) {
		setX(v.getX());
		setY(v.getY());
		setZ(v.getZ());
	}
	
	/**
	 * Sets this vector equal to the given coordinates
	 */
	public void set(int x, int y, int z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	/**
	 * Adds the given vector to this vector
	 * 
	 * @param other
	 */
	public void add(IntVector3 other) {
		super.add(other);
		z += other.z;
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(getX(), getY(), getZ());
	}
	
	public IntVector3 copy() {
		return new IntVector3(getX(), getY(), getZ());
	}
	
	public static List<IntVector3> createList(int ... coords) {
		if (coords.length % 3 != 0) {
			throw new IllegalArgumentException("The number of coordinates must be a multiple of three to construct a list");
		}
		List<IntVector3> list = new ArrayList<IntVector3>(coords.length / 3);
		for (int i = 0; i < coords.length; i += 3) {
			IntVector3 v = new IntVector3(coords[i], coords[i + 1], coords[i + 2]);
			list.add(v);
		}
		return list;
	}

	public static List<IntVector3> createList(BlockFaces blockFaces) {
		List<IntVector3> list = new ArrayList<IntVector3>(blockFaces.size());
		for (BlockFace face : blockFaces) {
			list.add(new IntVector3(face));
		}
		return list;
	}

	public static List<IntVector3> createList(BlockFace... blockFaces) {
		List<IntVector3> list = new ArrayList<IntVector3>(blockFaces.length);
		for (BlockFace face : blockFaces) {
			list.add(new IntVector3(face));
		}
		return list;
	}
}
