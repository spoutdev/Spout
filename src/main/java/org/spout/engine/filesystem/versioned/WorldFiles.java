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
package org.spout.engine.filesystem.versioned;

import static org.spout.api.lang.Translation.log;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import org.spout.api.generator.WorldGenerator;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.util.StringMap;
import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.SharedFileSystem;
import org.spout.engine.filesystem.WorldData;
import org.spout.engine.world.SpoutWorld;


public class WorldFiles {

	public static final byte WORLD_VERSION = 2;
	
	public static SpoutWorld loadWorld(SpoutEngine engine, WorldGenerator generator, String name) {
		WorldData worldData = WorldData.loadForWorld(name);
		SpoutWorld world;
		if (worldData == null) {
			log("Generating new world named [%0]", name);

			File itemMapFile = new File(new File(SharedFileSystem.getWorldsDirectory(), name), "materials.dat");
			BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
			StringMap itemMap = new StringMap(engine.getEngineItemMap(), itemStore, 0, Short.MAX_VALUE, name + "ItemMap");

			world = new SpoutWorld(name, engine, new Random().nextLong(), 0L, generator, UUID.randomUUID(), itemMap);
			world.save();
		} else {
			log("Loading world [%0]", name);
			world = worldData.toWorld(generator, engine.getEngineItemMap());
		}
		return world;
	}
	
}
