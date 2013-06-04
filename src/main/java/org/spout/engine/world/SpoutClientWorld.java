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

import java.util.List;
import java.util.UUID;

import org.spout.api.event.Cause;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.ClientWorld;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringMap;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;

import org.spout.engine.SpoutClient;

/**
 * A dummy world used for the client
 */
public class SpoutClientWorld extends SpoutWorld implements ClientWorld {
	public SpoutClientWorld(String name, SpoutClient client, UUID uid, StringMap itemMap, StringMap lightingMap) {
		super(name, client, 0, 0, null, uid, itemMap, lightingMap);
	}

	@Override
	public void addChunk(ChunkSnapshot c) {
		addChunk(c.getX(), c.getY(), c.getZ(), c.getBlockIds(), c.getBlockData(), c.getBiomeManager());
	}

	@Override
	public void addChunk(int x, int y, int z, short[] blockIds, short[] blockData, BiomeManager biomes) {
		getRegionFromBlock(x, y, z, LoadOption.NO_LOAD).addChunk(x, y, z, blockIds, blockData, biomes);
	}

	@Override
	public void unload(boolean save) {
		throw new UnsupportedOperationException("Client is not allowed to unload worlds");
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException("Client is not allowed to save worlds");
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to reset dynamic block");
	}

	@Override
	public void resetDynamicBlocks(Chunk c) {
		throw new UnsupportedOperationException("Client is not allowed to reset dynamic blocks");
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to sync and reset dynamic blocks");
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, boolean exclusive) {
		throw new UnsupportedOperationException("Client is not allowed to queue dynamic update");
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, boolean exclusive) {
		throw new UnsupportedOperationException("Client is not allowed to queue dynamic update");
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, boolean exclusive) {
		throw new UnsupportedOperationException("Client is not allowed to queue dynamic update");
	}

	@Override
	public void copySnapshotRun() {
		throw new UnsupportedOperationException("Client is not allowed to invoke a snapshot");
	}

	@Override
	public void startTickRun(int stage, long delta) {
		throw new UnsupportedOperationException("Client is not allowed to execute a world tick");
	}

	@Override
	public long getSeed() {
		throw new UnsupportedOperationException("Client is not allowed to retrieve the world seed");
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to update block physics");
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range) {
		throw new UnsupportedOperationException("Client is not allowed to queue block physics");
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial) {
		throw new UnsupportedOperationException("Client is not allowed to queue block physics");
	}

	@Override
	public void removeColumn(int x, int z, SpoutColumn column) {
		throw new UnsupportedOperationException("Client is not allowed to remove a column");
	}

	@Override
	public SpoutColumn setIfNotGenerated(int x, int z, int[][] heightMap) {
		throw new UnsupportedOperationException("Client is not allowed to set modify columns");
	}

	@Override
	public SpoutColumn loadColumn(int x, int z) {
		throw new UnsupportedOperationException("Client is not allowed to load columns");
	}

	@Override
	public SpoutColumn setColumn(int x, int z, SpoutColumn col) {
		throw new UnsupportedOperationException("Client is not allowed to set a column");
	}

	@Override
	public void saveChunk(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to save a chunk");
	}

	@Override
	public void unloadChunk(int x, int y, int z, boolean save) {
		throw new UnsupportedOperationException("Client is not allowed to unload a chunk");
	}

	@Override
	protected void lockChunks(SpoutChunk[][][] chunks) {
		throw new UnsupportedOperationException("Client is not allowed to lock chunks");
	}

	@Override
	protected void unlockChunks(SpoutChunk[][][] chunks) {
		throw new UnsupportedOperationException("Client is not allowed to unlock chunks");
	}

	@Override
	public boolean commitCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		throw new UnsupportedOperationException("Client is not allowed to commit a cuboid");
	}

	@Override
	protected boolean commitCuboid(SpoutChunk[][][] chunks, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		throw new UnsupportedOperationException("Client is not allowed to commit a cuboid");
	}

	@Override
	public void runPhysics(int sequence) {
		throw new UnsupportedOperationException("Client is not allowed to run physics");
	}

	@Override
	public void runLighting(int sequence) {
		throw new UnsupportedOperationException("Client is not allowed to run lighting");
	}

	@Override
	public void runDynamicUpdates(long time, int sequence) {
		throw new UnsupportedOperationException("Client is not allowed to run dynamic updates");
	}

	@Override
	public void queueChunksForGeneration(List<Vector3> chunks) {
		throw new UnsupportedOperationException("Client is not allowed to queue chunks for generation");
	}

	@Override
	public void queueChunkForGeneration(Vector3 chunk) {
		throw new UnsupportedOperationException("Client is not allowed to queue chunks for generation");
	}
}
