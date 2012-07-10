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

import java.util.Random;
import org.spout.api.generator.WorldGeneratorObject;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;

/**
 *
 */
public class WorldGeneratorObjectDecorator extends Decorator {
	private final int probability;
	private final WGOFactory factory;

	public WorldGeneratorObjectDecorator(int probability, WGOFactory factory) {
		this.probability = probability;
		this.factory = factory;
	}

	@Override
	public void populate(Chunk chunk, Random random) {
		if (random.nextInt(probability) == 0) {
			final World world = chunk.getWorld();
			final int worldX = chunk.getBlockX() + random.nextInt(16);
			final int worldY = chunk.getBlockY() + random.nextInt(16);
			final int worldZ = chunk.getBlockZ() + random.nextInt(16);
			WorldGeneratorObject dungeon = factory.createObject(random);
			if (dungeon.canPlaceObject(world, worldX, worldY, worldZ)) {
				dungeon.placeObject(world, worldX, worldY, worldZ);
			}
		}
	}

	public interface WGOFactory {
		WorldGeneratorObject createObject(Random random);
	}
}
