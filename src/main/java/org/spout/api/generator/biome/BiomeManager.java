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
package org.spout.api.generator.biome;

/**
 * Manages the biomes for a specific chunk in the world
 */
public abstract class BiomeManager implements Cloneable {
	private final int chunkX, chunkY, chunkZ;

	public BiomeManager(int chunkX, int chunkY, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.chunkZ = chunkZ;
	}

	/**
	 * Gets the x chunk coordinate of the chunk this biome manager oversees
	 *
	 * @return x chunk coordinate
	 */
	public final int getChunkX() {
		return chunkX;
	}

	/**
	 * Gets the y chunk coordinate of the chunk this biome manager oversees
	 *
	 * @return y chunk coordinate
	 */
	public final int getChunkY() {
		return chunkY;
	}

	/**
	 * Gets the z chunk coordinate of the chunk this biome manager oversees
	 *
	 * @return z chunk coordinate
	 */
	public final int getChunkZ() {
		return chunkZ;
	}

	/**
	 * Gets the biome at the relative block coords inside the chunk
	 *
	 * @param x coordinate (0-15)
	 * @param y coordinate (0-15)
	 * @param z coordinate (0-15)
	 * @return biome
	 */
	public abstract Biome getBiome(int x, int y, int z);

	/**
	 * Serializes the biome manager to bytes, or returns null if it should not
	 * be saved
	 *
	 * @return serialized bytes
	 */
	public abstract byte[] serialize();

	/**
	 * Deserializes the biome manager from an array of bytes
	 *
	 * @param bytes
	 */
	public abstract void deserialize(byte[] bytes);

	@Override
	public abstract BiomeManager clone();
}
