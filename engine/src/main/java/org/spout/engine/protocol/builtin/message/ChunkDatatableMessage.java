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
package org.spout.engine.protocol.builtin.message;

import org.spout.api.Spout;
import org.spout.api.datatable.delta.DeltaMap;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.engine.world.SpoutChunk;

public class ChunkDatatableMessage extends DatatableMessage {
	final String world;
	final int x, y, z;

	public ChunkDatatableMessage(String world, int x, int y, int z, byte[] compressedData, DeltaMap.DeltaType type) {
		super(compressedData, type);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public ChunkDatatableMessage(SpoutChunk c) {
		this(c.getWorld().getName(), c.getX(), c.getY(), c.getZ(), c.getDataMap().getDeltaMap().serialize(), c.getDataMap().getDeltaMap().getType());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String getWorld() {
		return world;
	}

	public Chunk getChunk() {
		return Spout.getEngine().getWorld(world, true).getChunk(x, y, z);
	}
}
