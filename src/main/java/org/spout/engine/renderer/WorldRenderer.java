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
package org.spout.engine.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.math.Vector3;
import org.spout.api.render.BufferContainer;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotRender;
import org.spout.api.util.map.TInt21TripleObjectHashMap;

import org.spout.engine.batcher.ChunkMeshBatchAggregator;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.world.SpoutClientWorld;

public class WorldRenderer {
	public static final long TIME_LIMIT = 2;
	private final BatchGeneratorTask batchGenerator = new BatchGeneratorTask();
	private final ConcurrentLinkedQueue<ChunkMesh> renderChunkMeshBatchQueue = new ConcurrentLinkedQueue<ChunkMesh>();
	private final Queue<ChunkMeshBatchAggregator> toUpdate = new LinkedList<ChunkMeshBatchAggregator>();
	/**
	 * Store ChunkMeshBatchAggregator by BlockFace, RenderMaterial and ChunkMeshBatchAggregator position
	 */
	private final TInt21TripleObjectHashMap<Map<RenderMaterial, ChunkMeshBatchAggregator>> chunkRenderersByPosition = new TInt21TripleObjectHashMap<Map<RenderMaterial, ChunkMeshBatchAggregator>>();
	private final TreeMap<RenderMaterial, List<ChunkMeshBatchAggregator>> chunkRenderers = new TreeMap<RenderMaterial, List<ChunkMeshBatchAggregator>>();
	private SpoutClientWorld currentWorld = null;
	//Benchmark
	public int addedBatch, updatedBatch;

	public void update(long limit) {
		final SpoutClientWorld world = (SpoutClientWorld) ((Client) Spout.getEngine()).getWorld();

		if (currentWorld != world) {
			if (currentWorld != null) {
				currentWorld.disableRenderQueue();
			}
			if (world != null) {
				world.enableRenderQueue();
			}
			currentWorld = world;
		}

		batchGenerator.run(limit);
	}

	public void render() {
		long start = System.currentTimeMillis();

		update(start + TIME_LIMIT);

		renderChunks();
	}

	public void addMeshToBatchQueue(ChunkMesh mesh) {
		renderChunkMeshBatchQueue.add(mesh);
	}

	private void addBatchAggregator(ChunkMeshBatchAggregator batch) {
		// Add in chunkRenderers
		List<ChunkMeshBatchAggregator> list = chunkRenderers.get(batch.getMaterial());
		if (list == null) {
			list = new ArrayList<ChunkMeshBatchAggregator>();
			chunkRenderers.put(batch.getMaterial(), list);
		}

		list.add(batch);

		//Add in chunkRenderersByPosition

		Map<RenderMaterial, ChunkMeshBatchAggregator> map = chunkRenderersByPosition.get(batch.getX(), batch.getY(), batch.getZ());
		if (map == null) {
			map = new HashMap<RenderMaterial, ChunkMeshBatchAggregator>();
			chunkRenderersByPosition.put(batch.getX(), batch.getY(), batch.getZ(), map);
		}

		map.put(batch.getMaterial(), batch);
	}

	/**
	 * Remove all batch at the specified position
	 * @param world
	 * @param chunkMesh
	 */
	private void cleanBatchAggregator(World world, ChunkMesh chunkMesh) {
		Vector3 position = ChunkMeshBatchAggregator.getCoordFromChunkMesh(chunkMesh);
		Map<RenderMaterial, ChunkMeshBatchAggregator> aggregatorPerMaterial = chunkRenderersByPosition.get(position.getFloorX(), position.getFloorY(), position.getFloorZ());

		//Can be null if the thread receive a unload model of a model which has been send previously to load be not done
		if (aggregatorPerMaterial != null) {

			LinkedList<RenderMaterial> materialToRemove = new LinkedList<RenderMaterial>();

			for (Entry<RenderMaterial, ChunkMeshBatchAggregator> entry : aggregatorPerMaterial.entrySet()) {

				RenderMaterial material = entry.getKey();
				ChunkMeshBatchAggregator batch = entry.getValue();

				if (batch.getWorld() != world) {
					continue;
				}

				List<ChunkMeshBatchAggregator> chunkRenderer = chunkRenderers.get(material);

				batch.setSubBatch(null, chunkMesh.getChunkX(), chunkMesh.getChunkY(), chunkMesh.getChunkZ());

				if (!batch.isEmpty()) {
					continue;
				}

				batch.finalize();

				if (batch.isQueued()) {
					toUpdate.remove(batch);
				}

				//Clean chunkRenderers
				chunkRenderer.remove(batch);

				//Clean chunkRenderersByPosition
				materialToRemove.add(material);

				//Clean chunkRenderers
				if (chunkRenderer.isEmpty()) {
					chunkRenderers.remove(material);
				}
			}

			//Clean chunkRenderersByPosition
			for (RenderMaterial material : materialToRemove) {
				aggregatorPerMaterial.remove(material);
			}

			if (aggregatorPerMaterial.isEmpty()) {
				chunkRenderersByPosition.remove(position.getFloorX(), position.getFloorY(), position.getFloorZ());
			}
		}
	}

