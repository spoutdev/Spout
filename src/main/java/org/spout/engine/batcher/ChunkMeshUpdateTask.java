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

import org.spout.api.Spout;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;

public class ChunkMeshUpdateTask implements Runnable{

	private final ChunkMesh mesh;
	private final ChunkMeshBatch batch;
	
	public ChunkMeshUpdateTask(final ChunkMesh mesh, final ChunkMeshBatch batch){
		this.mesh = mesh;
		this.batch = batch;
		try {
			mesh.lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		SpoutSnapshotLock lock = (SpoutSnapshotLock) Spout.getEngine().getScheduler().getSnapshotLock();
		lock.coreReadLock("Generate mesh");
		try {
			mesh.update();
			batch.dirty = true;
		} finally {
			lock.coreReadUnlock("Generate mesh");
		}
		mesh.lock.release();
		batch.notifyGenerated();
	}

}
