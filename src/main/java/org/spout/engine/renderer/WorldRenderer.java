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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.Spout;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.geo.World;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.engine.SpoutClient;
import org.spout.engine.batcher.ChunkMeshBatch;
import org.spout.engine.batcher.ChunkMeshBatchAggregator;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.mesh.ComposedMesh;
import org.spout.engine.mesh.CubeMesh;
import org.spout.engine.world.SpoutWorld;

public class WorldRenderer {
	private final SpoutClient client;

	public static final long TIME_LIMIT = 2;

	//private TreeMap<RenderMaterial,List<ChunkMeshBatch>> chunkRenderers = new TreeMap<RenderMaterial,List<ChunkMeshBatch>>();
	//private TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatch>>> chunkRenderersByPosition = new TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatch>>>();

	private TreeMap<RenderMaterial,List<ChunkMeshBatchAggregator>> chunkRenderers = new TreeMap<RenderMaterial,List<ChunkMeshBatchAggregator>>();
	
	/**
	 * Store ChunkMeshBatchAggregator by BlockFace, RenderMaterial and ChunkMeshBatchAggregator position
	 */
	private TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatchAggregator>>> chunkRenderersByPosition = new TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatchAggregator>>>();
	private List<ChunkMeshBatchAggregator> dirties = new LinkedList<ChunkMeshBatchAggregator>();
	
	public static HashMap<String,CubeMesh> blocksMesh = new HashMap<String, CubeMesh>();
	public static CubeMesh defaultMesh = null;

	private World world; // temp
	private final BatchGeneratorTask batchGenerator = new BatchGeneratorTask();

	public long minUpdate = Long.MAX_VALUE,maxUpdate = Long.MIN_VALUE,sumUpdate = 0;
	public long minRender = Long.MAX_VALUE,maxRender = Long.MIN_VALUE,sumRender = 0;
	public long count = 0;
	
	public WorldRenderer(SpoutClient client) {
		this.client = client;
	}

