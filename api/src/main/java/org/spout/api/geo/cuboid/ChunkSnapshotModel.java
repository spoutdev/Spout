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
package org.spout.api.geo.cuboid;

public interface ChunkSnapshotModel {
	public int getX();

	public int getY();

	public int getZ();

	/**
	 * Gets if the chunk was unloaded.  Unload models only indicate an unload occurred and contain no data.
	 */
	public boolean isUnload();

	/**
	 * Gets the current center chunk of this model
	 */
	public ChunkSnapshot getCenter();

	/**
	 * Clears all references to live chunks and regions
	 */
	public void cleanUp();

	/**
	 * Gets the chunk at world chunk coordinates<br> Note: Coordinates must be within this model, or index out of bounds will be thrown.
	 *
	 * @param cx coordinate of the chunk
	 * @param cy coordinate of the chunk
	 * @param cz coordinate of the chunk
	 * @return The chunk, or null if not available
	 */
	public ChunkSnapshot getChunk(int cx, int cy, int cz);

	/**
	 * Gets the chunk at world block coordinates<br> Note: Coordinates must be within this model, or index out of bounds will be thrown.
	 *
	 * @param bx coordinate of the block
	 * @param by coordinate of the block
	 * @param bz coordinate of the block
	 * @return The chunk, or null if not available
	 */
	public ChunkSnapshot getChunkFromBlock(int bx, int by, int bz);
}
