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
package org.spout.api.generator;

import java.util.Random;

import net.royawesome.jlibnoise.module.Module;

import org.spout.api.geo.World;
import org.spout.api.math.MathHelper;

public class WorldGeneratorUtils {
	private final static int HASH_SHIFT = 19;
	private final static long HASH_SHIFT_MASK = (1L << HASH_SHIFT) - 1;

	/**
	 * Returns the particular seed a Random should use for a position
	 *
	 * The meaning of the x, y and z coordinates can be determined by the
	 * generator.
	 *
	 * This gives consistent results for world generation.
	 *
	 * The extra seed allows multiple Randoms to be returned for the same
	 * position for use by populators and different stages of generation.
	 *
	 * @param world the World
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param extraSeed the extra seed value
	 */
	public static long getSeed(World world, int x, int y, int z, int extraSeed) {
		long hash = world.getSeed();
		hash += (hash << HASH_SHIFT) + (hash >> 64 - HASH_SHIFT & HASH_SHIFT_MASK) + extraSeed;
		hash += (hash << HASH_SHIFT) + (hash >> 64 - HASH_SHIFT & HASH_SHIFT_MASK) + x;
		hash += (hash << HASH_SHIFT) + (hash >> 64 - HASH_SHIFT & HASH_SHIFT_MASK) + y;
		hash += (hash << HASH_SHIFT) + (hash >> 64 - HASH_SHIFT & HASH_SHIFT_MASK) + z;

		return hash;
	}

	/**
	 * Gets a pre-seeded random for a particular position.
	 *
	 * The meaning of the x, y and z coordinates can be determined by the
	 * generator.
	 *
	 * The extra seed allows multiple Randoms to be returned for the same
	 * position for use by populators and different stages of generation.
	 *
	 * @param world the World
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param extraSeed the extra seed value
	 * @return the random
	 */
	public static Random getRandom(World world, int x, int y, int z, int extraSeed) {
		Random rng = new Random();
		rng.setSeed(getSeed(world, x, y, z, extraSeed));
		return rng;
	}

	/**
	 * Seeds an array of Randoms for a cuboid of positions.
	 *
	 * The meaning of the x, y and z coordinates can be determined by the
	 * generator.
	 *
	 * The indexes for the array are arranged as array[x][y][z].
	 *
	 * array[sizeX][sizeY][sizeZ] is Random corresponding to the the given
	 * central position.
	 *
	 * The array is a 3d array of size (2 * sizeX + 1, 2 * sizeY + 1, 2 * sizeZ
	 * + 1)
	 *
	 * @param world the World containing the Chunk
	 * @param x the x coordinate for the centre of the array
	 * @param y the y coordinate for the centre of the array
	 * @param z the z coordinate for the centre of the array
	 * @param extraSeed the extra seed value for the randoms
	 * @return the random array
	 */
	public static void seedRandomArray(Random[][][] array, World world, int x, int y, int z, int extraSeed) {
		// TODO - check this conversion
		int lz = array.length;
		int ly = array[0].length;
		int lx = array[0][0].length;

		int sizeX = lx - 1 >> 1;
		int sizeY = ly - 1 >> 1;
		int sizeZ = lz - 1 >> 1;

		for (int cx = 0; cx < lx; cx++) {
			for (int cy = 0; cy < ly; cy++) {
				for (int cz = 0; cz < lz; cz++) {

					array[cx][cy][cz].setSeed(getSeed(world, x + cx - sizeX, y + cy - sizeY, z + cz - sizeZ, extraSeed));
				}
			}
		}
	}

	/**
	 * Gets an array of Randoms for a cuboid of positions.
	 *
	 * The meaning of the x, y and z coordinates can be determined by the
	 * generator.
	 *
	 * The indexes for the array are arranged as array[x][y][z].
	 *
	 * array[sizeX][sizeY][sizeZ] is Random corresponding to the the given
	 * central position.
	 *
	 * The array is a 3d array of size (2 * sizeX + 1, 2 * sizeY + 1, 2 * sizeZ
	 * + 1)
	 *
	 * @param array The array of randoms to seed
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
		int lz = 2 * sizeX + 1;
		int ly = 2 * sizeY + 1;
		int lx = 2 * sizeZ + 1;

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

	/**
	 * Generates a 1D noise map using reduced sampling and linear interpolation
	 *
	 * @param noiseGenerator The noise generator module
	 * @param xSize The size of the 1D map
	 * @param samplingRate The sampling rate to use. xSize % samplingRate must
	 * return 0.
	 * @param x The x coord
	 * @param y The y coord
	 * @param z The z coord
	 * @throws IllegalArgumentException if the noise generator is null, the
	 * samplign rate is zero, or xSize % samplingRate doesn't return 0
	 * @return The noise map
	 */
	public static double[] fastNoise(Module noiseGenerator, int xSize, int samplingRate, int x, int y, int z) {
		if (noiseGenerator == null) {
			throw new IllegalArgumentException("noiseGenerator cannot be null");
		}
		if (samplingRate == 0) {
			throw new IllegalArgumentException("samplingRate cannot be 0");
		}
		if (xSize % samplingRate != 0) {
			throw new IllegalArgumentException("xSize % samplingRate must return 0");
		}
		final double[] noiseArray = new double[xSize + 1];
		for (int xx = 0; xx <= xSize; xx += samplingRate) {
			noiseArray[xx] = noiseGenerator.GetValue(xx + x, y, z);
		}
		for (int xx = 0; xx < xSize; xx++) {
			if (xx % samplingRate != 0) {
				int nx = (xx / samplingRate) * samplingRate;
				noiseArray[xx] = MathHelper.lerp(xx, nx, nx + samplingRate,
						noiseArray[nx], noiseArray[nx + samplingRate]);
			}
		}
		return noiseArray;
	}

