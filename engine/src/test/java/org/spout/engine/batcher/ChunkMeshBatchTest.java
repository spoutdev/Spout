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
package org.spout.engine.batcher;

import org.junit.Test;

import org.spout.api.math.Vector3;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.world.SpoutChunkSnapshotModel;

import static org.junit.Assert.assertEquals;

/**
 * Testing of the ChunkMeshBatch.
 */
public class ChunkMeshBatchTest {
	@Test
	public void testGetBaseCoordinates() {
		Vector3 batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(0, 0, 0)));
		assertEquals(new Vector3(0, 0, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(-1, 0, 0)));
		assertEquals(new Vector3(-1, 0, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(-1, 5, 0)));
		assertEquals(new Vector3(-1, 0, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(1, 7, 0)));
		assertEquals(new Vector3(1, 0, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(1, 8, 0)));
		assertEquals(new Vector3(1, 8, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(1, -1, 0)));
		assertEquals(new Vector3(1, -1, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(1, -7, 0)));
		assertEquals(new Vector3(1, -1, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(1, -8, 0)));
		assertEquals(new Vector3(1, -1, 0), batchCoords);

		batchCoords = ChunkMeshBatchAggregator.getBaseFromChunkMesh(of(new Vector3(-4, -9, 0)));
		assertEquals(new Vector3(-4, -9, 0), batchCoords);
	}

	@Test
	public void testGetLocalCoordinates() {
		Vector3 chunkCoords = ChunkMeshBatchAggregator.getLocalCoordFromChunkMesh(of(new Vector3(0, 0, 0)));
		assertEquals(new Vector3(1, 1, 1), chunkCoords);

		chunkCoords = ChunkMeshBatchAggregator.getLocalCoordFromChunkMesh(of(new Vector3(-1, 0, 0)));
		assertEquals(new Vector3(1, 1, 1), chunkCoords);

		chunkCoords = ChunkMeshBatchAggregator.getLocalCoordFromChunkMesh(of(new Vector3(-1, -4, 0)));
		assertEquals(new Vector3(1, 4, 1), chunkCoords);

		chunkCoords = ChunkMeshBatchAggregator.getLocalCoordFromChunkMesh(of(new Vector3(-4, -1, 4)));
		assertEquals(new Vector3(1, 1, 1), chunkCoords);
	}

	private static ChunkMesh of(Vector3 of) {
		return new ChunkMesh(new SpoutChunkSnapshotModel(null, of.getFloorX(), of.getFloorY(), of.getFloorZ(), true, 0));
	}
}
