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
package org.spout.engine.world;

import gnu.trove.procedure.TShortObjectProcedure;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spout.api.component.components.BlockComponent;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntitySnapshot;
import org.spout.api.entity.Player;
import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.hashing.NibbleQuadHashed;
import org.spout.api.util.thread.SnapshotRead;

import org.spout.engine.world.SpoutChunk.PopulationState;

public class SpoutChunkSnapshot extends ChunkSnapshot {
	/**
	 * The parent region that manages this chunk
	 */
	private final WeakReference<Region> parentRegion;
	private final byte worldSkyLightLoss;
	private final List<EntitySnapshot> entities;
	private final List<BlockComponentSnapshot> blockComponents;
	private final short[] blockIds;
	private final short[] blockData;
	private final byte[] blockLight;
	private final byte[] skyLight;
	private final BiomeManager biomes;
	private final SerializableMap dataMap;
	private final PopulationState populationState;
	private boolean renderDirty = false;

	public SpoutChunkSnapshot(SpoutChunk chunk, short[] blockIds, short[] blockData, byte[] blockLight, byte[] skyLight, EntityType type, ExtraData data) {
		super(chunk.getWorld(), chunk.getX() * CHUNK_SIZE, chunk.getY() * CHUNK_SIZE, chunk.getZ() * CHUNK_SIZE);
		parentRegion = new WeakReference<Region>(chunk.getRegion());

		// Cache entities
		if (type == EntityType.BOTH) {
			this.entities = Collections.unmodifiableList(getEntities(chunk));
			BlockSnapshotProcedure procedure = new BlockSnapshotProcedure(chunk);
			synchronized(chunk.getBlockComponents()) {
				chunk.getBlockComponents().forEachEntry(procedure);
			}
			this.blockComponents = Collections.unmodifiableList(procedure.snapshots);
		} else if (type == EntityType.ENTITIES) {
			this.entities = Collections.unmodifiableList(getEntities(chunk));
			this.blockComponents = null;
		} else if (type == EntityType.BLOCK_COMPONENTS) {
			this.entities = null;
			BlockSnapshotProcedure procedure = new BlockSnapshotProcedure(chunk);
			synchronized(chunk.getBlockComponents()) {
				chunk.getBlockComponents().forEachEntry(procedure);
			}
			this.blockComponents = Collections.unmodifiableList(procedure.snapshots);
		} else {
			this.entities = null;
			this.blockComponents = null;
		}

		this.worldSkyLightLoss = (byte) (15 - chunk.getWorld().getSkyLight());

		// Cache blocks
		this.blockIds = blockIds;
		this.blockData = blockData;
		this.blockLight = blockLight;
		this.skyLight = skyLight;

		// Cache extra data
		if (data == ExtraData.BIOME_DATA) {
			this.biomes = chunk.getWorld().getBiomeManager(chunk.getBlockX(), chunk.getBlockZ()).clone();
			this.dataMap = null;
		} else if (data == ExtraData.DATATABLE) {
			this.dataMap = chunk.getDataMap().deepCopy();
			this.biomes = null;
		} else if (data == ExtraData.BOTH) {
			this.biomes = chunk.getWorld().getBiomeManager(chunk.getBlockX(), chunk.getBlockZ()).clone();
			this.dataMap = chunk.getDataMap().deepCopy();
		} else {
			this.biomes = null;
			this.dataMap = null;
		}
		this.populationState = chunk.getPopulationState();
		renderDirty = chunk.isDirty();
	}

	private static List<EntitySnapshot> getEntities(SpoutChunk chunk) {
		ArrayList<EntitySnapshot> entities = new ArrayList<EntitySnapshot>();
		for (Entity e : chunk.getLiveEntities()) {
			if (e instanceof Player) {
				entities.add(new PlayerSnapshot((Player) e));
			} else {
				entities.add(new EntitySnapshot(e));
			}
		}
		return entities;
	}

	private int getBlockIndex(int x, int y, int z) {
		return (y & Chunk.BLOCKS.MASK) << 8 | (z & Chunk.BLOCKS.MASK) << 4 | x & Chunk.BLOCKS.MASK;
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		if (blockIds == null) {
			throw new UnsupportedOperationException("This chunk snapshot does not contain block ids");
		}
		BlockMaterial mat = BlockMaterial.get(getBlockId(x, y, z));
		return mat == null ? BlockMaterial.AIR : mat;
	}