	/**
	 * Generates a 2D noise map using reduced sampling and bilinear
	 * interpolation.
	 *
	 * @param noiseGenerator The noise generator module
	 * @param xSize The x size of the 2D map
	 * @param zSize The z size of the 2D map
	 * @param samplingRate The sampling rate to use. xSize % samplingRate and
	 * zSize % samplingRate must return 0.
	 * @param x The x coord
	 * @param y The y coord
	 * @param z The z coord
	 * @throws IllegalArgumentException if the noise generator is null, the
	 * samplign rate is zero, or xSize % samplingRate or zSize % samplingRate
	 * doesn't return 0
	 * @return The noise map
	 */
	public static double[][] fastNoise(Module noiseGenerator, int xSize, int zSize, int samplingRate, int x, int y, int z) {
		if (noiseGenerator == null) {
			throw new IllegalArgumentException("noiseGenerator cannot be null");
		}
		if (samplingRate == 0) {
			throw new IllegalArgumentException("samplingRate cannot be 0");
		}
		if (xSize % samplingRate != 0) {
			throw new IllegalArgumentException("xSize % samplingRate must return 0");
		}
		if (zSize % samplingRate != 0) {
			throw new IllegalArgumentException("zSize % samplingRate must return 0");
		}
		final double[][] noiseArray = new double[xSize + 1][zSize + 1];
		for (int xx = 0; xx <= xSize; xx += samplingRate) {
			for (int zz = 0; zz <= zSize; zz += samplingRate) {
				noiseArray[xx][zz] = noiseGenerator.GetValue(xx + x, y, z + zz);
			}
		}
		for (int xx = 0; xx < xSize; xx++) {
			for (int zz = 0; zz < zSize; zz++) {
				if (xx % samplingRate != 0 || zz % samplingRate != 0) {
					int nx = (xx / samplingRate) * samplingRate;
					int nz = (zz / samplingRate) * samplingRate;
					noiseArray[xx][zz] = MathHelper.biLerp(xx, zz, noiseArray[nx][nz],
							noiseArray[nx][nz + samplingRate], noiseArray[nx + samplingRate][nz],
							noiseArray[nx + samplingRate][nz + samplingRate], nx, nx + samplingRate,
							nz, nz + samplingRate);
				}
			}
		}
		return noiseArray;
	}

	/**
	 * Generates a 3D noise map using reduced sampling and trilinear
	 * interpolation.
	 *
	 * @param noiseGenerator The noise generator module
	 * @param xSize The x size of the 3D map
	 * @param ySize The y size of the 3D map
	 * @param zSize The z size of the 3D map
	 * @param samplingRate The sampling rate to use. xSize % samplingRate, ySize
	 * % samplingRate and zSize % samplingRate must return 0.
	 * @param x The x coord
	 * @param y The y coord
	 * @param z The z coord
	 * @throws IllegalArgumentException if the noise generator is null, the
	 * samplign rate is zero, or xSize % samplingRate, ySize % samplingRate or
	 * zSize % samplingRate doesn't return 0
	 * @return The noise map
	 */
	public static double[][][] fastNoise(Module noiseGenerator, int xSize, int ySize, int zSize,
			int samplingRate, int x, int y, int z) {
		if (noiseGenerator == null) {
			throw new IllegalArgumentException("noiseGenerator cannot be null");
		}
		if (samplingRate == 0) {
			throw new IllegalArgumentException("samplingRate cannot be 0");
		}
		if (xSize % samplingRate != 0) {
			throw new IllegalArgumentException("xSize % samplingRate must return 0");
		}
		if (ySize % samplingRate != 0) {
			throw new IllegalArgumentException("ySize % samplingRate must return 0");
		}
		if (zSize % samplingRate != 0) {
			throw new IllegalArgumentException("zSize % samplingRate must return 0");
		}
		final double[][][] noiseArray = new double[xSize + 1][ySize + 1][zSize + 1];
		for (int xx = 0; xx <= xSize; xx += samplingRate) {
			for (int yy = 0; yy <= ySize; yy += samplingRate) {
				for (int zz = 0; zz <= zSize; zz += samplingRate) {
					noiseArray[xx][yy][zz] = noiseGenerator.GetValue(xx + x, y + yy, z + zz);
				}
			}
		}
		for (int xx = 0; xx < xSize; xx++) {
			for (int yy = 0; yy < ySize; yy++) {
				for (int zz = 0; zz < zSize; zz++) {
					if (xx % samplingRate != 0 || yy % samplingRate != 0 || zz % samplingRate != 0) {
						int nx = (xx / samplingRate) * samplingRate;
						int ny = (yy / samplingRate) * samplingRate;
						int nz = (zz / samplingRate) * samplingRate;
						noiseArray[xx][yy][zz] = MathHelper.triLerp(xx, yy, zz,
								noiseArray[nx][ny][nz], noiseArray[nx][ny + samplingRate][nz],
								noiseArray[nx][ny][nz + samplingRate], noiseArray[nx][ny + samplingRate][nz + samplingRate],
								noiseArray[nx + samplingRate][ny][nz], noiseArray[nx + samplingRate][ny + samplingRate][nz],
								noiseArray[nx + samplingRate][ny][nz + samplingRate], noiseArray[nx + samplingRate][ny + samplingRate][nz + samplingRate],
								nx, nx + samplingRate, ny, ny + samplingRate, nz, nz + samplingRate);
					}
				}
			}
		}
		return noiseArray;
	}
}
