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
package org.spout.engine.batcher;

import org.junit.Test;
import org.spout.api.math.Vector3;
import static org.junit.Assert.assertEquals;

/**
 * Testing of the ChunkMeshBatch.
 */
public class ChunkMeshBatchTest {
	@Test
	public void testGetBatchCoordinates() {
		Vector3 batchCoords = ChunkMeshBatch.getBatchCoordinates(new Vector3(0, 0, 0));
		assertEquals(new Vector3(0, 0, 0), batchCoords);

		batchCoords = ChunkMeshBatch.getBatchCoordinates(new Vector3(-1, 0, 0));
		assertEquals(new Vector3(-1, 0, 0), batchCoords);

		batchCoords = ChunkMeshBatch.getBatchCoordinates(new Vector3(-4, 0, 0));
		assertEquals(new Vector3(-2, 0, 0), batchCoords);

		batchCoords = ChunkMeshBatch.getBatchCoordinates(new Vector3(-4, -1, 4));
		assertEquals(new Vector3(-2, -1, 1), batchCoords);
	}

	@Test
	public void testGetChunkCoordinates() {
		Vector3 chunkCoords = ChunkMeshBatch.getChunkCoordinates(new Vector3(0, 0, 0));
		assertEquals(new Vector3(0, 0, 0), chunkCoords);

		chunkCoords = ChunkMeshBatch.getChunkCoordinates(new Vector3(-1, 0, 0));
		assertEquals(new Vector3(-3, 0, 0), chunkCoords);

		chunkCoords = ChunkMeshBatch.getChunkCoordinates(new Vector3(-4, 0, 0));
		assertEquals(new Vector3(-12, 0, 0), chunkCoords);

		chunkCoords = ChunkMeshBatch.getChunkCoordinates(new Vector3(-4, -1, 4));
		assertEquals(new Vector3(-12, -1, 12), chunkCoords);
	}
}
