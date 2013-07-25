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
package org.spout.api.material.block;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.util.hashing.ShortPairHashed;

/**
 * Represents an immutable snapshot of the state of a block
 */
public class BlockSnapshot {
	private final BlockMaterial material;
	private final short data;
	private final int x, y, z;
	private final World world;

	public BlockSnapshot(Block block) {
		this(block, block.getMaterial(), block.getBlockData());
	}

	public BlockSnapshot(Block block, BlockMaterial material, short data) {
		this(block.getWorld(), block.getX(), block.getY(), block.getZ(), material, data);
	}

	public BlockSnapshot(World world, int x, int y, int z, BlockMaterial material, short data) {
		this.material = material;
		this.data = data;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	/**
	 * Gets the x-coordinate of this Block snapshot
	 * @return the x-coordinate
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Gets the y-coordinate of this Block snapshot
	 * @return the y-coordinate
	 */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Gets the z-coordinate of this Block snapshot
	 * @return the z-coordinate
	 */
	public int getZ() {
		return this.z;
	}

	/**
	 * Gets the world this Block snapshot is in
	 * 
	 * @return the World
	 */
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * Gets which block corresponding to the snapshot
	 *
	 * @return the block
	 */
	public Block getBlock() {
		return this.world.getBlock(this.x, this.y, this.z);
	}

	/**
	 * Gets the block's material at the time of the snapshot
	 *
	 * @return the material
	 */
	public BlockMaterial getMaterial() {
		return this.material;
	}

	public short getData() {
		return this.data;
	}

	@Override
	public String toString() {
		return "BlockSnapshot { material: " + this.material + " , data:" + this.data + ", x: " + x + ", y: " + y + ", z: " + z + "}";
	}

	@Override
	public int hashCode() {
		return ShortPairHashed.key(this.material.getId(), this.data);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BlockSnapshot) {
			BlockSnapshot other = (BlockSnapshot)o;
			return other.x == x && other.y == y && other.z == z && other.material.equals(material) && other.data == data;
		}
		return false;
	}

	public boolean isMaterial(Material... materials) {
		if (this.material == null) {
			for (Material material : materials) {
				if (material == null) {
					return true;
				}
			}
			return false;
		} else {
			return this.material.isMaterial(materials);
		}
	}
}
