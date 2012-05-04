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
package org.spout.api.generator.biome;

/**
 * Defines an abstract biome selector
 *
 * Biome Selectors provide the logic to determine if the the current x,y,z is in a provided biome.
 *
 * Biome selectors need to override pickBiome, and return a value between 0 and maxbiomes
 *
 *
 */
public abstract class BiomeSelector {
	
	protected BiomeMap parent;
	
	
	
	public final Biome pickBiome(int x, int z, long seed) {
		return pickBiome(x, 0, z, seed);
	}
	/**
	 * The logic to select the biome at the given x,y,z.  The value must be between 0 and maxBiomes
	 * @param x
	 * @param y
	 * @param z
	 * @param seed
	 * @return the biome between 0 and maxBiomes
	 */
	public abstract Biome pickBiome(int x, int y, int z, long seed);
}
