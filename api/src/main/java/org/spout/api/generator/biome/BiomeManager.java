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
package org.spout.api.generator.biome;

/**
 * Manages the biomes for a specific volume in the world
 */
public abstract class BiomeManager implements Cloneable {
	private final int chunkX, chunkZ;

	public BiomeManager(int chunkX, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	/**
	 * Gets the x chunk coordinate of the base of the cuboid this biome manager oversees
	 *
	 * @return x chunk coordinate
	 */
	public final int getChunkX() {
		return chunkX;
	}

	/**
	 * Gets the z chunk coordinate of the base of the cuboid this biome manager oversees
	 *
	 * @return z chunk coordinate
	 */
	public final int getChunkZ() {
		return chunkZ;
	}

	/**
	 * Gets the biome at the relative block coords inside the cuboid
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return biome
	 */
	public abstract Biome getBiome(int x, int y, int z);

	/**
	 * Serializes the biome manager to bytes, or returns null if it should not be saved
	 *
	 * @return serialized bytes
	 */
	public abstract byte[] serialize();

	/**
	 * Deserializes the biome manager from an array of bytes
	 */
	public abstract void deserialize(byte[] bytes);

	@Override
	public abstract BiomeManager clone();
}
