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

import java.util.Collections;
import java.util.Map;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.Material;
import org.spout.nbt.Tag;

/**
 * Represents an immutable snapshot of the state of a block
 */
public class BlockSnapshot {
	private final Block block;
	private final Material material;
	private final Map<String, Tag> auxData;

	public BlockSnapshot(Block block, Material material, Map<String, Tag> auxData) {
		this.block = block;
		this.material = material;
		this.auxData = Tag.cloneMap(auxData);
	}

	/**
	 * Gets which block corresponding to the snapshot
	 *
	 * @return the block
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Gets the block's material at the time of the snapshot
	 *
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Gets the auxiliary data associated with the block at the time of the snapshot
	 *
	 * @return the auxiliary data, or null if there was no auxiliary data
	 */
	public Map<String, Tag> getAuxData() {
		if (auxData == null) {
			return null;
		} else {
			return Collections.unmodifiableMap(auxData);
		}
	}
}
