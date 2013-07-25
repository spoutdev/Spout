/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.geo.cuboid.reference;

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;

public interface RegionReference {
	/**
	 * Gets the Region referred to by this reference, loading if required
	 */
	public Region get();

	/**
	 * Gets the Region referred to by this reference
	 *
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 */
	public Region get(LoadOption loadOpt);

	/**
	 * Gets the block at the given packed location
	 *
	 * @param packed the coords in packed int form
	 */
	public BlockMaterial getBlockMaterial(int packed);

	/**
	 * Gets the block at the given packed coords
	 *
	 * @param packed the coords in packed int form
	 * @param loadopt to control whether to load and/or generate the region/chunk, if needed
	 */
	public BlockMaterial getBlockMaterial(int packed, LoadOption loadOpt);

	/**
	 * Sets the block at the given packed coords
	 *
	 * @param packed the coords in packed int form
	 * @param material the block material
	 */
	public BlockMaterial setBlockMaterial(int packed, BlockMaterial material);

	/**
	 * Gets the block at the given packed coords
	 *
	 * @param packed the coords in packed int form
	 * @param material the block material
	 * @param loadopt to control whether to load and/or generate the region/chunk, if needed
	 */
	public BlockMaterial setBlockMaterial(int packed, BlockMaterial material, LoadOption loadOpt);

	/**
	 * Gets the int packed location for the given coordinates.  The coordinates must be in the region or one of its 26 neighbours for correction operation.
	 */
	public int getPackedCoords(int x, int y, int z);
}
