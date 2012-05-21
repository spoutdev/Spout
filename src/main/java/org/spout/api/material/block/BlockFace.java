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

import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.flag.ByteFlagMask;

/**
 * Indicates the face of a Block
 */
public enum BlockFace implements ByteFlagMask {
	TOP(0x0, 0, 1, 0),
	BOTTOM(0x1, 0, -1, 0, TOP),
	NORTH(0x2, -1, 0, 0),
	SOUTH(0x4, 1, 0, 0, NORTH),
	EAST(0x8, 0, 0, -1),
	WEST(0xf, 0, 0, 1, EAST),
	THIS(0x20, 0, 0, 0);

	private final byte mask;
	private Vector3 offset;
	private Quaternion direction;
	private BlockFace opposite = this;

	private BlockFace(int mask, int dx, int dy, int dz, BlockFace opposite) {
		this(mask, dx, dy, dz);
		this.opposite = opposite;
		opposite.opposite = this;
	}

	private BlockFace(int mask, int dx, int dy, int dz) {
		this.offset = new Vector3(dx, dy, dz);
		this.direction = new Quaternion(0f, this.offset);
		this.mask = (byte) mask;
	}

	public Quaternion getDirection() {
		return this.direction;
	}

	public Vector3 getOffset() {
		return this.offset;
	}

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
}
