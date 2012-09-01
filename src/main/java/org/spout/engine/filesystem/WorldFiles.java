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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.spout.api.Spout;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Controller;
import org.spout.api.entity.EntitySnapshot;
import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.entity.controller.PlayerController;
import org.spout.api.entity.controller.type.ControllerRegistry;
import org.spout.api.entity.controller.type.ControllerType;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.generator.biome.EmptyBiomeManager;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.NBTMapper;
import org.spout.api.util.StringMap;
import org.spout.api.util.hashing.ByteTripleHashed;
import org.spout.api.util.hashing.SignedTenBitTripleHashed;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.world.FilteredChunk;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutChunk.PopulationState;
import org.spout.engine.world.SpoutChunkSnapshot;
import org.spout.engine.world.SpoutColumn;
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
	private static final TypeChecker<List<? extends FloatTag>> checkerListFloatTag = TypeChecker.tList(FloatTag.class);
	private static final TypeChecker<List<? extends CompoundTag>> checkerListCompoundTag = TypeChecker.tList(CompoundTag.class);
	private static final byte WORLD_VERSION = 2;
	private static final byte ENTITY_VERSION = 1;
	private static final byte CHUNK_VERSION = 1;
	private static final int COLUMN_VERSION = 4;
	
	public static boolean savePlayerData(SpoutPlayer player) {
		File playerDir = new File(Spout.getEngine().getDataFolder().toString(), "players");
		//Save data to temp file first
		String fileName = player.getName() + ".dat";
		String tempName = fileName + ".temp";
		File playerData = new File(playerDir, tempName);
		if (!playerData.exists()) {
			try {
				playerData.createNewFile();
			} catch (Exception e) {
				Spout.getLogger().log(Level.SEVERE, "Error creating player data for " + player.getName(), e);
			}
		}
		PlayerSnapshot snapshot = new PlayerSnapshot(player);
		CompoundTag playerTag = saveEntity(snapshot);
		NBTOutputStream os = null;
		try {
			os = new NBTOutputStream(new DataOutputStream(new FileOutputStream(playerData)), false);
			os.writeTag(playerTag);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving player data for " + player.getName(), e);
			playerData.delete();
			return false;
		} finally {
			if (os != null) {
				try {
				  os.close();
				} catch (IOException ignore) { }
			}
		}
		try {
			//Move the temp data to final location
			File finalData = new File(playerDir, fileName);
			if (finalData.exists()) {
				finalData.delete();
			}
			FileUtils.moveFile(playerData, finalData);
			return true;
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving player data for " + player.getName(), e);
			playerData.delete();
			return false;
		}
	}

	/**
	 * Loads player data for the player, if it exists
	 * 
	 * Returns null on failure or if the data could not be loaded.
	 * If an exception is thrown or the player data is not in a valid format
	 * it will be backed up and new player data will be created for the player
	 * @param name
	 * @param playerSession
	 * @return player, or null if it could not be loaded
	 */
	public static SpoutPlayer loadPlayerData(String name, SpoutSession<?> playerSession) {
		File playerDir = new File(Spout.getEngine().getDataFolder().toString(), "players");
		String fileName = name + ".dat";
		File playerData = new File(playerDir, fileName);
		if (playerData.exists()) {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(new DataInputStream(new FileInputStream(playerData)), false);
				CompoundTag dataTag = (CompoundTag) is.readTag();
				World world = Spout.getEngine().getWorld(dataTag.getName());
				return (SpoutPlayer)loadEntity(world, dataTag, name, playerSession);
			} catch (Exception e) {
				Spout.getLogger().log(Level.SEVERE, "Error loading player data for " + name, e);
				
				//Back up the corrupt data, so new data can be saved
				//Back up the file with a unique name, based off the current system time
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = formatter.format(new Date(System.currentTimeMillis()));
				File backup = new File(playerDir, fileName + "_" + time + ".bak");
				if (!playerData.renameTo(backup)) {
					Spout.getLogger().log(Level.SEVERE, "Failed to back up corrupt player data " + name);
				} else {
					Spout.getLogger().log(Level.WARNING, "Successfully backed up corrupt player data for " + name);
				}
			} finally {
				try {
					is.close();
				} catch (IOException ignore) { }
			}
		}
		return null;
	}
	
	public static void saveWorldData(SpoutWorld world) {
		File worldData = new File(world.getDirectory(), "world.dat");

		String generatorName = world.getGenerator().getName();
		if (!StringSanitizer.isAlphaNumericUnderscore(generatorName)) {
			String oldName = generatorName;
			generatorName = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("Generator name " + oldName + " is not valid, using " + generatorName + " instead");
		}

		//Save the world item map
		world.getItemMap().save();

		CompoundMap worldTags = new CompoundMap();
		//World Version 1
		worldTags.put(new ByteTag("version", WORLD_VERSION));
		worldTags.put(new LongTag("seed", world.getSeed()));
		worldTags.put(new StringTag("generator", generatorName));
		worldTags.put(new LongTag("UUID_lsb", world.getUID().getLeastSignificantBits()));
		worldTags.put(new LongTag("UUID_msb", world.getUID().getMostSignificantBits()));
		worldTags.put(new ByteArrayTag("extra_data", ((DataMap) world.getDataMap()).getRawMap().compress()));
		worldTags.put(new LongTag("age", world.getAge()));
		//World version 2
		worldTags.put(new ListTag<FloatTag>("spawn_position", FloatTag.class, NBTMapper.transformToNBT(world.getSpawnPoint())));
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

		world.getItemMap().save();
	}

	public static SpoutWorld loadWorldFromData(String name, WorldGenerator generator, StringMap global) {
		SpoutWorld world = null;

		File worldData = new File(new File(SharedFileSystem.WORLDS_DIRECTORY, name), "world.dat");

		if (worldData.exists()) {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(new DataInputStream(new FileInputStream(worldData)), false);
				CompoundTag dataTag = (CompoundTag) is.readTag();
				CompoundMap map = dataTag.getValue();

				byte version = SafeCast.toByte(NBTMapper.toTagValue(map.get("version")), WORLD_VERSION);
				switch (version) {
					case 1:
						world = loadVersionOne(name, generator, global, map);
						break;
					case 2:
						world = loadVersionTwo(name, generator, global, map);
						break;
				}
			} catch (Exception e) {
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

	/**
	 * Loads version 2 of the world NBT
	 * @param name The name of the world
	 * @param generator The generator of the world
	 * @param global The global StringMap for the engine
	 * @param map The CompoundMap of tags from NBT
	 * @return The newly created SpoutWorld loaded from NBT
	 */
	private static SpoutWorld loadVersionTwo(String name, WorldGenerator generator, StringMap global, CompoundMap map) {
		SpoutWorld world;

		//Load the world specific item map
		File itemMapFile = new File(new File(SharedFileSystem.WORLDS_DIRECTORY, name), "materials.dat");
		BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
		if (itemMapFile.exists()) {
			itemStore.load();
		}
		StringMap itemMap = new StringMap(global, itemStore, 0, Short.MAX_VALUE, name + "ItemMap");

		String generatorName = generator.getName();

		GenericDatatableMap extraData = new GenericDatatableMap();

		long seed = SafeCast.toLong(NBTMapper.toTagValue(map.get("seed")), new Random().nextLong());
		String savedGeneratorName = SafeCast.toString(NBTMapper.toTagValue(map.get("generator")), "");

		long lsb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_lsb")), new Random().nextLong());
		long msb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_msb")), new Random().nextLong());

		byte[] extraDataBytes = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("extra_data")), new byte[0]);
		extraData.decompress(extraDataBytes);

		if (!savedGeneratorName.equals(generatorName)) {
			Spout.getLogger().severe("World was saved last with the generator: " + savedGeneratorName + " but is being loaded with: " + generatorName + " MAY CAUSE WORLD CORRUPTION!");
		}

		long age = SafeCast.toLong(NBTMapper.toTagValue(map.get("age")), 0L);
		world = new SpoutWorld(name, (SpoutEngine) Spout.getEngine(), seed, age, generator, new UUID(msb, lsb), itemMap, extraData);

		List<? extends FloatTag> spawnPosition = checkerListFloatTag.checkTag(map.get("spawn_position"));
		Transform spawn = NBTMapper.nbtToTransform(world, spawnPosition);
		world.setSpawnPoint(spawn);

		return world;
	}

	/**
	 * Loads version 1 of the world NBT
	 * @param name The name of the world
	 * @param generator The generator of the world
	 * @param global The global StringMap for the engine
	 * @param map The CompoundMap of tags from NBT
	 * @return The newly created SpoutWorld loaded from NBT
	 */
	private static SpoutWorld loadVersionOne(String name, WorldGenerator generator, StringMap global, CompoundMap map) {
		SpoutWorld world;

		//Load the world specific item map
		File itemMapFile = new File(new File(SharedFileSystem.WORLDS_DIRECTORY, name), "materials.dat");
		BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
		if (itemMapFile.exists()) {
			itemStore.load();
		}
		StringMap itemMap = new StringMap(global, itemStore, 0, Short.MAX_VALUE, name + "ItemMap");

		String generatorName = generator.getName();

		GenericDatatableMap extraData = new GenericDatatableMap();

		long seed = SafeCast.toLong(NBTMapper.toTagValue(map.get("seed")), new Random().nextLong());
		String savedGeneratorName = SafeCast.toString(NBTMapper.toTagValue(map.get("generator")), "");

		long lsb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_lsb")), new Random().nextLong());
		long msb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_msb")), new Random().nextLong());

		byte[] extraDataBytes = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("extraData")), new byte[0]);
		extraData.decompress(extraDataBytes);

		if (!savedGeneratorName.equals(generatorName)) {
			Spout.getEngine().getLogger().severe("World was saved last with the generator: " + savedGeneratorName + " but is being loaded with: " + generatorName + " MAY CAUSE WORLD CORRUPTION!");
		}

		long age = SafeCast.toLong(NBTMapper.toTagValue(map.get("age")), 0L);
		world = new SpoutWorld(name, (SpoutEngine) Spout.getEngine(), seed, age, generator, new UUID(msb, lsb), itemMap, extraData);

		return world;
	}

	public static void saveChunk(SpoutWorld world, SpoutChunkSnapshot snapshot, List<DynamicBlockUpdate> blockUpdates, OutputStream dos) {
		CompoundMap chunkTags = new CompoundMap();
		short[] blocks = snapshot.getBlockIds();
		short[] data = snapshot.getBlockData();

		//Switch block ids from engine material ids to world specific ids
		StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
		StringMap itemMap = world.getItemMap();
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = (short) global.convertTo(itemMap, blocks[i]);
		}

		chunkTags.put(new ByteTag("version", CHUNK_VERSION));
		chunkTags.put(new ByteTag("format", (byte) 0));
		chunkTags.put(new IntTag("x", snapshot.getX()));
		chunkTags.put(new IntTag("y", snapshot.getY()));
		chunkTags.put(new IntTag("z", snapshot.getZ()));
		chunkTags.put(new ByteTag("populationState", snapshot.getPopulationState().getId()));
		chunkTags.put(new ShortArrayTag("blocks", blocks));
		chunkTags.put(new ShortArrayTag("data", data));
		chunkTags.put(new ByteArrayTag("skyLight", snapshot.getSkyLight()));
		chunkTags.put(new ByteArrayTag("blockLight", snapshot.getBlockLight()));
		chunkTags.put(new CompoundTag("entities", saveEntities(snapshot.getEntities())));
		chunkTags.put(saveDynamicUpdates(blockUpdates));

		byte[] biomes = snapshot.getBiomeManager().serialize();
		if (biomes != null) {
			chunkTags.put(new StringTag("biomeManager", snapshot.getBiomeManager().getClass().getCanonicalName()));
			chunkTags.put(new ByteArrayTag("biomes", biomes));
		}

		chunkTags.put(new ByteArrayTag("extraData", ((DataMap) snapshot.getDataMap()).getRawMap().compress()));

		CompoundTag chunkCompound = new CompoundTag("chunk", chunkTags);

		NBTOutputStream os = null;
		try {
			os = new NBTOutputStream(dos, false);
			os.writeTag(chunkCompound);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving chunk {" + snapshot.getX() + ", " + snapshot.getY() + ", " + snapshot + "}", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}

		world.getItemMap().save();
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
			int cx = r.getChunkX() + x;
			int cy = r.getChunkY() + y;
			int cz = r.getChunkZ() + z;

			byte populationState = SafeCast.toGeneric(map.get("populationState"), new ByteTag("", PopulationState.POPULATED.getId()), ByteTag.class).getValue();
			short[] blocks = SafeCast.toShortArray(NBTMapper.toTagValue(map.get("blocks")), null);
			short[] data = SafeCast.toShortArray(NBTMapper.toTagValue(map.get("data")), null);
			byte[] skyLight = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("skyLight")), null);
			byte[] blockLight = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("blockLight")), null);
			byte[] extraData = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("extraData")), null);

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

			chunk = new FilteredChunk(r.getWorld(), r, cx, cy, cz, PopulationState.byID(populationState), blocks, data, skyLight, blockLight, manager, extraDataMap);

			CompoundMap entityMap = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("entities")), (CompoundMap) null, CompoundMap.class);
			loadEntities(r, entityMap, dataForRegion.loadedEntities);

			List<? extends CompoundTag> updateList = checkerListCompoundTag.checkTag(map.get("dynamic_updates"), null);
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

	private static CompoundMap saveEntities(List<EntitySnapshot> entities) {
		CompoundMap tagMap = new CompoundMap();

		for (EntitySnapshot e : entities) {
			Tag tag = saveEntity(e);
			if (tag != null) {
				tagMap.put(tag);
			}
		}

		return tagMap;
	}

	private static SpoutEntity loadEntity(SpoutRegion r, CompoundTag tag) {
		return loadEntity(r.getWorld(), tag, null, null); 
	}
	private static SpoutEntity loadEntity(World w, CompoundTag tag, String Name, SpoutSession<?> playerSession) {
		CompoundMap map = tag.getValue();

		@SuppressWarnings("unused")
		byte version = SafeCast.toByte(NBTMapper.toTagValue(map.get("version")), (byte) 0);
		String name = SafeCast.toString(NBTMapper.toTagValue(map.get("controller")), "");

		ControllerType type = ControllerRegistry.get(name);
		if (type == null) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "No controller type found matching: " + name);
		} else if (type.canCreateController()) {

			//Read entity
			Float pX = SafeCast.toFloat(NBTMapper.toTagValue(map.get("posX")), Float.MAX_VALUE);
			Float pY = SafeCast.toFloat(NBTMapper.toTagValue(map.get("posY")), Float.MAX_VALUE);
			Float pZ = SafeCast.toFloat(NBTMapper.toTagValue(map.get("posZ")), Float.MAX_VALUE);

			if (pX == Float.MAX_VALUE || pY == Float.MAX_VALUE || pZ == Float.MAX_VALUE) {
				return null;
			}

			float sX = SafeCast.toFloat(NBTMapper.toTagValue(map.get("scaleX")), 1.0F);
			float sY = SafeCast.toFloat(NBTMapper.toTagValue(map.get("scaleY")), 1.0F);
			float sZ = SafeCast.toFloat(NBTMapper.toTagValue(map.get("scaleZ")), 1.0F);

			float qX = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatX")), 0.0F);
			float qY = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatY")), 0.0F);
			float qZ = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatZ")), 0.0F);
			float qW = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatW")), 1.0F);

			long msb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_msb")), new Random().nextLong());
			long lsb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_lsb")), new Random().nextLong());
			UUID uid = new UUID(msb, lsb);

			int view = SafeCast.toInt(NBTMapper.toTagValue(map.get("view")), 0);
			boolean observer = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("observer")), new ByteTag("", (byte) 0), ByteTag.class).getBooleanValue();

			//Setup entity
			Controller controller = type.createController();
			try {
				boolean controllerDataExists = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("controller_data_exists")), new ByteTag("", (byte) 0), ByteTag.class).getBooleanValue();

				if (controllerDataExists) {
					byte[] data = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("controller_data")), new byte[0]);
					DatatableMap dataMap = ((DataMap) controller.getDataMap()).getRawMap();
					dataMap.decompress(data);
				}
			} catch (Exception error) {
				Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to load the controller for the type: " + type.getName(), error);
				return null;
			}

			//Setup entity
			Region r = w.getRegionFromBlock((int) Math.floor(pX), (int) Math.floor(pY), (int) Math.floor(pZ), LoadOption.NO_LOAD);
			if (r == null) {
				// TODO - this should never happen - entities should be located in the chunk that was just loaded
				Spout.getLogger().info("Attempted to load entity to unloaded region for block at " + (int) Math.floor(pX) + ", " + (int) Math.floor(pY) + ", " + (int) Math.floor(pZ));
				return null;
			}
			Transform t = new Transform(new Point(r.getWorld(), pX, pY, pZ), new Quaternion(qX, qY, qZ, qW, false), new Vector3(sX, sY, sZ));
			if (!(controller instanceof PlayerController)) {
				SpoutEntity e = new SpoutEntity((SpoutEngine) Spout.getEngine(), t, controller, view, uid, false);
				e.setObserver(observer);
				return e;
			}
			else {
				SpoutPlayer e = new SpoutPlayer(Name, t, playerSession, (SpoutEngine) Spout.getEngine(), view);
				return e;
			}
		} else {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to create controller for the type: " + type.getName());
		}

		return null;
	}

	private static CompoundTag saveEntity(EntitySnapshot e) {
		if (!e.isSavable() && (!(e instanceof PlayerSnapshot))) {
			return null;
		}
		CompoundMap map = new CompoundMap();
		map.put(new ByteTag("version", ENTITY_VERSION));
		map.put(new StringTag("controller", e.getType().getName()));

		//Write entity
		Transform t = e.getTransform();
		map.put(new FloatTag("posX",t.getPosition().getX()));
		map.put(new FloatTag("posY", t.getPosition().getY()));
		map.put(new FloatTag("posZ", t.getPosition().getZ()));

		map.put(new FloatTag("scaleX", t.getScale().getX()));
		map.put(new FloatTag("scaleY", t.getScale().getY()));
		map.put(new FloatTag("scaleZ", t.getScale().getZ()));

		map.put(new FloatTag("quatX", t.getRotation().getX()));
		map.put(new FloatTag("quatY", t.getRotation().getY()));
		map.put(new FloatTag("quatZ", t.getRotation().getZ()));
		map.put(new FloatTag("quatW", t.getRotation().getW()));

		map.put(new LongTag("UUID_msb", e.getUID().getMostSignificantBits()));
		map.put(new LongTag("UUID_lsb", e.getUID().getLeastSignificantBits()));

		map.put(new IntTag("view", e.getViewDistance()));
		map.put(new ByteTag("observer", e.isObserver()));

		//Write entity
		try {
			//Serialize data
			DatatableMap dataMap = ((DataMap) e.getDataMap()).getRawMap();
			if (!dataMap.isEmpty()) {
				map.put(new ByteTag("controller_data_exists", true));
				map.put(new ByteArrayTag("controller_data", dataMap.compress()));
			} else {
				map.put(new ByteTag("controller_data_exists", false));
			}
		} catch (Exception error) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to write the controller information for the type: " + e.getType(), error);
		}
		CompoundTag tag = null;
		if (e instanceof PlayerSnapshot) {
			tag = new CompoundTag(e.getWorldName(), map);
		} else {
		tag = new CompoundTag("entity_" + e.getId(), map);
		}
		return tag;
	}

	private static ListTag<CompoundTag> saveDynamicUpdates(List<DynamicBlockUpdate> updates) {
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

		map.put(new IntTag("packedv2", update.getPacked()));
		map.put(new LongTag("nextUpdate", update.getNextUpdate()));
		map.put(new IntTag("data", update.getData()));

		return new CompoundTag("update", map);
	}

	private static void loadDynamicUpdates(List<? extends CompoundTag> list, List<DynamicBlockUpdate> loadedUpdates) {
		if (list == null) {
			return;
		}

		for (CompoundTag compoundTag : list) {
			DynamicBlockUpdate update = loadDynamicUpdate(compoundTag);
			if (update == null) {
				continue;
			}

			loadedUpdates.add(update);
		}
	}

	private static DynamicBlockUpdate loadDynamicUpdate(CompoundTag compoundTag) {
		final CompoundMap map = compoundTag.getValue();
		int packed = SafeCast.toInt(NBTMapper.toTagValue(map.get("packedv2")), -1);
		if (packed == -1) {
			packed = SafeCast.toInt(NBTMapper.toTagValue(map.get("packed")), -1);
			if (packed < 0) {
				return null;
			} else {
				int x = 0xFF & ByteTripleHashed.key1(packed);
				int y = 0xFF & ByteTripleHashed.key2(packed);
				int z = 0xFF & ByteTripleHashed.key3(packed);
				packed = SignedTenBitTripleHashed.key(x, y, z);
			}
		}

		final long nextUpdate = SafeCast.toLong(NBTMapper.toTagValue(map.get("nextUpdate")), -1L);
		if (nextUpdate < 0) {
			return null;
		}

		final int data = SafeCast.toInt(NBTMapper.toTagValue(map.get("data")), 0);
		return new DynamicBlockUpdate(packed, nextUpdate, data);
	}
	
	public static void readColumn(InputStream in, SpoutColumn column, AtomicInteger lowestY, BlockMaterial[][] topmostBlocks) {
		if (in == null) {
			//The inputstream is null because no height map data exists
			for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
				for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
					column.getAtomicInteger(x, z).set(Integer.MIN_VALUE);
					topmostBlocks[x][z] = null;
					column.setDirty(x, z);
				}
			}
			lowestY.set(Integer.MAX_VALUE);
			return;
		}

		DataInputStream dataStream = new DataInputStream(in);
		try {
			for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
				for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
					column.getAtomicInteger(x, z).set(dataStream.readInt());
				}
			}
			int version;
			try {
				version = dataStream.readInt();
			} catch (EOFException eof) {
				version = 1;
			}
			if (version > 1) {
				lowestY.set(dataStream.readInt());
			} else {
				lowestY.set(Integer.MAX_VALUE);
			}
			if (version > 2) {
				StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
				StringMap itemMap;
				if (version == 3) {
					itemMap = global;
				} else {
					itemMap = column.getWorld().getItemMap();
				}
				boolean warning = false;
				for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
					for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
						if (!dataStream.readBoolean()) {
							continue;
						}
						int blockState = dataStream.readInt();
						short blockId = BlockFullState.getId(blockState);
						short blockData = BlockFullState.getData(blockState);
						blockId = (short) itemMap.convertTo(global, blockId);
						blockState = BlockFullState.getPacked(blockId, blockData);
						BlockMaterial m;
						try {
							m = (BlockMaterial) MaterialRegistry.get(blockState);
						} catch (ClassCastException e) {
							m = null;
							if (!warning) {
								Spout.getLogger().severe("Error reading column topmost block information, block was not a valid BlockMaterial");
								warning = false;
							}
						}
						if (m == null) {
							column.setDirty(x, z);
						}
						topmostBlocks[x][z] = m;
					}
				}
			}
		} catch (IOException e) {
			Spout.getLogger().severe("Error reading column height-map for column" + column.getX() + ", " + column.getZ());
		}
	}
	
	public static void writeColumn(OutputStream out, SpoutColumn column, AtomicInteger lowestY, BlockMaterial[][] topmostBlocks) {
		DataOutputStream dataStream = new DataOutputStream(out);
		try {
			for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
				for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
					dataStream.writeInt(column.getAtomicInteger(x, z).get());
				}
			}
			dataStream.writeInt(COLUMN_VERSION);
			dataStream.writeInt(lowestY.get());
			StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
			StringMap itemMap;
			itemMap = column.getWorld().getItemMap();
			for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
				for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
					Material m = topmostBlocks[x][z];
					if (m == null) {
						dataStream.writeBoolean(false);
						continue;
					} else {
						dataStream.writeBoolean(true);
					}
					short blockId = m.getId();
					short blockData = m.getData();
					blockId = (short) global.convertTo(itemMap, blockId);
					dataStream.writeInt(BlockFullState.getPacked(blockId, blockData));
				}
			}
			dataStream.flush();
		} catch (IOException e) {
			Spout.getLogger().severe("Error writing column height-map for column" + column.getX() + ", " + column.getZ());
		}

	}
	
	
}
