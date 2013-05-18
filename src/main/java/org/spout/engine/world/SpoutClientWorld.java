/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.world;

import java.util.UUID;

import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.ClientWorld;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.util.StringMap;
import org.spout.engine.SpoutEngine;

/**
 * A dummy world used for the client
 */
public class SpoutClientWorld extends SpoutWorld implements ClientWorld {
	
	public SpoutClientWorld(String name, UUID uid, SpoutEngine engine, StringMap itemMap, StringMap lightingMap) {
		super(name, engine, 0, 0, null, uid, itemMap, lightingMap);
	}

	@Override
	public void addChunk(ChunkSnapshot c) {
		addChunk(c.getX(), c.getY(), c.getZ(), c.getBlockIds(), c.getBlockData(), c.getBiomeManager());
	}

	@Override
	public void addChunk(int x, int y, int z, short[] blockIds, short[] blockData, BiomeManager biomes) {
		getRegionFromBlock(x, y, z, LoadOption.LOAD_GEN).addChunk(x, y, z, blockIds, blockData, biomes);
	}
}
