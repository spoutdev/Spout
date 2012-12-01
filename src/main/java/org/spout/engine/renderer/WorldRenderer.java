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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotRender;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.engine.SpoutClient;
import org.spout.engine.batcher.ChunkMeshBatchAggregator;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.world.SpoutWorld;

public class WorldRenderer {
	private final SpoutClient client;

	public static final long TIME_LIMIT = 2;

	private TreeMap<RenderMaterial,List<ChunkMeshBatchAggregator>> chunkRenderers = new TreeMap<RenderMaterial,List<ChunkMeshBatchAggregator>>();

	/**
	 * Store ChunkMeshBatchAggregator by BlockFace, RenderMaterial and ChunkMeshBatchAggregator position
	 */
	private TInt21TripleObjectHashMap<Map<RenderMaterial,ChunkMeshBatchAggregator>> chunkRenderersByPosition = new TInt21TripleObjectHashMap<Map<RenderMaterial,ChunkMeshBatchAggregator>>();

	private SpoutWorld currentWorld = null;
	private final BatchGeneratorTask batchGenerator = new BatchGeneratorTask();

	//Benchmark
	public long minUpdate = Long.MAX_VALUE,maxUpdate = Long.MIN_VALUE,sumUpdate = 0;
	public long minRender = Long.MAX_VALUE,maxRender = Long.MIN_VALUE,sumRender = 0;
	public long count = 0;

	public WorldRenderer(SpoutClient client) {
		this.client = client;
	}

	public void render() {
		count ++;
		if(count > 600){
			count = 0;
			sumUpdate = 0;
			sumRender = 0;
		}

		long time,start = System.currentTimeMillis();

		update(System.currentTimeMillis() + TIME_LIMIT);

		time = System.currentTimeMillis() - start;
		if(minUpdate > time)
			minUpdate = time;
		if(maxUpdate < time)
			maxUpdate = time;
		sumUpdate += time;

		start = System.currentTimeMillis();

		renderChunks();

		time = System.currentTimeMillis() - start;
		if(minRender > time)
			minRender = time;
		if(maxRender < time)
			maxRender = time;
		sumRender += time;
	}

	private final ConcurrentLinkedQueue<ChunkMesh> renderChunkMeshBatchQueue = new ConcurrentLinkedQueue<ChunkMesh>();

	private class BatchGeneratorTask{

		private ChunkMesh chunkMesh = null;
		private World world = null;

		private Iterator<Entry<RenderMaterial, BatchVertex>> it = null;

		private Entry<RenderMaterial, BatchVertex> data;
		private RenderMaterial material;

		public void run(final long limit) {

			if(it != null){

				while(it.hasNext()){
					data = it.next();
					material = data.getKey();

					handle( data.getValue(), limit);

					if( System.currentTimeMillis() > limit)
						return;

					material = null;
					data = null;
				}
				it = null;
				chunkMesh = null;
			}

			//Step 2 : Add ChunkMesh to ChunkMeshBatch
			while( (chunkMesh = renderChunkMeshBatchQueue.poll()) != null){
				world = chunkMesh.getWorld();

				if(world != currentWorld) //Some meshs can be keeped in the queue when you change of world
					continue;

				if(chunkMesh.isUnloaded()){
					cleanBatchAggregator(world,chunkMesh);
					chunkMesh = null;

					if( System.currentTimeMillis() > limit)
						return;

					continue;
				}

				it = chunkMesh.getMaterialsFaces().entrySet().iterator();
				while(it.hasNext()){
					data = it.next();
					material = data.getKey();

					handle( data.getValue(), limit);

					if( System.currentTimeMillis() > limit)
						return;

					material = null;
					data = null;
				}
				it = null;
				chunkMesh = null;
			}
		}

		private void handle(BatchVertex batchVertex, long limit){
			ChunkMeshBatchAggregator chunkMeshBatch = getBatchAggregator(chunkMesh.getChunkX(), chunkMesh.getChunkY(), chunkMesh.getChunkZ(), material);

			if(chunkMeshBatch==null){
				chunkMeshBatch = new ChunkMeshBatchAggregator(world,chunkMesh.getChunkX(), chunkMesh.getChunkY(), chunkMesh.getChunkZ(),material);
				addBatchAggregator(chunkMeshBatch);
			}

			chunkMeshBatch.setSubBatch(batchVertex);
			chunkMeshBatch.update();
		}

	}

	public void update(long limit){
		SpoutWorld world = (SpoutWorld) ((Client)Spout.getEngine()).getActivePlayer().getWorld();

		if(currentWorld != world){
			if(currentWorld != null)
				currentWorld.disableRenderQueue();
			if(world != null)
				world.enableRenderQueue();
			currentWorld = world;
		}

		batchGenerator.run(limit);
	}

	public void addMeshToBatchQueue(ChunkMesh mesh) {
		renderChunkMeshBatchQueue.add(mesh);
	}

