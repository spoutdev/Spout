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
package org.spout.api.material.block;

import gnu.trove.map.hash.TIntObjectHashMap;

import net.phys2d.math.MathUtil;

import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.bytebit.ByteBitMask;

/**
 * Indicates the facing of a Block
 */
public enum BlockFace implements ByteBitMask {
	TOP(0x1, 0, 1, 0),
	BOTTOM(0x2, 0, -1, 0, TOP),
	NORTH(0x4, -1, 0, 0),
	SOUTH(0x8, 1, 0, 0, NORTH),
	EAST(0x10, 0, 0, -1),
	WEST(0x20, 0, 0, 1, EAST),
	THIS(0x40, 0, 0, 0);
	
	private final byte mask;
	private Vector3 offset;
	private Quaternion direction;
	private BlockFace opposite = this;
	private static TIntObjectHashMap<BlockFace> offsetMap = new TIntObjectHashMap<BlockFace>(7);

	static {
		for (BlockFace face:values()) {
			offsetMap.put(getOffsetHash(face.getOffset()), face);
		}
	}
	
	private BlockFace(int mask, int dx, int dy, int dz, BlockFace opposite) {
		this(mask, dx, dy, dz);
		this.opposite = opposite;
		opposite.opposite = this;
	}

	private BlockFace(int mask, int dx, int dy, int dz) {
		this.offset = new Vector3(dx, dy, dz);
		this.direction = new Quaternion(0, this.offset);
		this.mask = (byte) mask;
	}
	
	private static int getOffsetHash(Vector3 offset) {
		int x = offset.getFloorX();
		int y = offset.getFloorY();
		int z = offset.getFloorZ();
		x += 1; y += 1; z += 1;
		return x | y << 2 | z << 4;
	}

	/**
	 * Represents the rotation of the BlockFace in the world as a Quaternion.
	 * 
	 * @return the direction of the blockface.
	 */
	public Quaternion getDirection() {
		return this.direction;
	}

	/**
	 * Represents the directional offset of this Blockface as a Vector3.
	 * 
	 * @return the offset of this directional.
	 */
	public Vector3 getOffset() {
		return this.offset;
	}

	/**
	 * Gets the opposite BlockFace.  If this BlockFace has no opposite the method will return itself.
	 * 
	 * @return the opposite BlockFace, or this if it has no opposite.
	 */
	public BlockFace getOpposite() {
		return this.opposite;
	}

	@Override
	public byte getMask() {
		return this.mask;
	}

	/**
	 * Uses a yaw angle to get the north, east, west or south face which points into the same direction.
	 * 
	 * @param yaw to use
	 * @return the block face
	 */
	public static BlockFace fromYaw(float yaw) {
		yaw = MathHelper.wrapAngle(yaw);
		//apply angle differences
		if (yaw >= -135f && yaw < -45f) {
			return BlockFace.NORTH;
		} else if (yaw >= -45f && yaw < 45f) {
			return BlockFace.WEST;
		} else if (yaw >= 45f && yaw < 135f) {
			return BlockFace.SOUTH;
		} else {
			return BlockFace.EAST;
		}
	}
	
	public static BlockFace fromOffset(Vector3 offset) {
		offset = MathHelper.round(MathHelper.normalize(offset));
		return offsetMap.get(getOffsetHash(offset));
	}
}
