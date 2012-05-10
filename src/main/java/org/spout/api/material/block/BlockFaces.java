/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.material.block;

import java.util.Arrays;
import java.util.Iterator;

import org.spout.api.util.flag.ByteFlagMask;

/**
 * Contains several BlockFace array constants and functions to operate on them
 */
public class BlockFaces implements Iterable<BlockFace>, ByteFlagMask {

	/**
	 * The [north-east-south-west] faces
	 */
	public static final BlockFaces NESW = new BlockFaces(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

	/**
	 * The [north-south-east-west] faces
	 */
	public static final BlockFaces NSEW = new BlockFaces(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	/**
	 * The [east-west-south-north] faces
	 */
	public static final BlockFaces EWSN = new BlockFaces(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH);

	/**
	 * The [north-south-west-east] faces
	 */
	public static final BlockFaces NSWE = new BlockFaces(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST);

	/**
	 * The [south-west-north-east] faces
	 */
	public static final BlockFaces SWNE = new BlockFaces(BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST);

	/**
	 * The [west-north-east-south] faces
	 */
	public static final BlockFaces WNES = new BlockFaces(BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH);

	/**
	 * The [south-north-east-west] faces
	 */
	public static final BlockFaces SNEW = new BlockFaces(BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST);

	/**
	 * The [west-east-south-north] faces
	 */
	public static final BlockFaces WESN = new BlockFaces(BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH);

	/**
	 * The [east-south-west-north] faces
	 */
	public static final BlockFaces ESWN = new BlockFaces(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH);

	/**
	 * The [north-east-south-west-bottom] faces
	 */
	public static final BlockFaces NESWB = new BlockFaces(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.BOTTOM);

	/**
	 * The [east-west-south-north-bottom] faces
	 */
	public static final BlockFaces EWSNB = new BlockFaces(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.BOTTOM);

	/**
	 * The [north-east-south-west-top] faces
	 */
	public static final BlockFaces NESWT = new BlockFaces(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.TOP);

	/**
	 * The [north-east-south-west-bottom-top] faces
	 */
	public static final BlockFaces NESWBT = new BlockFaces(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.BOTTOM, BlockFace.TOP);

	/**
	 * The [bottom-top-east-west-north-south] faces
	 */
	public static final BlockFaces BTEWNS = new BlockFaces(BlockFace.BOTTOM, BlockFace.TOP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);

	/**
	 * The [bottom-top-north-south-west-east] faces
	 */
	public static final BlockFaces BTNSWE = new BlockFaces(BlockFace.BOTTOM, BlockFace.TOP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST);

	/**
	 * The [north-south-east-west-bottom] faces
	 */
	public static final BlockFaces NSEWB = new BlockFaces(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.BOTTOM);

	/**
	 * The [north-south-west-east-bottom] faces
	 */
	public static final BlockFaces NSWEB = new BlockFaces(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.BOTTOM);

	/**
	 * The [top-bottom-north-south-east-west-this] faces
	 */
	public static final BlockFaces ALL = new BlockFaces(BlockFace.values());

	/**
	 * A constant containing no faces at all
	 */
	public static final BlockFaces NONE = new BlockFaces();

	private final byte mask;
	private final BlockFace[] faces;

	public BlockFaces(BlockFace... blockfaces) {
		this.faces = blockfaces;
		byte mask = 0;
		for (BlockFace face : this.faces) {
			mask |= face.getMask();
		}
		this.mask = mask;
	}

	@Override
	public byte getMask() {
		return this.mask;
	}

	@Override
	public Iterator<BlockFace> iterator() {
		return Arrays.asList(this.faces).iterator();
	}

	/**
	 * Checks if this block face constant contains the face given
	 * @param face to look for
	 * @return True if found, False if not
	 */
	public boolean contains(BlockFace face) {
		for (BlockFace bface : this.faces) {
			if (bface == face) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the index of a face in this constant
	 * 
	 * @param face to get the index of
	 * @param def value to return if not found
	 * @return the index in the constant, or the def value if not found
	 */
	public int indexOf(BlockFace face, int def) {
		for (int i = 0; i < this.faces.length; i++) {
			if (this.faces[i] == face) {
				return i;
			}
		}
		return def;
	}

	/**
	 * Gets the face from this constant at the index given<br>
	 * If the index is out of range, the first or last element is returned
	 * 
	 * @param index to get at
	 * @return the BlockFace
	 */
	public BlockFace get(int index) {
		if (index < 0) {
			return this.faces[0];
		} else if (index >= this.faces.length) {
			return this.faces[this.faces.length - 1];
		} else {
			return this.faces[index];
		}
	}

	/**
	 * Gets the face from this constant at the index given<br>
	 * If the index is out of range, the default is returned
	 * 
	 * @param index to get at
	 * @param def if the index is out of range
	 * @return the BlockFace
	 */
	public BlockFace get(int index, BlockFace def) {
		if (index < 0 || index >= this.faces.length) {
			return def;
		} else {
			return this.faces[index];
		}
	}
}