	private short getBlockId(int x, int y, int z) {
		return blockIds[this.getBlockIndex(x, y, z)];
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		if (blockData == null) {
			throw new UnsupportedOperationException("This chunk snapshot does not contain block data");
		}
		return blockData[this.getBlockIndex(x, y, z)];
	}

	@Override
	public int getBlockFullState(int x, int y, int z) {
		return BlockFullState.getPacked(getBlockId(x, y, z), getBlockData(x, y, z));
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		byte level = this.getBlockSkyLightRaw(x, y, z);
		if (level > this.worldSkyLightLoss) {
			return (byte) (level - this.worldSkyLightLoss);
		} else {
			return 0;
		}
	}

	@Override
	public byte getBlockSkyLightRaw(int x, int y, int z) {
		if (skyLight == null) {
			throw new UnsupportedOperationException("This chunk snapshot does not contain sky light");
		}
		int index = getBlockIndex(x, y, z);
		byte light = skyLight[index >> 1];
		if ((index & 1) == 1) {
			return NibblePairHashed.key1(light);
		} else {
			return NibblePairHashed.key2(light);
		}
	}

	@Override
	public byte getBlockLight(int x, int y, int z) {
		if (blockLight == null) {
			throw new UnsupportedOperationException("This chunk snapshot does not contain block light");
		}
		int index = getBlockIndex(x, y, z);
		byte light = blockLight[index >> 1];
		if ((index & 1) == 1) {
			return NibblePairHashed.key1(light);
		} else {
			return NibblePairHashed.key2(light);
		}
	}

	@Override
	public Region getRegion() {
		return parentRegion.get();
	}

	@Override
	public List<EntitySnapshot> getEntities() {
		if (this.entities == null) {
			throw new UnsupportedOperationException("This chunk snapshot does not contain entities");
		} else {
			return this.entities;
		}
	}

	@Override
	public short[] getBlockIds() {
		return blockIds;
	}

	@Override
	public short[] getBlockData() {
		return blockData;
	}

	@Override
	public byte[] getBlockLight() {
		return blockLight;
	}

	@Override
	public byte[] getSkyLight() {
		return skyLight;
	}

	public boolean isRenderDirty() {
		return renderDirty;
	}

	public void setRenderDirty(boolean val) {
		renderDirty = false;
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		return biomes.getBiome(x, y, z);
	}

	@Override
	public boolean isPopulated() {
		return populationState == PopulationState.POPULATED;
	}

	public PopulationState getPopulationState() {
		return populationState;
	}

	@Override
	public SerializableMap getDataMap() {
		return this.dataMap;
	}

	@Override
	public BiomeManager getBiomeManager() {
		return biomes;
	}

	@Override
	public List<BlockComponentSnapshot> getBlockComponents() {
		return blockComponents;
	}

	@Override
	@SnapshotRead
	public BlockComponent getBlockComponent(int x, int y, int z) {
		throw new UnsupportedOperationException("Use getBlockComponents instead");
	}

	private static class SpoutBlockComponentSnapshot implements BlockComponentSnapshot {
		private final int x, y, z;
		private final Class<? extends BlockComponent> clazz;
		private final SerializableMap data;
		private SpoutBlockComponentSnapshot(int x, int y, int z, Class<? extends BlockComponent> clazz, SerializableMap data) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.clazz = clazz;
			this.data = data;
		}

		@Override
		public int getX() {
			return x;
		}

		@Override
		public int getY() {
			return y;
		}

		@Override
		public int getZ() {
			return z;
		}

		@Override
		public Class<? extends BlockComponent> getComponent() {
			return clazz;
		}

		@Override
		public SerializableMap getData() {
			return data;
		}
	}

	private static class BlockSnapshotProcedure implements TShortObjectProcedure<BlockComponent> {
		private final SpoutChunk chunk;
		private final ArrayList<BlockComponentSnapshot> snapshots = new ArrayList<BlockComponentSnapshot>();
		private BlockSnapshotProcedure(SpoutChunk chunk) {
			this.chunk = chunk;
		}

		@Override
		public boolean execute(short index, BlockComponent component) {
			int x = NibbleQuadHashed.key1(index) + chunk.getBlockX();
			int y = NibbleQuadHashed.key2(index) + chunk.getBlockY();
			int z = NibbleQuadHashed.key3(index) + chunk.getBlockZ();
			snapshots.add(new SpoutBlockComponentSnapshot(x, y, z, component.getClass(), component.getOwner().getData()));
			return true;
		}
	}
}
