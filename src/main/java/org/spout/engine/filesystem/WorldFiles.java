/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.type.ControllerRegistry;
import org.spout.api.entity.type.ControllerType;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.generator.biome.EmptyBiomeManager;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringMap;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.api.util.sanitation.StringSanitizer;

import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.world.FilteredChunk;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class WorldFiles {
	private static final byte WORLD_VERSION = 1;
	private static final byte ENTITY_VERSION = 1;
	private static final byte CHUNK_VERSION = 1;

	public static void saveWorldData(SpoutWorld world) {
		File worldData = new File(world.getDirectory(), "world.dat");

		String generatorName = world.getGenerator().getName();
		if (!StringSanitizer.isAlphaNumericUnderscore(generatorName)) {
			generatorName = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("Generator name " + generatorName + " is not valid, using " + generatorName + " instead");
		}

		//Save the world item map
		world.getItemMap().save();

		CompoundMap worldTags = new CompoundMap();
		worldTags.put(new ByteTag("version", (byte) WORLD_VERSION));
		worldTags.put(new LongTag("seed", world.getSeed()));
		worldTags.put(new StringTag("generator", generatorName));
		worldTags.put(new LongTag("UUID_lsb", world.getUID().getLeastSignificantBits()));
		worldTags.put(new LongTag("UUID_msb", world.getUID().getMostSignificantBits()));
		worldTags.put(new ByteArrayTag("extraData", ((DataMap) world.getDataMap()).getRawMap().compress()));
		worldTags.put(new LongTag("age", world.getAge()));

		CompoundTag worldTag = new CompoundTag(world.getName(), worldTags);

		NBTOutputStream os = null;
		try {
			os = new NBTOutputStream(new DataOutputStream(new FileOutputStream(worldData)), false);
			os.writeTag(worldTag);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving world data for " + world.toString(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	public static SpoutWorld loadWorldData(Engine engine, String name, WorldGenerator generator, StringMap global) {
		SpoutWorld world = null;

		String generatorName = generator.getName();
		if (!StringSanitizer.isAlphaNumericUnderscore(generatorName)) {
			generatorName = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("Generator name " + generatorName + " is not valid, using " + generatorName + " instead");
		}

		//Load the world specific item map
		File itemMapFile = new File(new File(FileSystem.WORLDS_DIRECTORY, name), "materials.dat");
		BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
		if (itemMapFile.exists()) {
			itemStore.load();
		}
		StringMap itemMap = new StringMap(global, itemStore, 0, Short.MAX_VALUE);

		File worldData = new File(new File(FileSystem.WORLDS_DIRECTORY, name), "world.dat");

		if (worldData.exists()) {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(new DataInputStream(new FileInputStream(worldData)), false);
				CompoundTag dataTag = (CompoundTag) is.readTag();
				CompoundMap map = dataTag.getValue();
				GenericDatatableMap extraData = new GenericDatatableMap();

				@SuppressWarnings("unused")
				byte version = SafeCast.toByte(toTagValue(map.get("version")), (byte) 0);
				long seed = SafeCast.toLong(toTagValue(map.get("seed")), new Random().nextLong());
				String savedGeneratorName = SafeCast.toString(toTagValue(map.get("generator")), "");

				long lsb = SafeCast.toLong(toTagValue(map.get("UUID_lsb")), new Random().nextLong());
				long msb = SafeCast.toLong(toTagValue(map.get("UUID_msb")), new Random().nextLong());

				byte[] extraDataBytes = SafeCast.toByteArray(toTagValue(map.get("extraData")), new byte[0]);
				extraData.decompress(extraDataBytes);

				if (!savedGeneratorName.equals(generatorName)) {
					Spout.getEngine().getLogger().severe("World was saved last with the generator: " + savedGeneratorName + " but is being loaded with: " + generatorName + " MAY CAUSE WORLD CORRUPTION!");
				}

				long age = SafeCast.toLong(toTagValue(map.get("age")), 0L);

				world = new SpoutWorld(name, engine, seed, age, generator, new UUID(msb, lsb), itemMap, extraData);
			} catch (IOException e) {
				Spout.getLogger().log(Level.SEVERE, "Error saving load data for " + name, e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ignore) {
					}
				}
			}
		}

		return world;
	}

	public static void saveChunk(SpoutChunk c, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, DatatableMap extraData, OutputStream dos) {
		CompoundMap chunkTags = new CompoundMap();

		//Switch block ids from engine material ids to world specific ids
		SpoutWorld world = c.getWorld();
		StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
		StringMap itemMap = world.getItemMap();
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = (short) global.convertTo(itemMap, blocks[i]);
		}

		chunkTags.put(new ByteTag("version", CHUNK_VERSION));
		chunkTags.put(new ByteTag("format", (byte) 0));
		chunkTags.put(new IntTag("x", c.getX()));
		chunkTags.put(new IntTag("y", c.getY()));
		chunkTags.put(new IntTag("z", c.getZ()));
		chunkTags.put(new ByteTag("populated", c.isPopulated()));
		chunkTags.put(new ShortArrayTag("blocks", blocks));
		chunkTags.put(new ShortArrayTag("data", data));
		chunkTags.put(new ByteArrayTag("skyLight", skyLight));
		chunkTags.put(new ByteArrayTag("blockLight", blockLight));
		chunkTags.put(new CompoundTag("entities", saveEntities(c)));
		chunkTags.put(saveDynamicUpdates(c));

		byte[] biomes = c.getBiomeManager().serialize();
		if (biomes != null) {
			chunkTags.put(new StringTag("biomeManager", c.getBiomeManager().getClass().getCanonicalName()));
			chunkTags.put(new ByteArrayTag("biomes", biomes));
		}

		chunkTags.put(new ByteArrayTag("extraData", extraData.compress()));

		CompoundTag chunkCompound = new CompoundTag("chunk", chunkTags);

		NBTOutputStream os = null;
		try {
			os = new NBTOutputStream(dos, false);
			os.writeTag(chunkCompound);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving chunk " + c.toString(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	public static SpoutChunk loadChunk(SpoutRegion r, int x, int y, int z, InputStream dis, ChunkDataForRegion dataForRegion) {
		SpoutChunk chunk = null;
		NBTInputStream is = null;

		try {
			if (dis == null) {
				//The inputstream is null because no chunk data exists
				return chunk;
			}

			is = new NBTInputStream(dis, false);
			CompoundTag chunkTag = (CompoundTag) is.readTag();
			CompoundMap map = chunkTag.getValue();
			int cx = (r.getX() << Region.REGION_SIZE_BITS) + x;
			int cy = (r.getY() << Region.REGION_SIZE_BITS) + y;
			int cz = (r.getZ() << Region.REGION_SIZE_BITS) + z;

			boolean populated = SafeCast.toGeneric(map.get("populated"), new ByteTag("", false), ByteTag.class).getBooleanValue();
			short[] blocks = SafeCast.toShortArray(toTagValue(map.get("blocks")), null);
			short[] data = SafeCast.toShortArray(toTagValue(map.get("data")), null);
			byte[] skyLight = SafeCast.toByteArray(toTagValue(map.get("skyLight")), null);
			byte[] blockLight = SafeCast.toByteArray(toTagValue(map.get("blockLight")), null);
			byte[] extraData = SafeCast.toByteArray(toTagValue(map.get("extraData")), null);

			BiomeManager manager = null;
			if (map.containsKey("biomes")) {
				try {
					String biomeManagerClass = (String) map.get("biomeManager").getValue();
					byte[] biomes = (byte[]) map.get("biomes").getValue();
					@SuppressWarnings("unchecked")
					Class<? extends BiomeManager> clazz = (Class<? extends BiomeManager>) Class.forName(biomeManagerClass);
					Class<?>[] params = {int.class, int.class, int.class};
					manager = clazz.getConstructor(params).newInstance(cx, cy, cz);
					manager.deserialize(biomes);
				} catch (Exception e) {
					Spout.getLogger().severe("Failed to read biome data for chunk");
					e.printStackTrace();
				}
			}
			if (manager == null) {
				manager = new EmptyBiomeManager(cx, cy, cz);
			}

			//Convert world block ids to engine material ids 
			SpoutWorld world = r.getWorld();
			StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
			StringMap itemMap = world.getItemMap();
			for (int i = 0; i < blocks.length; i++) {
				blocks[i] = (short) itemMap.convertTo(global, blocks[i]);
			}

			DatatableMap extraDataMap = new GenericDatatableMap();
			extraDataMap.decompress(extraData);

			chunk = new FilteredChunk(r.getWorld(), r, cx, cy, cz, populated, blocks, data, skyLight, blockLight, manager, extraDataMap);

			CompoundMap entityMap = SafeCast.toGeneric(toTagValue(map.get("entities")), null, CompoundMap.class);
			loadEntities(r, entityMap, dataForRegion.loadedEntities);

			List<CompoundTag> updateList = SafeCast.toGeneric(toTagValue(map.get("dynamic_updates")), null, List.class);
			loadDynamicUpdates(updateList, dataForRegion.loadedUpdates);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
		return chunk;
	}

	private static void loadEntities(SpoutRegion r, CompoundMap map, List<SpoutEntity> loadedEntities) {
		if (r != null && map != null) {
			for (Tag tag : map) {
				SpoutEntity e = loadEntity(r, (CompoundTag) tag);
				if (e != null) {
					loadedEntities.add(e);
				}
			}
		}
	}

	private static CompoundMap saveEntities(SpoutChunk c) {
		Set<Entity> entities = c.getLiveEntities();
		CompoundMap tagMap = new CompoundMap();

		for (Entity e : entities) {
			Tag tag = saveEntity((SpoutEntity) e);
			if (tag != null) {
				tagMap.put(tag);
			}
		}

		return tagMap;
	}

	private static SpoutEntity loadEntity(SpoutRegion r, CompoundTag tag) {
		CompoundMap map = tag.getValue();

		@SuppressWarnings("unused")
		byte version = SafeCast.toByte(toTagValue(map.get("version")), (byte) 0);
		String name = SafeCast.toString(toTagValue(map.get("controller")), "");

		ControllerType type = ControllerRegistry.get(name);
		if (type == null) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "No controller type found matching: " + name);
		} else if (type.canCreateController()) {

			//Read entity
			Float pX = SafeCast.toFloat(toTagValue(map.get("posX")), Float.MAX_VALUE);
			Float pY = SafeCast.toFloat(toTagValue(map.get("posY")), Float.MAX_VALUE);
			Float pZ = SafeCast.toFloat(toTagValue(map.get("posZ")), Float.MAX_VALUE);

			if (pX == Float.MAX_VALUE || pY == Float.MAX_VALUE || pZ == Float.MAX_VALUE) {
				return null;
			}

			float sX = SafeCast.toFloat(toTagValue(map.get("scaleX")), 1.0F);
			float sY = SafeCast.toFloat(toTagValue(map.get("scaleY")), 1.0F);
			float sZ = SafeCast.toFloat(toTagValue(map.get("scaleZ")), 1.0F);

			float qX = SafeCast.toFloat(toTagValue(map.get("quatX")), 0.0F);
			float qY = SafeCast.toFloat(toTagValue(map.get("quatY")), 0.0F);
			float qZ = SafeCast.toFloat(toTagValue(map.get("quatZ")), 0.0F);
			float qW = SafeCast.toFloat(toTagValue(map.get("quatW")), 1.0F);

			long msb = SafeCast.toLong(toTagValue(map.get("UUID_msb")), new Random().nextLong());
			long lsb = SafeCast.toLong(toTagValue(map.get("UUID_lsb")), new Random().nextLong());
			UUID uid = new UUID(msb, lsb);

			int view = SafeCast.toInt(toTagValue(map.get("view")), 0);
			boolean observer = SafeCast.toGeneric(toTagValue(map.get("observer")), new ByteTag("", (byte) 0), ByteTag.class).getBooleanValue();

			//Setup controller
			Controller controller = type.createController();
			try {
				boolean controllerDataExists = SafeCast.toGeneric(toTagValue(map.get("controller_data_exists")), new ByteTag("", (byte) 0), ByteTag.class).getBooleanValue();

				if (controllerDataExists) {
					byte[] data = SafeCast.toByteArray(toTagValue(map.get("controller_data")), new byte[0]);
					DatatableMap dataMap = ((DataMap) controller.data()).getRawMap();
					dataMap.decompress(data);
				}
			} catch (Exception error) {
				Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to load the controller for the type: " + type.getName(), error);
			}

			//Setup entity
			Transform t = new Transform(new Point(r != null ? r.getWorld() : null, pX, pY, pZ), new Quaternion(qX, qY, qZ, qW, false), new Vector3(sX, sY, sZ));
			SpoutEntity e = new SpoutEntity((SpoutEngine) Spout.getEngine(), t, controller, view, uid, false);
			e.setObserver(observer);

			return e;
		} else {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to create controller for the type: " + type.getName());
		}

		return null;
	}

	private static Tag saveEntity(SpoutEntity e) {
		if (!e.getController().isSavable() || e.isDead()) {
			return null;
		}
		CompoundMap map = new CompoundMap();

		map.put(new ByteTag("version", ENTITY_VERSION));
		map.put(new StringTag("controller", e.getController().getType().getName()));

		//Write entity
		map.put(new FloatTag("posX", e.getPosition().getX()));
		map.put(new FloatTag("posY", e.getPosition().getY()));
		map.put(new FloatTag("posZ", e.getPosition().getZ()));

		map.put(new FloatTag("scaleX", e.getScale().getX()));
		map.put(new FloatTag("scaleY", e.getScale().getY()));
		map.put(new FloatTag("scaleZ", e.getScale().getZ()));

		map.put(new FloatTag("quatX", e.getRotation().getX()));
		map.put(new FloatTag("quatY", e.getRotation().getY()));
		map.put(new FloatTag("quatZ", e.getRotation().getZ()));
		map.put(new FloatTag("quatW", e.getRotation().getW()));

		map.put(new LongTag("UUID_msb", e.getUID().getMostSignificantBits()));
		map.put(new LongTag("UUID_lsb", e.getUID().getLeastSignificantBits()));

		map.put(new IntTag("view", e.getViewDistance()));
		map.put(new ByteTag("observer", e.isObserverLive()));

		//Write controller
		try {
			//Call onSave
			e.getController().onSave();
			//Serialize data
			DatatableMap dataMap = ((DataMap) e.getController().data()).getRawMap();
			if (!dataMap.isEmpty()) {
				map.put(new ByteTag("controller_data_exists", true));
				map.put(new ByteArrayTag("controller_data", dataMap.compress()));
			} else {
				map.put(new ByteTag("controller_data_exists", false));
			}
		} catch (Exception error) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to write the controller information for the type: " + e.getController().getType(), error);
		}

		CompoundTag tag = new CompoundTag("entity_" + e.getId(), map);

		return tag;
	}

	private static ListTag<CompoundTag> saveDynamicUpdates(SpoutChunk c) {
		List<DynamicBlockUpdate> updates = ((SpoutRegion) c.getRegion()).getDynamicBlockUpdates(c);

		List<CompoundTag> list = new ArrayList<CompoundTag>(updates.size());

		for (DynamicBlockUpdate update : updates) {
			CompoundTag tag = saveDynamicUpdate(update);
			if (tag != null) {
				list.add(tag);
			}
		}

		return new ListTag<CompoundTag>("dynamic_updates", CompoundTag.class, list);
	}

	private static CompoundTag saveDynamicUpdate(DynamicBlockUpdate update) {
		CompoundMap map = new CompoundMap();

		map.put(new IntTag("packed", update.getPacked()));
		map.put(new LongTag("nextUpdate", update.getNextUpdate()));
		map.put(new LongTag("lastUpdate", update.getLastUpdate()));

		return new CompoundTag("update", map);
	}

	private static void loadDynamicUpdates(List<CompoundTag> list, List<DynamicBlockUpdate> loadedUpdates) {
		if (list != null) {
			for (CompoundTag t : list) {
				DynamicBlockUpdate update = loadDynamicUpdate(t);
				if (update != null) {
					loadedUpdates.add(update);
				}
			}
		}
	}

	private static DynamicBlockUpdate loadDynamicUpdate(CompoundTag t) {
		CompoundMap map = t.getValue();
		int packed = SafeCast.toInt(toTagValue(map.get("packed")), -1);
		long nextUpdate = SafeCast.toLong(toTagValue(map.get("nextUpdate")), -1L);
		long lastUpdate = SafeCast.toLong(toTagValue(map.get("lastUpdate")), -1L);
		if (packed < 0 || nextUpdate < 0) {
			return null;
		} else {
			return new DynamicBlockUpdate(packed, nextUpdate, lastUpdate);
		}
	}

	private static Object toTagValue(Tag t) {
		if (t == null) {
			return null;
		} else {
			return t.getValue();
		}
	}
}
