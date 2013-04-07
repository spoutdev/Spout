/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.util.cuboid;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;

/**
 * This class implements a Cuboid buffer wrapper.  Each sub-buffer must be exactly one
 * chunk in size.
 */
public class LocalRegionChunkCuboidLightBufferWrapper<T extends CuboidLightBuffer> extends ChunkCuboidLightBufferWrapper<T> {
	
	private static final int SINGLE = Region.BLOCKS.SIZE;
	private static final int TRIPLE = SINGLE * 3;
	
	private final Region r;
	private final LoadOption loadOpt;
	private final short id;
	
	public LocalRegionChunkCuboidLightBufferWrapper(Region r, short id, LoadOption loadOpt) {
		this(r.getBlockX() - SINGLE, r.getBlockY() - SINGLE, r.getBlockZ() - SINGLE, TRIPLE, TRIPLE, TRIPLE, r, id, loadOpt);
	}
	
	private LocalRegionChunkCuboidLightBufferWrapper(int bx, int by, int bz, int sx, int sy, int sz, Region r, short id, LoadOption loadOpt) {
		super(bx, by, bz, sx, sy, sz, id);
		this.r = r;
		this.loadOpt = loadOpt;
		this.id = id;
	}
	
	protected T getLightBufferRaw(int x, int y, int z) {
		int cx = (x - baseX - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		int cy = (y - baseY - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		int cz = (z - baseZ - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		Chunk c = r.getLocalChunk(cx, cy, cz, loadOpt);
		if (c == null) {
			Spout.getLogger().info("No local chunk, " + cx + ", " + cy + ", " + cz + " block: " + x + ", " + y + ", " + z);
			return null;
		}
		@SuppressWarnings("unchecked")
		T buf = (T) c.getLightBuffer(id);
		if (buf == null) {
			Spout.getLogger().info("Chunk has no buffer");
			return null;
		}
		return buf;
	}
}