	public void setup() {
		defaultMesh = (CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/cube.obj");
		blocksMesh.put("Stone",(CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/stone.obj"));
		blocksMesh.put("Grass",(CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/grass.obj"));
		blocksMesh.put("Dirt",(CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/dirt.obj"));

		setupWorld();

		//Enable(GL11.GL_CULL_FACE);
	}

	public void render() {
		count ++;
		if(count > 600){
			count = 0;
			sumUpdate = 0;
			sumRender = 0;
		}
		
		long time,start = System.currentTimeMillis();
		
		update();

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

	public void setupWorld(){
		world = client.getDefaultWorld();
		if( world != null )
			((SpoutWorld)world).enableRenderQueue();
	}

	private final ConcurrentLinkedQueue<ChunkMesh> renderChunkMeshBatchQueue = new ConcurrentLinkedQueue<ChunkMesh>();

	private class BatchGeneratorTask implements Runnable {

		private ChunkMesh chunkMesh = null;

		private Iterator<Entry<RenderMaterial, Map<BlockFace, ComposedMesh>>> it = null;

		private Entry<RenderMaterial, Map<BlockFace, ComposedMesh>> data;
		private RenderMaterial material;
		private Iterator<Entry<BlockFace, ComposedMesh>> it2 = null;

		public void run() {
			final long start = System.currentTimeMillis();
			
			batch(start);

			//Execute previous unfinished chunkMesh
			//if(chunkMesh != null){
			if(it2 != null){
				Vector3 position = ChunkMeshBatchAggregator.getCoordFromChunkMesh(chunkMesh);
				//RenderMaterial material = data.getKey();
				while(it2.hasNext()){
					Entry<BlockFace, ComposedMesh> entry = it2.next();
					handle(position, entry.getKey(),entry.getValue(), start);

					if( System.currentTimeMillis() - start > TIME_LIMIT)
						return;
				}
				it2 = null;
				material = null;
				data = null;
			}

			if(it != null){
				Vector3 position = ChunkMeshBatchAggregator.getCoordFromChunkMesh(chunkMesh);
				while(it.hasNext()){
					data = it.next();
					material = data.getKey();
					it2 = data.getValue().entrySet().iterator();

					while(it2.hasNext()){
						Entry<BlockFace, ComposedMesh> entry = it2.next();
						handle(position, entry.getKey(),entry.getValue(), start);

						if( System.currentTimeMillis() - start > TIME_LIMIT)
							return;
					}

					it2 = null;
					material = null;
					data = null;
				}
				it = null;
				chunkMesh = null;
			}
			//}

			//Step 2 : Add ChunkMesh to ChunkMeshBatch
			while( (chunkMesh = renderChunkMeshBatchQueue.poll()) != null){
				Vector3 position = ChunkMeshBatchAggregator.getCoordFromChunkMesh(chunkMesh);

				if(chunkMesh.isUnloaded()){
					cleanBatchAggregator(position,chunkMesh);
					chunkMesh = null;
					continue;
				}

				it = chunkMesh.getMaterialsFaces().entrySet().iterator();
				while(it.hasNext()){
					data = it.next();
					material = data.getKey();

					it2 = data.getValue().entrySet().iterator();

					while(it2.hasNext()){
						Entry<BlockFace, ComposedMesh> entry = it2.next();
						handle(position, entry.getKey(), entry.getValue(), start);

						if( System.currentTimeMillis() - start > TIME_LIMIT)
							return;
					}

					it2 = null;
					material = null;
					data = null;
				}
				it = null;
				chunkMesh = null;
			}
		}


		private void handle(Vector3 position, BlockFace face, ComposedMesh mesh, long start){
			ChunkMeshBatchAggregator chunkMeshBatch = getBatchAggregator(position, face, material);

			if(chunkMeshBatch==null){
				chunkMeshBatch = new ChunkMeshBatchAggregator(world,position.getFloorX(),position.getFloorY(),position.getFloorZ(),
						face, material);
				addBatchAggregator(chunkMeshBatch);
			}

			chunkMeshBatch.setSubBatch(chunkMesh.getSubX(),chunkMesh.getSubY(),chunkMesh.getSubZ(),mesh);
			dirties.add(chunkMeshBatch);
			batch(start);
		}
		
		private void batch(long start){
			while(!dirties.isEmpty()){
				ChunkMeshBatchAggregator batch = dirties.remove(0);
				
				if(!batch.update(start))
					dirties.add(batch);
				
				if( System.currentTimeMillis() - start > TIME_LIMIT)
					return;
			}
		}

	}

	public void update(){
		if( world == null ){
			setupWorld();
			if( world == null )
				return;
		}

		batchGenerator.run();
	}

	public void addMeshToBatchQueue(ChunkMesh mesh) {
		renderChunkMeshBatchQueue.add(mesh);
	}

	private void addBatchAggregator(ChunkMeshBatchAggregator batch) {
		List<ChunkMeshBatchAggregator> list = chunkRenderers.get(batch.getMaterial());
		if(list == null){
			list = new ArrayList<ChunkMeshBatchAggregator>();
			chunkRenderers.put(batch.getMaterial(),list);
		}
		list.add(batch);

		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatchAggregator>> map = chunkRenderersByPosition.get(batch.getX(), batch.getY(), batch.getZ());
		if(map == null){
			map = new HashMap<RenderMaterial, Map<BlockFace, ChunkMeshBatchAggregator>>();
			chunkRenderersByPosition.put(batch.getX(), batch.getY(), batch.getZ(), map);
		}

		Map<BlockFace, ChunkMeshBatchAggregator> map2 = map.get(batch.getMaterial());
		if( map2 == null ){
			map2 = new HashMap<BlockFace, ChunkMeshBatchAggregator>();
			map.put(batch.getMaterial(), map2);
		}

		map2.put(batch.getFace(), batch);
	}

	/**
	 * Remove all batch at the specified position
	 * @param x
	 * @param y
	 * @param z
	 */
	private void cleanBatchAggregator(Vector3 position, ChunkMesh chunkMesh) {
		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatchAggregator>> aggregatorPerMaterial =
				chunkRenderersByPosition.get(position.getFloorX(),position.getFloorY(),position.getFloorZ());

		//Can be null if the thread receive a unload model of a model wich has been send previously to load be not done
		if(aggregatorPerMaterial != null){
			
			LinkedList<RenderMaterial> materialToRemove = new LinkedList<RenderMaterial>();
			
			for(Entry<RenderMaterial, Map<BlockFace, ChunkMeshBatchAggregator>> entry : aggregatorPerMaterial.entrySet()){

				RenderMaterial material = entry.getKey();
				LinkedList<BlockFace> faceToRemove = new LinkedList<BlockFace>();
				List<ChunkMeshBatchAggregator> chunkRenderer = chunkRenderers.get(material);

				for(Entry<BlockFace, ChunkMeshBatchAggregator> entry2 : entry.getValue().entrySet()){

					BlockFace face = entry2.getKey();
					ChunkMeshBatchAggregator batch = entry2.getValue();
					batch.setSubBatch(chunkMesh.getSubX(), chunkMesh.getSubY(), chunkMesh.getSubZ(), null);
					if(batch.isEmpty()){
						batch.finalize();
						
						//Clean dirties
						dirties.remove(batch);
						
						//Clean chunkRenderers
						chunkRenderer.remove(batch);
						
						//Clean chunkRenderersByPosition
						faceToRemove.add(face);
					}
				}

				//Clean chunkRenderersByPosition
				for(BlockFace face : faceToRemove)
					entry.getValue().remove(face);

				if(entry.getValue().isEmpty())
					materialToRemove.add(material);
				
				//Clean chunkRenderers
				if(chunkRenderer.isEmpty())
					chunkRenderers.remove(material);
			}
			
			//Clean chunkRenderersByPosition
			for(RenderMaterial material : materialToRemove)
				aggregatorPerMaterial.remove(material);
			
			if(aggregatorPerMaterial.isEmpty())
				chunkRenderersByPosition.remove(position.getFloorX(),position.getFloorY(),position.getFloorZ());
			
		}
	}
	
	/**
	 * Gets the batch aggregator corresponding wuth the given mesh, face and material.
	 * @param mesh
	 * @param face
	 * @param material
	 * @return
	 */
	private ChunkMeshBatchAggregator getBatchAggregator(Vector3 position, BlockFace face, RenderMaterial material) {
		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatchAggregator>> map = chunkRenderersByPosition.get( position.getFloorX(), position.getFloorY(), position.getFloorZ());
		if( map == null )
			return null;
		Map<BlockFace, ChunkMeshBatchAggregator> map2 = map.get(material);
		if( map2 == null )
			return null;
		return map2.get(face);
	}
	
	int ocludedChunks = 0;
	int culledChunks = 0;
	int rended = 0;

	private void renderChunks() {
		int minX = (int) ((Math.floor((float)client.getActivePlayer().getChunk().getX() * ChunkMesh.SPLIT_X) / ChunkMeshBatchAggregator.SIZE_X) * ChunkMeshBatchAggregator.SIZE_X);
		int minY = (int) ((Math.floor((float)client.getActivePlayer().getChunk().getY() * ChunkMesh.SPLIT_Y) / ChunkMeshBatchAggregator.SIZE_Y) * ChunkMeshBatchAggregator.SIZE_Y);
		int minZ = (int) ((Math.floor((float)client.getActivePlayer().getChunk().getZ() * ChunkMesh.SPLIT_Z) / ChunkMeshBatchAggregator.SIZE_Z) * ChunkMeshBatchAggregator.SIZE_Z);

		int maxX = (int) ((Math.floor((float)client.getActivePlayer().getChunk().getX() * ChunkMesh.SPLIT_X + ChunkMesh.SPLIT_X) / ChunkMeshBatchAggregator.SIZE_X) * ChunkMeshBatchAggregator.SIZE_X + ChunkMeshBatchAggregator.SIZE_X);
		int maxY = (int) ((Math.floor((float)client.getActivePlayer().getChunk().getY() * ChunkMesh.SPLIT_Y + ChunkMesh.SPLIT_Y) / ChunkMeshBatchAggregator.SIZE_Y) * ChunkMeshBatchAggregator.SIZE_Y + ChunkMeshBatchAggregator.SIZE_Y);
		int maxZ = (int) ((Math.floor((float)client.getActivePlayer().getChunk().getZ() * ChunkMesh.SPLIT_Z + ChunkMesh.SPLIT_Z) / ChunkMeshBatchAggregator.SIZE_Z) * ChunkMeshBatchAggregator.SIZE_Z + ChunkMeshBatchAggregator.SIZE_Z);
		
		ocludedChunks = 0;
		culledChunks = 0;
		rended = 0;
		
		for(Entry<RenderMaterial, List<ChunkMeshBatchAggregator>> entry : chunkRenderers.entrySet()){
			RenderMaterial material = entry.getKey();
			material.getShader().setUniform("View", client.getActiveCamera().getView());
			material.getShader().setUniform("Projection", client.getActiveCamera().getProjection());
			for (ChunkMeshBatchAggregator renderer : entry.getValue()) {

				if(renderer.getFace() == BlockFace.TOP){
					if(renderer.getY()>maxY){
						ocludedChunks++;
						continue;
					}
				}else if(renderer.getFace() == BlockFace.BOTTOM){
					if(renderer.getY()<minY){
						ocludedChunks++;
						continue;
					}
				}else if(renderer.getFace() == BlockFace.SOUTH){
					if(renderer.getX()>maxX){
						ocludedChunks++;
						continue;
					}
				}else if(renderer.getFace() == BlockFace.NORTH){
					if(renderer.getX()<minX){
						ocludedChunks++;
						continue;
					}
				}else if(renderer.getFace() == BlockFace.WEST){
					if(renderer.getZ()>maxZ){
						ocludedChunks++;
						continue;
					}
				}else if(renderer.getFace() == BlockFace.EAST){
					if(renderer.getZ()<minZ){
						ocludedChunks++;
						continue;
					}
				}

				material.getShader().setUniform("Model", renderer.getTransform());

				// It's hard to look right
				// at the world baby
				// But here's my frustrum
				// so cull me maybe?
				//if (client.getActiveCamera().getFrustum().intersects(renderer)) {
				rended += renderer.render(material);
				/*} else {
				culledChunks++;
			}*/
			}
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
	
	public int getBatchDirties() {
		return dirties.size();
	}
}