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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import org.spout.api.Platform;

import org.spout.api.Spout;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.nbt.TransformTag;
import org.spout.api.io.nbt.UUIDTag;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.util.StringMap;
import org.spout.api.util.sanitation.SafeCast;

import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.CommonFileSystem;
import org.spout.engine.world.SpoutWorld;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;
import org.spout.nbt.util.NBTMapper;

import static org.spout.api.lang.Translation.log;
import org.spout.engine.SpoutServer;
import org.spout.engine.world.SpoutServerWorld;

public class WorldFiles {

	public static final byte WORLD_VERSION = 2;
	
	public static SpoutServerWorld loadWorld(SpoutServer engine, WorldGenerator generator, String name) {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("Unable to load world in client mode");
		}
		File worldDir = new File(CommonFileSystem.WORLDS_DIRECTORY, name);
		
		worldDir.mkdirs();
		
		File worldFile = new File(worldDir, "world.dat");

		SpoutServerWorld world = null;
		
		File itemMapFile = new File(worldDir, "materials.dat");
		BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
		itemStore.load();
		
		StringMap itemMap = new StringMap(engine.getEngineItemMap(), itemStore, 0, Short.MAX_VALUE, name + "ItemMap");
		
		File lightingMapFile = new File(worldDir, "lighting.dat");
		BinaryFileStore lightingStore = new BinaryFileStore(lightingMapFile);
		lightingStore.load();
		
		StringMap lightingMap = new StringMap(engine.getEngineLightingMap(), itemStore, 0, Short.MAX_VALUE, name + "lightingMap");
		
		try {
			InputStream is = new FileInputStream(worldFile);
			NBTInputStream ns = new NBTInputStream(is, false);
			CompoundMap map;
			try {
				CompoundTag tag = (CompoundTag) ns.readTag();
				map = tag.getValue();
			} finally {
				try {
					ns.close();
				} catch (IOException e) {
					log("Cannot close world file");
				}
			}
			log("Loading world [%0]", name);
			world = loadWorldImpl(name, map, generator, itemMap, lightingMap);
		} catch (FileNotFoundException ioe) {
			log("Generating new world named [%0]", name);

			world = new SpoutServerWorld(name, engine, new Random().nextLong(), 0L, generator, UUID.randomUUID(), itemMap, lightingMap);
			world.save();

		} catch (IOException ioe) {
			log("Error reading file for world " + name);
		}
		return world;
	}

	private static SpoutServerWorld loadWorldImpl(String name, CompoundMap map, WorldGenerator generator, StringMap itemMap, StringMap lightingMap) {

		byte version = SafeCast.toByte(NBTMapper.toTagValue(map.get("version")), (byte) -1);

		if (version > WORLD_VERSION) {
			Spout.getLogger().log(Level.SEVERE, "World version " + version + " exceeds maximum allowed value of " + WORLD_VERSION);
			return null;
		} else if (version < WORLD_VERSION) {
			// TODO - Add conversion code here
			Spout.getLogger().log(Level.SEVERE, "Outdated World version " + version);
			return null;
		}
		
		String generatorName = SafeCast.toString(NBTMapper.toTagValue(map.get("generator")), null);
		Long seed = SafeCast.toLong(NBTMapper.toTagValue(map.get("seed")), 0);
		byte[] extraData = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("extra_data")), null);
		Long age = SafeCast.toLong(NBTMapper.toTagValue(map.get("age")), 0);
		UUID uuid = UUIDTag.getValue(map.get("uuid"));
		
		if (!generatorName.equals(generator.getName())) {
			Spout.getLogger().severe("World was saved last with the generator: " + generatorName + " but is being loaded with: " + generator.getName() + " THIS MAY CAUSE WORLD CORRUPTION!");
		}
		
		SpoutServerWorld world = new SpoutServerWorld(name, (SpoutEngine) Spout.getEngine(), seed, age, generator, uuid, itemMap, lightingMap);
		
		Transform t = TransformTag.getValue(world, map.get("spawn_position"));
		
		world.setSpawnPoint(t);
		
		ManagedHashMap dataMap = world.getDatatable().getBaseMap();
		dataMap.clear();
		try {
			dataMap.deserialize(extraData);
		} catch (IOException e) {
			Spout.getLogger().severe("Could not deserialize datatable for world: " + name);
		}
		
		return world;
	}
	
	public static void saveWorld(SpoutServerWorld world) {
		
		File worldDir = new File(CommonFileSystem.WORLDS_DIRECTORY, world.getName());
		
		worldDir.mkdirs();
		
		File worldFile = new File(worldDir, "world.dat");

		world.getItemMap().save();
		
		world.getLightingMap().save();
		
		CompoundMap map = saveWorldImpl(world);
		
		NBTOutputStream ns = null;
		try {
			OutputStream is = new FileOutputStream(worldFile);
			ns = new NBTOutputStream(is, false);
			ns.writeTag(new CompoundTag("world_" + world.getName(), map));
		} catch (IOException ioe) {
			log("Error writing file for world " + world.getName());
		} finally {
			if (ns != null) {
				try {
					ns.close();
				} catch (IOException ignore) { }
			}
		}
	}
	
	private static CompoundMap saveWorldImpl(SpoutServerWorld world) {
		
		CompoundMap map = new CompoundMap();
		
		map.put(new ByteTag("version", WORLD_VERSION));
		map.put(new StringTag("generator", world.getGenerator().getName()));
		map.put(new LongTag("seed", world.getSeed()));
		map.put(new ByteArrayTag("extra_data", world.getDatatable().serialize()));
		map.put(new LongTag("age", world.getAge()));
		map.put(new UUIDTag("uuid", world.getUID()));
		map.put(new TransformTag("spawn_position", world.getSpawnPoint()));
		
		return map;
		
	}
	
}
