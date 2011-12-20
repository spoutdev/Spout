package org.getspout.server.generator;

import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import org.getspout.server.block.BlockID;

/**
 * A generator for subterranean worlds, in particular the Nether.
 */
public class UndergroundGenerator extends SpoutChunkGenerator {
	public UndergroundGenerator() {
		// TODO: populators
		super();
	}

	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
		OctaveGenerator noiseFloor = octaves.get("floor");
		OctaveGenerator noiseCeiling = octaves.get("ceiling");
		OctaveGenerator noiseJitter1 = octaves.get("jitter1");
		OctaveGenerator noiseJitter2 = octaves.get("jitter2");
		OctaveGenerator noiseStalactite = octaves.get("stalactite");
		OctaveGenerator noiseStalagmite = octaves.get("stalagmite");
		OctaveGenerator noisePlatform1 = octaves.get("platform1");
		OctaveGenerator noisePlatform2 = octaves.get("platform2");

		chunkX <<= 4;
		chunkZ <<= 4;

		int stone = world.getEnvironment() == Environment.NETHER ? BlockID.NETHERRACK : BlockID.STONE;
		int height = world.getMaxHeight();

		byte[] buf = start(world, stone);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int min = (int) (Math.abs(noiseFloor.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 3) + 5 + noiseJitter1.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 2 + convertPointyThings(noiseStalagmite, x + chunkX, z + chunkZ, height));
				int max = (int) (height - Math.abs(noiseCeiling.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 3) - 5 + noiseJitter2.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 2 - random.nextInt(5) - convertPointyThings(noiseStalactite, x + chunkX, z + chunkZ, height));

				if (min > 20) {
					min -= random.nextInt(5);
				}

				if (min >= max) {
					set(buf, world, x, 0, z, BlockID.BEDROCK);
					set(buf, world, x, height - 1, z, BlockID.BEDROCK);
					continue;
				}

				for (int y = min; y <= max; y++) {
					set(buf, world, x, y, z, BlockID.AIR);
				}

				int platform = (int) (noisePlatform1.noise(x + chunkX, z + chunkZ, 0.5, 0.5, true) * 20 - 4);
				if (platform > 5) {
					platform -= random.nextInt(3);
				}
				while (platform-- > 0) {
					set(buf, world, x, height / 2 - platform - 1, z, stone);
				}

				platform = (int) (noisePlatform2.noise(x + chunkX, z + chunkZ, 0.5, 0.5, true) * 30 - 6);
				if (platform > 5) {
					platform -= random.nextInt(3);
				}
				while (platform-- > 0) {
					set(buf, world, x, height / 4 - platform - 1, z, stone);
				}

				for (int i = 4; i > 0; i--) {
					if (get(buf, world, x, i, z) == BlockID.AIR) {
						set(buf, world, x, i, z, BlockID.LAVA);
					}
				}
				set(buf, world, x, 0, z, BlockID.BEDROCK);
				set(buf, world, x, height - 1, z, BlockID.BEDROCK);
			}
		}

		return buf;
	}

	private double convertPointyThings(OctaveGenerator noise, int x, int z, int height) {
		return Math.max(noise.noise(x, z, 0.5, 0.5, true) * height * 3 / 2 - height / 2, 0);
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		while (true) {
			int x = random.nextInt(world.getMaxHeight()) - 64;
			int y = world.getMaxHeight() * 3 / 4;
			int z = random.nextInt(world.getMaxHeight()) - 64;

			if (world.getBlockAt(x, y, z).isEmpty()) {
				while (world.getBlockAt(x, y - 1, z).isEmpty() && y > 0) {
					y--;
				}
				return new Location(world, x, y, z);
			}
		}
	}

	@Override
	protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
		Random seed = new Random(world.getSeed());

		OctaveGenerator gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 64.0);
		octaves.put("floor", gen);

		gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 64.0);
		octaves.put("ceiling", gen);

		gen = new SimplexOctaveGenerator(seed, 5);
		gen.setScale(1 / 32.0);
		octaves.put("jitter1", gen);

		gen = new SimplexOctaveGenerator(seed, 5);
		gen.setScale(1 / 32.0);
		octaves.put("jitter2", gen);

		gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 32.0);
		octaves.put("stalactite", gen);

		gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 48.0);
		octaves.put("stalagmite", gen);

		gen = new SimplexOctaveGenerator(seed, 7);
		gen.setScale(1 / 32.0);
		octaves.put("platform1", gen);

		gen = new SimplexOctaveGenerator(seed, 8);
		gen.setScale(1 / 96.0);
		octaves.put("platform2", gen);
	}
}
