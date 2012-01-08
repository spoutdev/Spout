/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.generator;

import java.util.Random;

import org.spout.api.geo.World;

public class WorldGeneratorUtils {
	
	private final static int hashShift = 19;
	private final static long hashShiftMask = (1L << hashShift) - 1;
	
	/**
	 * Seeds a Random for a particular position.
	 * 
	 * The meaning of the x, y and z coordinates can be determined by the generator.
	 * 
	 * This gives consistent results for world generation.
	 * 
	 * The extra seed allows multiple Randoms to be returned for the same position for use by populators and different stages of generation.
	 * 
	 * @param rng the Random
	 * @param world the World
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param extraSeed the extra seed value
	 */
	public void seedRandom(Random rng, World world, int x, int y, int z, int extraSeed) {
		long hash = world.getSeed();
		hash += (hash << hashShift) + ((hash >> (64 - hashShift)) & hashShiftMask) + extraSeed;
		hash += (hash << hashShift) + ((hash >> (64 - hashShift)) & hashShiftMask) + x;
		hash += (hash << hashShift) + ((hash >> (64 - hashShift)) & hashShiftMask) + y;
		hash += (hash << hashShift) + ((hash >> (64 - hashShift)) & hashShiftMask) + z;
		
		rng.setSeed(hash);
	}
	
	/**
	 * Gets a pre-seeded random for a particular position.
	 * 
	 * The meaning of the x, y and z coordinates can be determined by the generator.
	 * 
	 * The extra seed allows multiple Randoms to be returned for the same position for use by populators and different stages of generation.
	 * 
	 * @param world the World
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param extraSeed the extra seed value
	 * @return the random
	 */
	public Random getRandom(World world, int x, int y, int z, int extraSeed) {
		Random rng = new Random();
		seedRandom(rng, world, x, y, z, extraSeed);
		return rng;
	}
	
	/**
	 * Seeds an array of Randoms for a cuboid of positions.
	 * 	 
	 * The meaning of the x, y and z coordinates can be determined by the generator.
	 * 
	 * The indexes for the array are arranged as array[x][y][z].
	 * 
	 * array[sizeX][sizeY][sizeZ] is Random corresponding to the the given central position.
	 * 
	 * The array is a 3d array of size (2 * sizeX + 1, 2 * sizeY + 1, 2 * sizeZ + 1)
	 * 
	 * @param world the World containing the Chunk
	 * @param x the x coordinate for the centre of the array
	 * @param y the y coordinate for the centre of the array
	 * @param z the z coordinate for the centre of the array
	 * @param extraSeed the extra seed value for the randoms
	 * @return the random array
	 */
	public void seedRandomArray(Random[][][] array, World world, int x, int y, int z, int extraSeed) {
		// TODO - check this conversion
		int lz = array.length;
		int ly = array[0].length;
		int lx = array[0][0].length;
		
		int sizeX = (lx - 1) >> 1;
		int sizeY = (ly - 1) >> 1;
		int sizeZ = (lz - 1) >> 1;
		
		for (int cx = 0; cx < lx; cx++) {
			for (int cy = 0; cy < ly; cy++) {
				for (int cz = 0; cz < lz; cz++) {
					seedRandom(array[cx][cy][cz], world, x + cx - sizeX, y + cy - sizeY, z + cz - sizeZ, extraSeed);
				}
			}
		}		
	}
	
	/**
	 * Gets an array of Randoms for a cuboid of positions.
	 * 	 
	 * The meaning of the x, y and z coordinates can be determined by the generator.
	 * 
	 * The indexes for the array are arranged as array[x][y][z].
	 * 
	 * array[sizeX][sizeY][sizeZ] is Random corresponding to the the given central position.
	 * 
	 * The array is a 3d array of size (2 * sizeX + 1, 2 * sizeY + 1, 2 * sizeZ + 1)
	 * 
	 * @param world the World containing the Chunk
	 * @param x the x coordinate for the centre of the array (blockX << 4)
	 * @param y the y coordinate for the centre of the array (blockY << 4)
	 * @param z the z coordinate for the centre of the array (blockZ << 4)
	 * @param sizeX the X distance to the edge
	 * @param sizeY the Y distance to the edge
	 * @param sizeZ the Z distance to the edge
	 * @param extraSeed the extra seed value for the randoms
	 * @return the random array
	 */
	public Random[][][] seedRandomArray(Random[][][] array, World world, int x, int y, int z, int sizeX, int sizeY, int sizeZ, int extraSeed) {
		int lz = (2 * sizeX) + 1;
		int ly = (2 * sizeY) + 1;
		int lx = (2 * sizeZ) + 1;
		
		for (int cx = 0; cx < lx; cx++) {
			for (int cy = 0; cy < ly; cy++) {
				for (int cz = 0; cz < lz; cz++) {
					array[cx][cy][cz] = new Random();
				}
			}
		}
		
		seedRandomArray(array, world, x, y, z, extraSeed);
		return array;
	}
		
}
