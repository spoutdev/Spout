/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.util.StringToUniqueIntegerMap;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.engine.SpoutServer;
import org.spout.engine.world.SpoutColumn;
import org.spout.engine.world.SpoutServerWorld;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntArrayTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;
import org.spout.nbt.util.NBTMapper;

public class ColumnFiles {
	public static final int COLUMN_VERSION = 2;

	public static void readColumn(InputStream in, SpoutColumn column, AtomicInteger lowestY, AtomicInteger highestY, BlockMaterial[][] topmostBlocks) {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("Unable to read column in client mode");
		}
		if (in == null) {
			initColumn(column, lowestY, highestY, topmostBlocks);
			return;
		}

		NBTInputStream is = null;
		DataInputStream dataStream = new DataInputStream(in);
		try {
			is = new NBTInputStream(dataStream, false);
			CompoundTag chunkTag = (CompoundTag) is.readTag();
			CompoundMap map = chunkTag.getValue();

			byte version = SafeCast.toByte(NBTMapper.toTagValue(map.get("version")), (byte) -1);

			boolean converted = false;

			if (version > COLUMN_VERSION) {
				Spout.getLogger().log(Level.SEVERE, "Chunk version " + version + " exceeds maximum allowed value of " + COLUMN_VERSION);
				initColumn(column, lowestY, highestY, topmostBlocks);
				return;
			} else if (version < COLUMN_VERSION) {
				if (version <= 0) {
					Spout.getLogger().log(Level.SEVERE, "Invalid column version " + version);
					initColumn(column, lowestY, highestY, topmostBlocks);
					return;
				}
				if (version >= 1) {
					map = convertV1V2(map);
				}
				converted = true;
				// Added conversion code here
			}

			loadColumn(column, lowestY, highestY, topmostBlocks, map);

			if (converted) {
				column.setDirty();
			}
		} catch (IOException e) {
			Spout.getLogger().severe("Error reading column height-map for column" + column.getX() + ", " + column.getZ());
		}
	}

	private static void loadColumn(SpoutColumn column, AtomicInteger lowestY, AtomicInteger highestY, BlockMaterial[][] topmostBlocks, CompoundMap map) {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("Unable to load column in client mode");
		}
		int[] heights = SafeCast.toIntArray(NBTMapper.toTagValue(map.get("heights")), null);

		for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
			for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
				column.getAtomicInteger(x, z).set(heights[NibblePairHashed.intKey(x, z)]);
			}
		}

		lowestY.set(SafeCast.toInt(NBTMapper.toTagValue(map.get("lowest_y")), Integer.MAX_VALUE));
		highestY.set(SafeCast.toInt(NBTMapper.toTagValue(map.get("highest_y")), Integer.MAX_VALUE));

		//Save heightmap
		StringToUniqueIntegerMap global = ((SpoutServer) Spout.getEngine()).getEngineItemMap();
		StringToUniqueIntegerMap itemMap = ((SpoutServerWorld) column.getWorld()).getItemMap();
		boolean warning = false;
		byte[] validMaterial = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("valid_material")), null);
		int[] topmostMaterial = SafeCast.toIntArray(NBTMapper.toTagValue(map.get("topmost_material")), null);

		if (validMaterial == null || topmostMaterial == null) {
			Spout.getLogger().severe("Topmost block arrays missing when reading column");
			initColumn(column, lowestY, highestY, topmostBlocks);
			return;
		}
		for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
			for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
				int key = NibblePairHashed.intKey(x, z);
				if (validMaterial[key] == 0) {
					continue;
				}
				int blockState = topmostMaterial[key];
				short blockId = BlockFullState.getId(blockState);
				short blockData = BlockFullState.getData(blockState);
				blockId = (short) itemMap.convertTo(global, blockId);
				blockState = BlockFullState.getPacked(blockId, blockData);
				BlockMaterial m;
				try {
					m = MaterialRegistry.get(blockState);
				} catch (ClassCastException e) {
					m = null;
					if (!warning) {
						Spout.getLogger().severe("Error reading column topmost block information, block was not a valid BlockMaterial");
						warning = true;
					}
				}
				if (m == null) {
					column.setDirty(x, z);
				}
				topmostBlocks[x][z] = m;
			}
		}

		BiomeManager manager = null;
		String biomeManagerClass = SafeCast.toString(NBTMapper.toTagValue(map.get("biome_manager")), null);
		byte[] biomes = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("biomes")), null);

		if (biomeManagerClass != null && biomes != null) {
			//Attempt to create the biome manager class from the class name
			try {
				@SuppressWarnings ("unchecked")
				Class<? extends BiomeManager> clazz = (Class<? extends BiomeManager>) Class.forName(biomeManagerClass);
				Class<?>[] params = {int.class, int.class};
				manager = clazz.getConstructor(params).newInstance(column.getX(), column.getZ());
				manager.deserialize(biomes);
				column.setBiomeManager(manager);
			} catch (Exception e) {
				Spout.getLogger().severe("Unable to find biome manager class " + biomeManagerClass + ", this may cause world corruption");
				e.printStackTrace();
			}
		}
	}

	private static void initColumn(SpoutColumn column, AtomicInteger lowestY, AtomicInteger highestY, BlockMaterial[][] topmostBlocks) {
		//The inputstream is null because no height map data exists
		for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
			for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
				column.getAtomicInteger(x, z).set(Integer.MIN_VALUE);
				topmostBlocks[x][z] = null;
				column.setDirty(x, z);
			}
		}
		lowestY.set(Integer.MAX_VALUE);
		highestY.set(Integer.MIN_VALUE);
	}

	public static void writeColumn(OutputStream out, SpoutColumn column, AtomicInteger lowestY, AtomicInteger highestY, BlockMaterial[][] topmostBlocks) {
		NBTOutputStream NBTStream = null;
		try {
			NBTStream = new NBTOutputStream(out, false);
			CompoundMap map = saveColumn(column, lowestY, highestY, topmostBlocks);
			NBTStream.writeTag(new CompoundTag("column", map));
			NBTStream.flush();
		} catch (IOException ioe) {
			Spout.getLogger().log(Level.SEVERE, "Error saving column {" + column.getX() + ", " + column.getZ() + "}", ioe);
		} finally {
			if (NBTStream != null) {
				try {
					NBTStream.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	private static CompoundMap saveColumn(SpoutColumn column, AtomicInteger lowestY, AtomicInteger highestY, BlockMaterial[][] topmostBlocks) {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("Unable to save column in client mode");
		}
		CompoundMap map = new CompoundMap();

		map.put(new ByteTag("version", (byte) COLUMN_VERSION));

		int[] heights = new int[SpoutColumn.BLOCKS.SIZE * SpoutColumn.BLOCKS.SIZE];

		for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
			for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
				int key = NibblePairHashed.intKey(x, z);
				heights[key] = column.getAtomicInteger(x, z).get();
			}
		}

		map.put(new IntArrayTag("heights", heights));

		map.put(new IntTag("lowest_y", lowestY.get()));
		map.put(new IntTag("highest_y", highestY.get()));

		byte[] validMaterial = new byte[SpoutColumn.BLOCKS.SIZE * SpoutColumn.BLOCKS.SIZE];
		int[] topmostMaterial = new int[SpoutColumn.BLOCKS.SIZE * SpoutColumn.BLOCKS.SIZE];

		StringToUniqueIntegerMap global = ((SpoutServer) Spout.getEngine()).getEngineItemMap();
		StringToUniqueIntegerMap itemMap;
		itemMap = ((SpoutServerWorld) column.getWorld()).getItemMap();
		for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
			for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
				int key = NibblePairHashed.intKey(x, z);
				Material m = topmostBlocks[x][z];
				if (m == null) {
					continue;
				}
				validMaterial[key] = 1;
				short blockId = m.getId();
				short blockData = m.getData();
				blockId = (short) global.convertTo(itemMap, blockId);
				topmostMaterial[key] = BlockFullState.getPacked(blockId, blockData);
			}
		}

		map.put(new ByteArrayTag("valid_material", validMaterial));
		map.put(new IntArrayTag("topmost_material", topmostMaterial));

		BiomeManager manager = column.getBiomeManager();
		if (manager != null) {
			map.put(new StringTag("biome_manager", manager.getClass().getName()));
			map.put(new ByteArrayTag("biomes", manager.serialize()));
		}

		return map;
	}

	/**
	 * Converts from version 1 to version 2<br> <br> Adds a highestY field
	 */
	private static CompoundMap convertV1V2(CompoundMap map) {
		map.put(new IntTag("highest_y", Integer.MIN_VALUE));
		return map;
	}
}