	private ChunkMeshBatchAggregator getBatchAggregator (
			ChunkMesh mesh, RenderMaterial material) {
		Vector3 position = ChunkMeshBatchAggregator.getCoordFromChunkMesh(mesh);
		Map<RenderMaterial, ChunkMeshBatchAggregator> map = chunkRenderersByPosition.get(position.getFloorX(), position.getFloorY(), position.getFloorZ());
		if (map == null) {
			return null;
		}
		return map.get(material);
	}

	int occludedChunks = 0;
	int culledChunks = 0;
	int renderedChunks = 0;
	int chunksToRender = 0;

	private void renderChunks() {
		occludedChunks = 0;
		culledChunks = 0;
		renderedChunks = 0;
		chunksToRender = 0;

		/*TODO (maybe): We could optimise iteration and testing frustum on ChunkMeshBatch.
		 * If we sort ChunkMeshBatch by x, y and z, and test only cubo's vertice changing
		 * Example : while we test batch with same x coord, we don't need to test
		 */

		for (Entry<RenderMaterial, List<ChunkMeshBatchAggregator>> entry : chunkRenderers.entrySet()) {
			RenderMaterial material = entry.getKey();

			SnapshotRender snapshotRender = new SnapshotRender(material);
			material.preRender(snapshotRender);
			final Client client = (Client) Spout.getEngine();
			final Camera camera = client.getPlayer().getType(Camera.class);
			material.getShader().setUniform("View", camera.getView());
			material.getShader().setUniform("Projection", camera.getProjection());
			material.getShader().setUniform("Model", ChunkMeshBatchAggregator.model);
			chunksToRender += entry.getValue().size();

			Iterator<ChunkMeshBatchAggregator> it = entry.getValue().iterator();
			ChunkMeshBatchAggregator renderer = null;

			while (it.hasNext()) {
				renderer = it.next();

				if (!renderer.isReady()) {
					continue;
				}

				renderer.preRender();

				// It's hard to look right
				// at the world baby
				// But here's my frustrum
				// so cull me maybe?

				if (camera.getFrustum().intersects(renderer)) {
					renderer.render(material);
					renderedChunks++;
				} else {
					culledChunks++;
				}
			}

			if (renderer != null && renderer.isReady()) {
				renderer.postRender();
			}

			material.postRender(snapshotRender);
		}
	}

	public int getOccludedChunks() {
		return occludedChunks;
	}

	public int getCulledChunks() {
		return culledChunks;
	}

	public int getRenderedChunks() {
		return renderedChunks;
	}

	public int getTotalChunks() {
		return chunksToRender;
	}

	public int getBatchWaiting() {
		return renderChunkMeshBatchQueue.size();
	}

	private class BatchGeneratorTask {
		private World world = null;

		public void run(final long limit) {
			addedBatch = 0;
			updatedBatch = 0;

			ChunkMesh mesh;

			// Add ChunkMesh to ChunkMeshBatch
			while ((mesh = renderChunkMeshBatchQueue.poll()) != null) {
				world = mesh.getWorld();

				if (world != currentWorld) {
					continue;
				}

				if (mesh.isUnloaded()) {
					cleanBatchAggregator(world, mesh);

					if (System.currentTimeMillis() > limit) {
						return;
					}

					continue;
				}

				final Iterator<Entry<RenderMaterial, BufferContainer>> it = mesh.getMaterialsFaces().entrySet().iterator();
				while (it.hasNext()) {
					final Entry<RenderMaterial, BufferContainer> data = it.next();
					final RenderMaterial material = data.getKey();

					handle(mesh, material, data.getValue(), limit);

					if (System.currentTimeMillis() > limit) {
						return;
					}
				}
			}

			while (!toUpdate.isEmpty()) {
				final ChunkMeshBatchAggregator batch = toUpdate.peek();

				if (batch.update()) {
					toUpdate.poll();
					batch.setQueued(false);
				}

				updatedBatch++;

				if (System.currentTimeMillis() > limit) {
					return;
				}
			}
		}

		private void handle(ChunkMesh mesh, RenderMaterial material, BufferContainer batchVertex, long limit) {
			ChunkMeshBatchAggregator chunkMeshBatch = getBatchAggregator(mesh, material);

			if (chunkMeshBatch == null) {
				Vector3 base = ChunkMeshBatchAggregator.getBaseFromChunkMesh(mesh);
				chunkMeshBatch = new ChunkMeshBatchAggregator(world, base.getFloorX(), base.getFloorY(), base.getFloorZ(), material);
				addBatchAggregator(chunkMeshBatch);
			}

			chunkMeshBatch.setSubBatch(batchVertex, mesh.getChunkX(), mesh.getChunkY(), mesh.getChunkZ());

			if (!chunkMeshBatch.isQueued()) {
				addedBatch++;
				toUpdate.add(chunkMeshBatch);
				chunkMeshBatch.setQueued(true);
			}
		}
	}
}