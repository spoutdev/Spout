package org.getspout.server;

import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Class representing a snapshot of a chunk.
 */
public class SpoutChunkSnapshot implements ChunkSnapshot {
	private final int x, z;
	private final int worldHeight;
	private final String world;
	private final long time;
	private final byte[] types, metaData, skyLight, blockLight, height;
	private final double[] temp, humid;
	private final Biome[] biomes;

	public SpoutChunkSnapshot(int x, int z, World world, byte[] types, byte[] metaData, byte[] skyLight, byte[] blockLight, boolean svHeight, boolean svBiome, boolean svTemp) {
		this.x = x;
		this.z = z;
		this.world = world.getName();
		time = world.getFullTime();
		worldHeight = world.getMaxHeight();
		this.types = types;
		this.metaData = metaData;
		this.skyLight = skyLight;
		this.blockLight = blockLight;

		if (svHeight) {
			height = new byte[16 * 16];
			for (int xx = 0; xx < 16; ++xx) {
				for (int zz = 0; zz < 16; ++zz) {
					height[coordToIndex(xx, zz)] = (byte) world.getHighestBlockYAt(16 * x + xx, 16 * z + zz);
				}
			}
		} else {
			height = null;
		}

		if (svBiome) {
			biomes = new Biome[16 * 16];
			for (int xx = 0; xx < 16; ++xx) {
				for (int zz = 0; zz < 16; ++zz) {
					biomes[coordToIndex(xx, zz)] = world.getBiome(16 * x + xx, 16 * z + zz);
				}
			}
		} else {
			biomes = null;
		}

		if (svTemp) {
			temp = new double[16 * 16];
			humid = new double[16 * 16];
			for (int xx = 0; xx < 16; ++xx) {
				for (int zz = 0; zz < 16; ++zz) {
					temp[coordToIndex(xx, zz)] = world.getTemperature(16 * x + xx, 16 * z + zz);
					humid[coordToIndex(xx, zz)] = world.getHumidity(16 * x + xx, 16 * z + zz);
				}
			}
		} else {
			temp = humid = null;
		}
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public String getWorldName() {
		return world;
	}

	@Override
	public long getCaptureFullTime() {
		return time;
	}

	@Override
	public int getBlockTypeId(int x, int y, int z) {
		return types[coordToIndex(x, y, z)];
	}

	@Override
	public int getBlockData(int x, int y, int z) {
		return metaData[coordToIndex(x, y, z)];
	}

	@Override
	public int getBlockSkyLight(int x, int y, int z) {
		return skyLight[coordToIndex(x, y, z)];
	}

	@Override
	public int getBlockEmittedLight(int x, int y, int z) {
		return blockLight[coordToIndex(x, y, z)];
	}

	@Override
	public int getHighestBlockYAt(int x, int z) {
		return height[coordToIndex(x, z)];
	}

	@Override
	public Biome getBiome(int x, int z) {
		return biomes[coordToIndex(x, z)];
	}

	@Override
	public double getRawBiomeTemperature(int x, int z) {
		return temp[coordToIndex(x, z)];
	}

	@Override
	public double getRawBiomeRainfall(int x, int z) {
		return humid[coordToIndex(x, z)];
	}

	private int coordToIndex(int x, int y, int z) {
		if (x < 0 || z < 0 || y < 0 || x >= SpoutChunk.WIDTH || z >= SpoutChunk.DEPTH || y >= worldHeight) {
			throw new IndexOutOfBoundsException();
		}

		return (x * SpoutChunk.DEPTH + z) * worldHeight + y;
	}

	private int coordToIndex(int x, int z) {
		if (x < 0 || z < 0 || x >= SpoutChunk.WIDTH || z >= SpoutChunk.DEPTH) {
			throw new IndexOutOfBoundsException();
		}

		return x * SpoutChunk.DEPTH + z;
	}

	public static class EmptySnapshot extends SpoutChunkSnapshot {

		public EmptySnapshot(int x, int z, World world, boolean svBiome, boolean svTemp) {
			super(x, z, world, null, null, null, null, false, svBiome, svTemp);
		}

		@Override
		public int getBlockTypeId(int x, int y, int z) {
			return 0;
		}

		@Override
		public int getBlockData(int x, int y, int z) {
			return 0;
		}

		@Override
		public int getBlockSkyLight(int x, int y, int z) {
			return 0;
		}

		@Override
		public int getBlockEmittedLight(int x, int y, int z) {
			return 0;
		}

		@Override
		public int getHighestBlockYAt(int x, int z) {
			return 0;
		}
	}
}
