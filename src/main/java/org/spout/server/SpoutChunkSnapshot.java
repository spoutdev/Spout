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
package org.spout.server;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.spout.api.entity.Entity;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.MaterialData;

public class SpoutChunkSnapshot extends ChunkSnapshot {
	/**
	 * The parent region that manages this chunk
	 */
	private final WeakReference<Region> parentRegion;

	/**
	 * The mask that should be applied to the x, y and z coords
	 */
	private final int coordMask;

	private final Set<WeakReference<Entity>> entities;

	private final short[] blockIds;

	private final short[] blockData;

	private final short[] blockLight;

	private final short[] skyLight;

	public SpoutChunkSnapshot(SpoutChunk chunk, short[] blockIds, short[] blockData, short[] blockLight, short[] skyLight, boolean entities) {
		super(chunk.getWorld(), chunk.getX(), chunk.getY(), chunk.getZ());
		coordMask = Chunk.CHUNK_SIZE - 1;
		parentRegion = new WeakReference<Region>(chunk.getRegion());
		if (entities) {
			Set<WeakReference<Entity>> liveEntities = new HashSet<WeakReference<Entity>>();
			for (Entity e : chunk.getLiveEntities()) {
				liveEntities.add(new WeakReference<Entity>(e));
			}
			this.entities = Collections.unmodifiableSet(liveEntities);
		} else {
			this.entities = Collections.emptySet();
		}
		this.blockIds = blockIds;
		this.blockData = blockData;
		this.blockLight = blockLight;
		this.skyLight = skyLight;
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		return MaterialData.getBlock(getBlockId(x, y, z), getBlockData(x, y, z));
	}

	@Override
	public short getBlockId(int x, int y, int z) {
		return blockIds[(y & coordMask) << 8 | (z & coordMask) << 4 | x & coordMask];
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		return blockData[(y & coordMask) << 8 | (z & coordMask) << 4 | x & coordMask];
	}

	@Override
	public short getSkyLight(int x, int y, int z) {
		return skyLight[(y & coordMask) << 8 | (z & coordMask) << 4 | x & coordMask];
	}

	@Override
	public short getBlockLight(int x, int y, int z) {
		return blockLight[(y & coordMask) << 8 | (z & coordMask) << 4 | x & coordMask];
	}

	@Override
	public Region getRegion() {
		return parentRegion.get();
	}

	@Override
	public Set<Entity> getEntities() {
		Set<Entity> entities = new HashSet<Entity>();
		for (WeakReference<Entity> ref : this.entities) {
			Entity e = ref.get();
			if (e != null) {
				entities.add(e);
			}
		}
		return entities;
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
	public short[] getBlockLight() {
		return blockLight;
	}

	@Override
	public short[] getSkyLight() {
		return skyLight;
	}
}
