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

import gnu.trove.procedure.TShortObjectProcedure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
import org.spout.api.component.ChunkComponentOwner;
import org.spout.api.component.Component;
import org.spout.api.component.components.BlockComponent;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.EntitySnapshot;
import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot.BlockComponentSnapshot;
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
import org.spout.api.plugin.CommonClassLoader;
import org.spout.api.util.NBTMapper;
import org.spout.api.util.StringMap;
import org.spout.api.util.hashing.ByteTripleHashed;
import org.spout.api.util.hashing.NibbleQuadHashed;
import org.spout.api.util.hashing.SignedTenBitTripleHashed;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutPlayer;
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
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class WorldFiles {
	private static final TypeChecker<List<? extends FloatTag>> checkerListFloatTag = TypeChecker.tList(FloatTag.class);
	private static final TypeChecker<List<? extends CompoundTag>> checkerListCompoundTag = TypeChecker.tList(CompoundTag.class);
	private static final byte WORLD_VERSION = 1;
	private static final byte ENTITY_VERSION = 1;
	private static final byte CHUNK_VERSION = 1;
	private static final int COLUMN_VERSION = 1;

	public static void savePlayerData(List<SpoutPlayer> Players) {
		for (SpoutPlayer player : Players) {
			savePlayerData(player);
		}
	}

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
	 * @return player, or null if it could not be loaded
	 */
	public static SpoutPlayer loadPlayerData(String name) {
		File playerDir = new File(Spout.getEngine().getDataFolder().toString(), "players");
		String fileName = name + ".dat";
		File playerData = new File(playerDir, fileName);
		if (playerData.exists()) {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(new DataInputStream(new FileInputStream(playerData)), false);
				CompoundTag dataTag = (CompoundTag) is.readTag();
				World world = Spout.getEngine().getWorld(dataTag.getName());
				return (SpoutPlayer)loadEntity(world, dataTag, name);
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
		worldTags.put(new ByteArrayTag("extra_data", world.getComponentHolder().getData().serialize()));
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
	 * @throws IOException 
	 */
	private static SpoutWorld loadVersionOne(String name, WorldGenerator generator, StringMap global, CompoundMap map) throws IOException {
		SpoutWorld world;

		//Load the world specific item map
		File itemMapFile = new File(new File(SharedFileSystem.WORLDS_DIRECTORY, name), "materials.dat");
		BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
		if (itemMapFile.exists()) {
			itemStore.load();
		}
		StringMap itemMap = new StringMap(global, itemStore, 0, Short.MAX_VALUE, name + "ItemMap");

		String generatorName = generator.getName();

		long seed = SafeCast.toLong(NBTMapper.toTagValue(map.get("seed")), new Random().nextLong());
		String savedGeneratorName = SafeCast.toString(NBTMapper.toTagValue(map.get("generator")), "");

		long lsb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_lsb")), new Random().nextLong());
		long msb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_msb")), new Random().nextLong());

		byte[] extraDataBytes = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("extra_data")), new byte[0]);

		if (!savedGeneratorName.equals(generatorName)) {
			Spout.getLogger().severe("World was saved last with the generator: " + savedGeneratorName + " but is being loaded with: " + generatorName + " MAY CAUSE WORLD CORRUPTION!");
		}

		long age = SafeCast.toLong(NBTMapper.toTagValue(map.get("age")), 0L);
		world = new SpoutWorld(name, (SpoutEngine) Spout.getEngine(), seed, age, generator, new UUID(msb, lsb), itemMap);

		SerializableMap dataMap = world.getComponentHolder().getData();
		dataMap.deserialize(extraDataBytes);

		List<? extends FloatTag> spawnPosition = checkerListFloatTag.checkTag(map.get("spawn_position"));
		Transform spawn = NBTMapper.nbtToTransform(world, spawnPosition);
		world.setSpawnPoint(spawn);

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
		chunkTags.put(saveBlockComponents(snapshot.getBlockComponents()));
		chunkTags.put(new ByteArrayTag("extraData", snapshot.getDataMap().serialize()));

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

			//Convert world block ids to engine material ids
			SpoutWorld world = r.getWorld();
			StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
			StringMap itemMap = world.getItemMap();
			for (int i = 0; i < blocks.length; i++) {
				blocks[i] = (short) itemMap.convertTo(global, blocks[i]);
			}

			ManagedHashMap extraDataMap = new ManagedHashMap();
			extraDataMap.deserialize(extraData);

			chunk = new FilteredChunk(r.getWorld(), r, cx, cy, cz, PopulationState.byID(populationState), blocks, data, skyLight, blockLight, extraDataMap);

			CompoundMap entityMap = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("entities")), (CompoundMap) null, CompoundMap.class);
			loadEntities(r, entityMap, dataForRegion.loadedEntities);

			List<? extends CompoundTag> updateList = checkerListCompoundTag.checkTag(map.get("dynamic_updates"));
			loadDynamicUpdates(updateList, dataForRegion.loadedUpdates);
			
			List<? extends CompoundTag> componentsList = checkerListCompoundTag.checkTag(map.get("block_components"), null);

			//Load Block components
			//This is a three-part process
			//1.) Scan the blocks and add them to the chunk map
			//2.) Load the datatables associated with the block components
			//3.) Attach the components
			for (int dx = 0; dx < Chunk.BLOCKS.SIZE; dx++) {
				for (int dy = 0; dy < Chunk.BLOCKS.SIZE; dy++) {
					for (int dz = 0; dz < Chunk.BLOCKS.SIZE; dz++) {
						int index = (dy << 8) + (dz << 4) + dx;
						BlockMaterial bm = (BlockMaterial) MaterialRegistry.get(BlockFullState.getPacked(blocks[index], data[index]));
						BlockComponent component = bm.getBlockComponent();
						if (component != null) {
							short packed = NibbleQuadHashed.key(dx, dy, dz, 0);
							//Does not need synchronized, the chunk is not yet accessible outside this thread
							chunk.getBlockComponents().put(packed, component);
							ChunkComponentOwner owner = new ChunkComponentOwner(chunk, chunk.getBlockX() + dx, chunk.getBlockY() + dy, chunk.getBlockZ() + dz);
							component.attachTo(owner);
						}
					}
				}
			}
			//Load data associated with block components
			loadBlockComponents(chunk, componentsList);
			//Attach block components
			chunk.getBlockComponents().forEachEntry(new AttachComponentProcedure());
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
	
	private static class AttachComponentProcedure implements TShortObjectProcedure<BlockComponent> {
		@Override
		public boolean execute(short a, BlockComponent b) {
			try {
				b.onAttached();
			} catch (Exception e) {
				Spout.getLogger().log(Level.SEVERE, "Unhandled exception attaching block component", e);
			}
			return true;
		}
	}

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	private static CompoundMap saveEntities(List<EntitySnapshot> entities) {
		CompoundMap tagMap = new CompoundMap();
		for (EntitySnapshot e : entities) {
			//Players are saved elsewhere
			if (!(e instanceof PlayerSnapshot)) {
				Tag tag = saveEntity(e);
				if (tag != null) {
					tagMap.put(tag);
				}
			}
		}

		return tagMap;
	}

	private static SpoutEntity loadEntity(SpoutRegion r, CompoundTag tag) {
		return loadEntity(r.getWorld(), tag, null); 
	}

	private static SpoutEntity loadEntity(World w, CompoundTag tag, String name) {
		try {
			return loadEntityImpl(w, tag, name);
		} catch (Exception e) {
			Spout.getLogger().log(Level.SEVERE, "Unable to load entity", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static SpoutEntity loadEntityImpl(World w, CompoundTag tag, String name) {
		CompoundMap map = tag.getValue();

		@SuppressWarnings("unused")
		byte version = SafeCast.toByte(NBTMapper.toTagValue(map.get("version")), (byte) 0);
		boolean player = SafeCast.toByte(NBTMapper.toTagValue(map.get("player")), (byte) 0) == 1;

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

		//Setup data
		boolean controllerDataExists = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("controller_data_exists")), new ByteTag("", (byte) 0), ByteTag.class).getBooleanValue();
		byte[] dataMap = null;
		if (controllerDataExists) {
			dataMap = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("controller_data")), new byte[0]);
		}

		//Setup entity
		Region r = w.getRegionFromBlock(Math.round(pX), Math.round(pY), Math.round(pZ), LoadOption.NO_LOAD);
		if (r == null) {
			// TODO - this should never happen - entities should be located in the chunk that was just loaded
			Spout.getLogger().info("Attempted to load entity to unloaded region");
			Thread.dumpStack();
			return null;
		}
		final Transform t = new Transform(new Point(r.getWorld(), pX, pY, pZ), new Quaternion(qX, qY, qZ, qW, false), new Vector3(sX, sY, sZ));
		
		ListTag<StringTag> components = (ListTag<StringTag>)map.get("components");
		List<Class<? extends Component>> types = new ArrayList<Class<? extends Component>>(components.getValue().size());
		for (StringTag component : components.getValue()) {
			try {
				Class<? extends Component> clazz = (Class<? extends Component>) CommonClassLoader.findPluginClass(component.getValue());
				types.add(clazz);
			} catch (ClassNotFoundException ex) {
				Spout.getLogger().log(Level.SEVERE, "Unable to find component class " + component.getValue(), ex);
			}
		}

		SpoutEntity e;
		if (!player) {
			e = new SpoutEntity(t, view, uid, false, dataMap, types.toArray(new Class[types.size()]));
			e.setObserver(observer);
		} else {
			e = new SpoutPlayer(name, t, view, uid, false, dataMap, types.toArray(new Class[types.size()]));
		}

		return e;
	}

	private static CompoundTag saveEntity(EntitySnapshot e) {
		if (!e.isSavable() && (!(e instanceof PlayerSnapshot))) {
			return null;
		}
		CompoundMap map = new CompoundMap();
		map.put(new ByteTag("version", ENTITY_VERSION));

		map.put(new ByteTag("player", (e instanceof PlayerSnapshot)));

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

		//Serialize data
		if (!e.getDataMap().isEmpty()) {
			map.put(new ByteTag("controller_data_exists", true));
			map.put(new ByteArrayTag("controller_data", e.getDataMap().serialize()));
		} else {
			map.put(new ByteTag("controller_data_exists", false));
		}
		
		List<StringTag> components = new ArrayList<StringTag>();
		for (Class<? extends Component> clazz : e.getComponents()) {
			components.add(new StringTag("component", clazz.getName()));
		}
		map.put(new ListTag<StringTag>("components", StringTag.class, components));
		
		CompoundTag tag = null;
		if (e instanceof PlayerSnapshot) {
			tag = new CompoundTag(e.getWorldName(), map);
		} else {
			tag = new CompoundTag("entity_" + e.getId(), map);
		}
		return tag;
	}

	private static ListTag<CompoundTag> saveBlockComponents(List<BlockComponentSnapshot> components) {
		List<CompoundTag> list = new ArrayList<CompoundTag>(components.size());
		
		for (BlockComponentSnapshot snapshot : components) {
			CompoundTag tag = saveBlockComponent(snapshot);
			if (tag != null) {
				list.add(tag);
			}
		}
		return new ListTag<CompoundTag>("block_components", CompoundTag.class, list);
	}

	private static CompoundTag saveBlockComponent(BlockComponentSnapshot snapshot) {
		if (!snapshot.getData().isEmpty()) {
			byte[] data = snapshot.getData().serialize();

			if (data != null && data.length > 0) {
				CompoundMap map = new CompoundMap();
				short packed = NibbleQuadHashed.key(snapshot.getX(), snapshot.getY(), snapshot.getZ(), 0);
				map.put(new ShortTag("packed", packed));
				map.put(new ByteArrayTag("data", data));

				return new CompoundTag("block_component_" + packed, map);
			}
		}

		return null;
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

	private static void loadBlockComponents(SpoutChunk chunk, List<? extends CompoundTag> list) {
		if (list == null) {
			return;
		}

		for (CompoundTag compoundTag : list) {
			CompoundMap map = compoundTag.getValue();
			short packed = (Short) map.get("packed").getValue();
			ByteArrayTag data = (ByteArrayTag) map.get("data");
	
			BlockComponent component = chunk.getBlockComponents().get(packed);
			if (component != null) {
				try {
					component.getOwner().getData().deserialize(data.getValue());
				} catch (IOException e) {
					Spout.getLogger().log(Level.SEVERE, "Unhandled exception deserializing block component data", e);
				}
			}
		}
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
			@SuppressWarnings("unused")
			int version = dataStream.readInt();
			
			lowestY.set(dataStream.readInt());
			
			//Save heightmap
			StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
			StringMap itemMap = column.getWorld().getItemMap();
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
			
			//Save Biomes
			BiomeManager manager = null;
			try {
				//Biome manager is serialized with:
				// - boolean, if a biome manager exists
				// - String, the class name
				// - int, the number of bytes of data to read
				// - byte[], size of the above int in length
				boolean exists = dataStream.readBoolean();
				if (exists) {
					String biomeManagerClass = dataStream.readUTF();
					int biomeSize = dataStream.readInt();
					byte[] biomes = new byte[biomeSize];
					dataStream.readFully(biomes);
					
					//Attempt to create the biome manager class from the class name
					@SuppressWarnings("unchecked")
					Class<? extends BiomeManager> clazz = (Class<? extends BiomeManager>) Class.forName(biomeManagerClass);
					Class<?>[] params = {int.class, int.class};
					manager = clazz.getConstructor(params).newInstance(column.getX(), column.getZ());
					manager.deserialize(biomes);
					column.setBiomeManager(manager);
				}
			} catch (Exception e) {
				Spout.getLogger().log(Level.SEVERE, "Failed to read biome data for column", e);
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
			//Biome manager is serialized with:
			// - boolean, if a biome manager exists
			// - String, the class name
			// - int, the number of bytes of data to read
			// - byte[], size of the above int in length
			BiomeManager manager = column.getBiomeManager();
			if (manager != null) {
				dataStream.writeBoolean(true);
				dataStream.writeUTF(manager.getClass().getName());
				byte[] data = manager.serialize();
				dataStream.writeInt(data.length);
				dataStream.write(data);
			} else {
				dataStream.writeBoolean(false);
			}
			dataStream.flush();
		} catch (IOException e) {
			Spout.getLogger().severe("Error writing column height-map for column" + column.getX() + ", " + column.getZ());
		}
	}
}
