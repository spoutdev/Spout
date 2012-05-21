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
package org.spout.api.geo;

import org.spout.api.geo.cuboid.Region;
import org.spout.api.math.Vector3;
import org.spout.api.util.thread.LiveRead;

public interface AreaRegionAccess extends AreaChunkAccess {

	/**
	 * Gets the {@link Region} at region coordinates (x, y, z)
	 *
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(int x, int y, int z);

	/**
	 * Gets the {@link Region} at region coordinates (x, y, z)
	 *
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @param load true if the region should be loaded/generated
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(int x, int y, int z, boolean load);

	/**
	 * Gets the {@link Region} at region coordinates (x, y, z)
	 *
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(int x, int y, int z, LoadGenerateOption loadopt);

	/**
	 * Gets the {@link Region} at block coordinates (x, y, z)
	 *
	 * @param x the block x coordinate
	 * @param y the block y coordinate
	 * @param z the block z coordinate
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(int x, int y, int z);

	/**
	 * Gets the {@link Region} at block coordinates (x, y, z)
	 *
	 * @param x the block x coordinate
	 * @param y the block y coordinate
	 * @param z the block z coordinate
	 * @param load true if the region should be loaded/generated
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(int x, int y, int z, boolean load);

	/**
	 * Gets the {@link Region} at block coordinates (x, y, z)
	 *
	 * @param x the block x coordinate
	 * @param y the block y coordinate
	 * @param z the block z coordinate
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(int x, int y, int z, LoadGenerateOption loadopt);

	/**
	 * Gets the {@link Region} at block coordinates (x, y, z)
	 *
	 * @param position of the block
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(Vector3 position);

	/**
	 * Gets the {@link Region} at block coordinates (x, y, z)
	 *
	 * @param position of the block
	 * @param load true if the region should be loaded/generated
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(Vector3 position, boolean load);

	/**
	 * Gets the {@link Region} at block coordinates (x, y, z)
	 *
	 * @param position of the block
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(Vector3 position, LoadGenerateOption loadopt);
}
