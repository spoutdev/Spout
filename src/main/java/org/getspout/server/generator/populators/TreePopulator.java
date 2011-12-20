package org.getspout.server.generator.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import org.getspout.server.generator.populators.trees.GenericTreeGenerator;
import org.getspout.server.generator.populators.trees.NormalTree;

/**
 * BlockPopulator that adds trees based on the biome.
 */
public class TreePopulator extends BlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk source) {
		int centerX = (source.getX() << 4) + random.nextInt(16);
		int centerZ = (source.getZ() << 4) + random.nextInt(16);

		byte data = 0;
		int chance = 0;
		int height = 4 + random.nextInt(3);
		int multiplier = 1;

		if (random.nextBoolean()) {
			data = 2;
			height = 5 + random.nextInt(3);
		}

		switch (world.getBlockAt(centerX, 0, centerZ).getBiome()) {
			case FOREST:
				chance = 160;
				multiplier = 10;
				break;
			case PLAINS:
				chance = 40;
				break;
			case RAINFOREST:
				chance = 160;
				multiplier = 10;
				break;
			case SAVANNA:
				chance = 20;
				break;
			case SEASONAL_FOREST:
				chance = 140;
				multiplier = 8;
				break;
			case SHRUBLAND:
				chance = 60;
				break;
			case SWAMPLAND:
				chance = 120;
				break;
			case TAIGA:
				// Redwood
				chance = 120;
				data = 1;
				height = 8 + random.nextInt(3);
				multiplier = 3;
				break;
			case TUNDRA:
				chance = 5;
				data = 1;
				height = 7 + random.nextInt(3);
				break;
			case SKY:
				chance = 1;
				break;
			case DESERT:
			case HELL:
			case ICE_DESERT:
				return;
		}

		for (int i = 0; i < multiplier; i++) {
			centerX = (source.getX() << 4) + random.nextInt(16);
			centerZ = (source.getZ() << 4) + random.nextInt(16);
			if (random.nextInt(300) < chance) {
				if (data == 0) {
					GenericTreeGenerator treegen = new NormalTree();
					treegen.generate(random, centerX, centerZ, height, world);
				}
			}
		}
	}
}
