/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.filesystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringMap;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.engine.SpoutEngine;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.holder.BasicTagField;
import org.spout.nbt.holder.FieldHolder;
import org.spout.nbt.holder.FieldValue;
import org.spout.engine.filesystem.fields.TransformField;
import org.spout.engine.filesystem.fields.UUIDField;
import org.spout.engine.world.SpoutWorld;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.LongTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.holder.ListField;

/**
 * Represents a world data file
 */
public class WorldData extends FieldHolder {
	private final String name;
	private final FieldValue<String> generatorName = FieldValue.from("generator", new BasicTagField<String>(StringTag.class));
	private final FieldValue<Byte> version = FieldValue.from("version", new BasicTagField<Byte>(ByteTag.class), WorldFiles.WORLD_VERSION);
	private final FieldValue<Long> seed = FieldValue.from("seed", new BasicTagField<Long>(LongTag.class));
	private final FieldValue<UUID> uuid = FieldValue.from("uuid", new UUIDField(), UUID.randomUUID());
	private final FieldValue<byte[]> worldDatatable = FieldValue.from("extra_data", new BasicTagField<byte[]>(ByteArrayTag.class));
	private final FieldValue<Long> age = FieldValue.from("age", new BasicTagField<Long>(LongTag.class));
	private final FieldValue<TransformField.Holder> spawnPosition = FieldValue.from("spawn_position", TransformField.INSTANCE, new TransformField.Holder());
	private VersionedData versionData = new Version2Data();
	private StringMap itemMap;

	private static interface VersionedData {
		@SuppressWarnings("unused")
		public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
		public UUID getUID();
		public Transform getSpawnPosition(SpoutWorld world);

		public FieldValue<?>[] addFields();
	}

	private static class Versionlte1Data implements VersionedData {
		private static final Random RAND = new Random();
		private final FieldValue<Long> uuidMSB = FieldValue.from("UUID_msb", new BasicTagField<Long>(LongTag.class), RAND.nextLong());
		private final FieldValue<Long> uuidLSB = FieldValue.from("UUID_lsb", new BasicTagField<Long>(LongTag.class), RAND.nextLong());
		private final FieldValue<List<Float>> spawnPosition = FieldValue.from("spawn_position", new ListField<Float>(new BasicTagField<Float>(FloatTag.class)));

		public UUID getUID() {
			return new UUID(uuidMSB.get(), uuidLSB.get());
		}

		public Transform getSpawnPosition(SpoutWorld world) {
			if (spawnPosition.get() == null) {
				return new Transform(new Point(world, 1, 85, 1), Quaternion.IDENTITY, Vector3.ONE);
			}

			//Position
			float px = spawnPosition.get().get(0);
			float py = spawnPosition.get().get(1);
			float pz = spawnPosition.get().get(2);

			//Rotation
			float rw = spawnPosition.get().get(3);
			float rx = spawnPosition.get().get(4);
			float ry = spawnPosition.get().get(5);
			float rz = spawnPosition.get().get(6);

			//Scale
			float sx = spawnPosition.get().get(7);
			float sy = spawnPosition.get().get(8);
			float sz = spawnPosition.get().get(9);

			return new Transform(new Point(world, px, py, pz), new Quaternion(rx, ry, rz, rw, true), new Vector3(sx, sy, sz));
		}

		public FieldValue<?>[] addFields() {
			return new FieldValue<?>[] {uuidMSB, uuidLSB, spawnPosition};
		}
	}

	private class Version2Data implements VersionedData {

		public UUID getUID() {
			return uuid.get();
		}

		public Transform getSpawnPosition(SpoutWorld world) {
			return spawnPosition.get().toTransform(world);
		}

		public FieldValue<?>[] addFields() {
			return new FieldValue<?>[] {uuid, spawnPosition};
		}
	}

	public WorldData(String name) {
		addFields(generatorName, version, seed, worldDatatable, age);
		this.name = name;
	}

	public WorldData(SpoutWorld world) {
		this(world.getName());
		addFields(uuid, spawnPosition);
		version.set(WorldFiles.WORLD_VERSION);

		String generatorName = world.getGenerator().getName();
		if (!StringSanitizer.isAlphaNumericUnderscore(generatorName)) {
			String oldName = generatorName;
			generatorName = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("Generator name " + oldName + " is not valid, using " + generatorName + " instead");
		}
		this.generatorName.set(generatorName);
		seed.set(world.getSeed());
		uuid.set(world.getUID());
		worldDatatable.set(world.getComponentHolder().getData().serialize());
		age.set(world.getAge());
		spawnPosition.set(new TransformField.Holder(world.getSpawnPoint()));
		itemMap = world.getItemMap();
	}

	@Override
	public CompoundMap save() {
		CompoundMap map = super.save();
		itemMap.save();
		return map;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		switch (version.get()) {
			case 0:
			case 1:
				versionData = new Versionlte1Data();
				break;
			case 2:
				versionData = new Version2Data();
				break;
			default:
				throw new IllegalArgumentException("Unknown version: " + version.get());
		}
		addFields(versionData.addFields());
		super.load(tag);
	}

	public SpoutWorld toWorld(WorldGenerator generator, StringMap global) {
		//Load the world specific item map
		File itemMapFile = new File(getFolderForWorld(), "materials.dat");
		BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
		if (itemMapFile.exists()) {
			itemStore.load();
		}
		itemMap = new StringMap(global, itemStore, 0, Short.MAX_VALUE, name + "ItemMap");

		if (!generatorName.get().equals(generator.getName())) {
			Spout.getLogger().severe("World was saved last with the generator: " + generatorName.get() + " but is being loaded with: " + generator.getName() + " MAY CAUSE WORLD CORRUPTION!");
		}
		SpoutWorld world = new SpoutWorld(name, (SpoutEngine) Spout.getEngine(), seed.get(), age.get(), generator, versionData.getUID(), itemMap);
		world.setSpawnPoint(versionData.getSpawnPosition(world));
		ManagedHashMap dataMap = world.getComponentHolder().getData().getBaseMap();
		dataMap.clear();
		try {
			dataMap.deserialize(worldDatatable.get());
		} catch (IOException e) {
			Spout.getLogger().severe("Could not deserialize datatable for world: " + name);
		}
		return world;
	}

	public void saveToFile() {
		try {
			save(new File(getFolderForWorld(), "world.dat"), false);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving data for world " + name, e);
		}
	}

	public static WorldData loadForWorld(String name) {
		WorldData ret = new WorldData(name);
		try {
			File worldDataFile = new File(ret.getFolderForWorld(), "world.dat");
			if (!worldDataFile.exists()) {
				return null;
			}
			ret.load(worldDataFile, false);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error loading data for world " + name, e);
		}
		return ret;
	}

	public File getFolderForWorld() {
		return new File(SharedFileSystem.getWorldsDirectory(), name);
	}
}
