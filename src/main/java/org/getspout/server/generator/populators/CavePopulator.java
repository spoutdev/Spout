package org.getspout.server.generator.populators;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 * BlockPopulator for snake-based caves.
 */
public class CavePopulator extends BlockPopulator {
	@Override
	public void populate(final World world, final Random random, Chunk source) {
		if (random.nextInt(100) < 10) {
			final int x = 4 + random.nextInt(8) + source.getX() * 16;
			final int z = 4 + random.nextInt(8) + source.getZ() * 16;
			int maxY = world.getHighestBlockYAt(x, z);
			if (maxY < 16) {
				maxY = 32;
			}

			final int y = random.nextInt(maxY);
			new Thread() {

				@Override
				public void run() {
					startSnake(world, random, x, y, z);

					if (random.nextInt(16) > 5) {
						if (y > 36) {
							startSnake(world, random, x, y / 2, z);
						} else if (y < 24) {
							startSnake(world, random, x, y * 2, z);
						}
					}
				}
			}.start();
		}
	}

	private static Set<Location> startSnake(World world, Random random, int blockX, int blockY, int blockZ) {
		Set<Location> snakeBlocks = new HashSet<Location>();

		int airHits = 0;
		while (true) {
			if (airHits > 2000) {
				break;
			}

			if (random.nextInt(20) == 0) {
				blockY++;
			} else if (world.getBlockTypeIdAt(blockX, blockY + 2, blockZ) == 0) {
				blockY += 2;
			} else if (world.getBlockTypeIdAt(blockX + 2, blockY, blockZ) == 0) {
				blockX++;
			} else if (world.getBlockTypeIdAt(blockX - 2, blockY, blockZ) == 0) {
				blockX--;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 2) == 0) {
				blockZ++;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 2) == 0) {
				blockZ--;
			} else if (world.getBlockTypeIdAt(blockX + 1, blockY, blockZ) == 0) {
				blockX++;
			} else if (world.getBlockTypeIdAt(blockX - 1, blockY, blockZ) == 0) {
				blockX--;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 1) == 0) {
				blockZ++;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 1) == 0) {
				blockZ--;
			} else if (random.nextBoolean()) {
				if (random.nextBoolean()) {
					blockX++;
				} else {
					blockZ++;
				}
			} else {
				if (random.nextBoolean()) {
					blockX--;
				} else {
					blockZ--;
				}
			}

			if (world.getBlockTypeIdAt(blockX, blockY, blockZ) != 0) {
				int radius = 1 + random.nextInt(3);
				int radius2 = radius * radius + 1;
				for (int x = -radius; x <= radius; x++) {
					for (int y = -radius; y <= radius; y++) {
						for (int z = -radius; z <= radius; z++) {
							if (x * x + y * y + z * z <= radius2 && y >= 0 && y < world.getMaxHeight()) {
								if (world.getBlockTypeIdAt(blockX + x, blockY + y, blockZ + z) == 0) {
									airHits++;
								} else {
									snakeBlocks.add(new Location(world, blockX + x, blockY + y, blockZ + z));
								}
							}
						}
					}
				}
			} else {
				airHits++;
			}
		}

		return snakeBlocks;
	}
}
