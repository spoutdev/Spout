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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.util.StringMap;
import org.spout.engine.SpoutEngine;
import org.spout.engine.world.SpoutColumn;

public class ColumnFiles {
	
	public static final int COLUMN_VERSION = 1;
	
	public static void readColumn(InputStream in, SpoutColumn column, AtomicInteger lowestY, BlockMaterial[][] topmostBlocks) {
		if (in == null) {
			initColumn(column, lowestY, topmostBlocks);
			return;
		}

		DataInputStream dataStream = new DataInputStream(in);
		try {
			for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
				for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
					column.getAtomicInteger(x, z).set(dataStream.readInt());
				}
			}

			int version = dataStream.readInt();
			
			if (version > COLUMN_VERSION) {
				Spout.getLogger().log(Level.SEVERE, "Column version " + version + " exceeds maximum allowed value of " + COLUMN_VERSION);
				initColumn(column, lowestY, topmostBlocks);
				return;
			} else if (version < COLUMN_VERSION) {
				// TODO - Add conversion code here
				Spout.getLogger().log(Level.SEVERE, "Outdated Column version " + version);
				initColumn(column, lowestY, topmostBlocks);
				return;
			}

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
	
	private static void initColumn(SpoutColumn column, AtomicInteger lowestY, BlockMaterial[][] topmostBlocks) {
		//The inputstream is null because no height map data exists
		for (int x = 0; x < SpoutColumn.BLOCKS.SIZE; x++) {
			for (int z = 0; z < SpoutColumn.BLOCKS.SIZE; z++) {
				column.getAtomicInteger(x, z).set(Integer.MIN_VALUE);
				topmostBlocks[x][z] = null;
				column.setDirty(x, z);
			}
		}
		lowestY.set(Integer.MAX_VALUE);
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
