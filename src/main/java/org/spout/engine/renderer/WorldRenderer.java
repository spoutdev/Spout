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
package org.spout.engine.renderer;

import gnu.trove.iterator.TLongObjectIterator;

import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.engine.SpoutClient;
import org.spout.engine.batcher.ChunkMeshBatch;

public class WorldRenderer {
	private final SpoutClient client;
	private RenderMaterial material;
	private TInt21TripleObjectHashMap<ChunkMeshBatch> chunkRenderers = new TInt21TripleObjectHashMap<ChunkMeshBatch>();

	public WorldRenderer(SpoutClient client) {
		this.client = client;
	}

	public void setup() {
		World world = client.getDefaultWorld();
		// World world = client.getWorld("world");
		material = (RenderMaterial) Spout.getFilesystem().getResource("material://Spout/resources/resources/materials/BasicMaterial.smt");

		for (int x = -3; x < 3; x++) {
			for (int y = 0; y < 8; y++) {
				for (int z = -3; z < 1; z++) {
					ChunkMeshBatch batch = new ChunkMeshBatch(material, world, x * ChunkMeshBatch.SIZE_X, y * ChunkMeshBatch.SIZE_Y, z * ChunkMeshBatch.SIZE_Z);
					chunkRenderers.put(x * ChunkMeshBatch.SIZE_X, y * ChunkMeshBatch.SIZE_Y, z * ChunkMeshBatch.SIZE_Z, batch);
					batch.update();
				}
			}
		}
		// GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void render() {
		material.getShader().setUniform("View", client.getActiveCamera().getView());
		material.getShader().setUniform("Projection", client.getActiveCamera().getProjection());

		renderChunks();
	}

	private void renderChunks() {
		TLongObjectIterator<ChunkMeshBatch> it = chunkRenderers.iterator();
		while (it.hasNext()) {
			it.advance();
			ChunkMeshBatch renderer = it.value();
			material.getShader().setUniform("Model", renderer.getTransform());

			// It's hard to look right
			// at the world baby
			// But here's my frustrum
			// so cull me maybe?
			if (client.getActiveCamera().getFrustum().intersects(renderer)) {
				it.value().render();
			}
		}
	}
}
