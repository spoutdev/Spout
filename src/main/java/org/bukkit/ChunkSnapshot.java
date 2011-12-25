/*
 * This file is part of Bukkit (http://bukkit.org/).
 *
 * Bukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bukkit;

import org.getspout.api.geo.Biome;

/**
 * Represents a static, thread-safe snapshot of chunk of blocks Purpose is to
 * allow clean, efficient copy of a chunk data to be made, and then handed off
 * for processing in another thread (e.g. map rendering)
 */
public interface ChunkSnapshot {

	/**
	 * Gets the X-coordinate of this chunk
	 *
	 * @return X-coordinate
	 */
	int getX();

	/**
	 * Gets the Z-coordinate of this chunk
	 *
	 * @return Z-coordinate
	 */
	int getZ();

	/**
	 * Gets name of the world containing this chunk
	 *
	 * @return Parent World Name
	 */
	String getWorldName();

	/**
	 * Get block type for block at corresponding coordinate in the chunk
	 *
	 * @param x 0-15
	 * @param y 0-127
	 * @param z 0-15
	 * @return 0-255
	 */
	int getBlockTypeId(int x, int y, int z);

	/**
	 * Get block data for block at corresponding coordinate in the chunk
	 *
	 * @param x 0-15
	 * @param y 0-127
	 * @param z 0-15
	 * @return 0-15
	 */
	int getBlockData(int x, int y, int z);

	/**
	 * Get sky light level for block at corresponding coordinate in the chunk
	 *
	 * @param x 0-15
	 * @param y 0-127
	 * @param z 0-15
	 * @return 0-15
	 */
	int getBlockSkyLight(int x, int y, int z);

	/**
	 * Get light level emitted by block at corresponding coordinate in the chunk
	 *
	 * @param x 0-15
	 * @param y 0-127
	 * @param z 0-15
	 * @return 0-15
	 */
	int getBlockEmittedLight(int x, int y, int z);

	/**
	 * Gets the highest non-air coordinate at the given coordinates
	 *
	 * @param x X-coordinate of the blocks
	 * @param z Z-coordinate of the blocks
	 * @return Y-coordinate of the highest non-air block
	 */
	int getHighestBlockYAt(int x, int z);

	/**
	 * Get biome at given coordinates
	 *
	 * @param x X-coordinate
	 * @param z Z-coordinate
	 * @return Biome at given coordinate
	 */
	Biome getBiome(int x, int z);

	/**
	 * Get raw biome temperature (0.0-1.0) at given coordinate
	 *
	 * @param x X-coordinate
	 * @param z Z-coordinate
	 * @return temperature at given coordinate
	 */
	double getRawBiomeTemperature(int x, int z);

	/**
	 * Get raw biome rainfall (0.0-1.0) at given coordinate
	 *
	 * @param x X-coordinate
	 * @param z Z-coordinate
	 * @return rainfall at given coordinate
	 */
	double getRawBiomeRainfall(int x, int z);

	/**
	 * Get world full time when chunk snapshot was captured
	 *
	 * @return time in ticks
	 */
	long getCaptureFullTime();
}
