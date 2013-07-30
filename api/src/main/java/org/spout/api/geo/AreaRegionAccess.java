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
package org.spout.api.geo;

import java.util.Collection;

import org.spout.api.geo.cuboid.Region;
import org.spout.math.vector.Vector3;
import org.spout.api.util.thread.annotation.LiveRead;

public interface AreaRegionAccess extends AreaChunkAccess {
	/**
	 * Gets an unmodifiable collection of all loaded regions
	 *
	 * @return all loaded regions
	 */
	public Collection<Region> getRegions();

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
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(int x, int y, int z, LoadOption loadopt);

	/**
	 * Gets the {@link Region} at chunk coordinates (x, y, z)
	 *
	 * @param x the chunk x coordinate
	 * @param y the chunk y coordinate
	 * @param z the chunk z coordinate
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromChunk(int x, int y, int z);

	/**
	 * Gets the {@link Region} at chunk coordinates (x, y, z)
	 *
	 * @param x the chunk x coordinate
	 * @param y the chunk y coordinate
	 * @param z the chunk z coordinate
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromChunk(int x, int y, int z, LoadOption loadopt);

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
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(int x, int y, int z, LoadOption loadopt);

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
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(Vector3 position, LoadOption loadopt);
}