	private void addBatchAggregator(ChunkMeshBatchAggregator batch) {
		// Add in chunkRenderers
		List<ChunkMeshBatchAggregator> list = chunkRenderers.get(batch.getMaterial());
		if(list == null){
			list = new ArrayList<ChunkMeshBatchAggregator>();
			chunkRenderers.put(batch.getMaterial(),list);
		}

		list.add(batch);

		//Add in chunkRenderersByPosition
		Map<RenderMaterial, ChunkMeshBatchAggregator> map = chunkRenderersByPosition.get(batch.getX(), batch.getY(), batch.getZ());
		if(map == null){
			map = new HashMap<RenderMaterial, ChunkMeshBatchAggregator>();
			chunkRenderersByPosition.put(batch.getX(), batch.getY(), batch.getZ(), map);
		}

		map.put(batch.getMaterial(),batch);
	}

	/**
	 * Remove all batch at the specified position
	 * @param world 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void cleanBatchAggregator(World world, ChunkMesh chunkMesh) {
		Map<RenderMaterial, ChunkMeshBatchAggregator> aggregatorPerMaterial =
				chunkRenderersByPosition.get(chunkMesh.getChunkX(), chunkMesh.getChunkY(), chunkMesh.getChunkZ());

		//Can be null if the thread receive a unload model of a model wich has been send previously to load be not done
		if(aggregatorPerMaterial != null){

			LinkedList<RenderMaterial> materialToRemove = new LinkedList<RenderMaterial>();

			for(Entry<RenderMaterial, ChunkMeshBatchAggregator> entry : aggregatorPerMaterial.entrySet()){

				RenderMaterial material = entry.getKey();
				ChunkMeshBatchAggregator batch = entry.getValue();

				if(batch.getWorld() != world)
					continue;

				List<ChunkMeshBatchAggregator> chunkRenderer = chunkRenderers.get(material);

				batch.finalize();

				//Clean chunkRenderers
				chunkRenderer.remove(batch);

				//Clean chunkRenderersByPosition
				materialToRemove.add(material);

				//Clean chunkRenderers
				if(chunkRenderer.isEmpty())
					chunkRenderers.remove(material);
			}

			//Clean chunkRenderersByPosition
			for(RenderMaterial material : materialToRemove)
				aggregatorPerMaterial.remove(material);

			if(aggregatorPerMaterial.isEmpty())
				chunkRenderersByPosition.remove(chunkMesh.getChunkX(), chunkMesh.getChunkY(), chunkMesh.getChunkZ());

		}
	}

	/**
	 * Gets the batch aggregator corresponding wuth the given mesh and material.
	 * @param mesh
	 * @param material
	 * @return
	 */
	private ChunkMeshBatchAggregator getBatchAggregator(int x, int y, int z, RenderMaterial material) {
		Map<RenderMaterial, ChunkMeshBatchAggregator> map = chunkRenderersByPosition.get( x, y, z);
		if( map == null )
			return null;
		return map.get(material);
	}

	int ocludedChunks = 0;
	int culledChunks = 0;
	int rended = 0;

	private void renderChunks() {
		ocludedChunks = 0;
		culledChunks = 0;
		rended = 0;

		/*TODO (maybe): We could optimise iteration and testing frustum on ChunkMeshBatch.
		 * If we sort ChunkMeshBatch by x, y and z, and test only cubo's vertice changing
		 * Example : while we test batch with same x coord, we don't need to test 
		 */

		for(Entry<RenderMaterial, List<ChunkMeshBatchAggregator>> entry : chunkRenderers.entrySet()){
			RenderMaterial material = entry.getKey();

			SnapshotRender snapshotRender = new SnapshotRender(material);
			material.preRender(snapshotRender);

			material.getShader().setUniform("View", client.getActiveCamera().getView());
			material.getShader().setUniform("Projection", client.getActiveCamera().getProjection());
			material.getShader().setUniform("Model", ChunkMeshBatchAggregator.model);

			for (ChunkMeshBatchAggregator renderer : entry.getValue()) {
				// It's hard to look right
				// at the world baby
				// But here's my frustrum
				// so cull me maybe?
				//System.out.println( renderer.getX() + "/" + renderer.getY() + "/" + renderer.getZ() + " : " + renderer.getBase() +  " : " + renderer.getSize());


				if (client.getActiveCamera().getFrustum().intersects(renderer)) {
					renderer.render(material);
					rended ++;
				} else {
					culledChunks++;
				}
			}

			material.postRender(snapshotRender);
		}
	}

	public int getOcluded() {
		return ocludedChunks;
	}

	public int getCulled() {
		return culledChunks;
	}

	public int getRended() {
		return rended;
	}

	public int getBatchWaiting() {
		return renderChunkMeshBatchQueue.size();
	}
}