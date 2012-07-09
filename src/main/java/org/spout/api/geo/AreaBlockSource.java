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

import org.spout.api.entity.component.controller.BlockController;
import org.spout.api.generator.biome.Biome;
import org.spout.api.material.BlockMaterial;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

public interface AreaBlockSource {
	/**
	 * Gets the material for the block at (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the block's material from the snapshot
	 */
	@LiveRead
	public BlockMaterial getBlockMaterial(int x, int y, int z);

	/**
	 * Gets the packed BlockFullData for the block at (x, y, z).  
	 * Handler methods are provided by the BlockFullState class.
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the block's full state from the snapshot
	 */
	@LiveRead
	public int getBlockFullState(int x, int y, int z);
	
	/**
	 * Gets the data for the block at (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the block's data from the snapshot
	 */
	@LiveRead
	public short getBlockData(int x, int y, int z);

	/**
	 * Gets the block light level for the block at (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the block's block light level
	 */
	@LiveRead
	public byte getBlockLight(int x, int y, int z);

	/**
	 * Gets the sky light level for the block at (x, y, z)<br>
	 * The returned value is affected by the world sky light that is emitted
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the block's sky light level
	 */
	@LiveRead
	public byte getBlockSkyLight(int x, int y, int z);

	/**
	 * Gets the sky light level for the block at (x, y, z)<br>
	 * The returned value is <b>not</b> affected by the world sky light that is emitted<br>
	 * It is the light level that is actually stored
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the block's sky light level
	 */
	@LiveRead
	public byte getBlockSkyLightRaw(int x, int y, int z);

	/**
	 * Gets the {@link BlockController} for the block at (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the block controller
	 */
	@SnapshotRead
	public BlockController getBlockController(int x, int y, int z);
	
	/**
	 * Gets the biome type at the coordinates.
	 * Returns {@code null} if no biomes are present.
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return The biome type at the location, or null if no biome exists.
	 */
	public Biome getBiomeType(int x, int y, int z);
}