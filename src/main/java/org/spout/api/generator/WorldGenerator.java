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
package org.spout.api.generator;

import org.spout.api.util.cuboid.CuboidShortBuffer;

public interface WorldGenerator {
	/**
	 * Gets the block structure for a Chunk.
	 *
	 * The CuboidBuffer will always be exactly one Chunk in size (16x16x16 blocks) and Chunk aligned.
	 *
	 * Structural blocks should not contain any lighting sources and the generator should give repeatable results.
	 *
	 * It is recommended that seeded random number generators from WorldGeneratorUtils are used.
	 *
	 * @param blockData a zeroed CuboidBuffer corresponding to the Chunk
	 */
	public void generate(CuboidShortBuffer blockData, int chunkX, int chunkY, int chunkZ);

	/**
	 * Gets an array of Populators for the world generator
	 *
	 * @return the Populator array
	 */
	public Populator[] getPopulators();
}
