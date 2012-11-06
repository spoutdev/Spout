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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.Spout;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.geo.World;
import org.spout.api.material.block.BlockFace;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.engine.SpoutClient;
import org.spout.engine.batcher.ChunkMeshBatch;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.mesh.ComposedMesh;
import org.spout.engine.mesh.CubeMesh;
import org.spout.engine.world.SpoutWorld;

public class WorldRenderer {
	private final SpoutClient client;

	private TreeMap<RenderMaterial,List<ChunkMeshBatch>> chunkRenderers = new TreeMap<RenderMaterial,List<ChunkMeshBatch>>();
	private TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatch>>> chunkRenderersByPosition = new TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatch>>>();

	public static HashMap<String,CubeMesh> blocksMesh = new HashMap<String, CubeMesh>();
	public static CubeMesh defaultMesh = null;

	private World world; // temp
	private final BatchGeneratorTask batchGenerator = new BatchGeneratorTask();

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
		final long start = System.currentTimeMillis();
		update();

		renderChunks();

		long time = System.currentTimeMillis() - start;
		if(time > 10) // -> 1000 / 60 = 16
			System.out.println("Worldrender take " + time);
	}

	public void setupWorld(){
		world = client.getDefaultWorld();
		if( world != null )
			((SpoutWorld)world).enableRenderQueue();
	}

	private final ConcurrentLinkedQueue<ChunkMesh> renderChunkMeshBatchQueue = new ConcurrentLinkedQueue<ChunkMesh>();

	private class BatchGeneratorTask implements Runnable {

		private static final long TIME_LIMIT = 2;

		private ChunkMesh chunkMesh = null;

		private Iterator<Entry<RenderMaterial, Map<BlockFace, ComposedMesh>>> it = null;

		private Entry<RenderMaterial, Map<BlockFace, ComposedMesh>> data;
		private RenderMaterial material;
		private Iterator<Entry<BlockFace, ComposedMesh>> it2 = null;

		public void run() {
			final long start = System.currentTimeMillis();

			//Execute previous unfinished chunkMesh
			//if(chunkMesh != null){
			if(it2 != null){
				//RenderMaterial material = data.getKey();
				while(it2.hasNext()){
					Entry<BlockFace, ComposedMesh> entry = it2.next();
					handle(entry.getKey(),entry.getValue());

					if( System.currentTimeMillis() - start > TIME_LIMIT)
						return;
				}
				it2 = null;
				material = null;
				data = null;
			}

			if(it != null){
				while(it.hasNext()){
					data = it.next();
					material = data.getKey();
					it2 = data.getValue().entrySet().iterator();

					while(it2.hasNext()){
						Entry<BlockFace, ComposedMesh> entry = it2.next();
						handle(entry.getKey(),entry.getValue());

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

				if(chunkMesh.isUnloaded()){
					cleanChunkMeshBatchByBatchPosition(chunkMesh);
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
						handle(entry.getKey(), entry.getValue());

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


		private void handle(BlockFace face, ComposedMesh mesh){
			ChunkMeshBatch chunkMeshBatch = getChunkMeshBatchByBatchPosition(chunkMesh, face, material);

			if(chunkMeshBatch==null){
				chunkMeshBatch = new ChunkMeshBatch(world,
						chunkMesh.getX() * ChunkMesh.SPLIT_X + chunkMesh.getSubX(),
						chunkMesh.getY() * ChunkMesh.SPLIT_Y + chunkMesh.getSubY(),
						chunkMesh.getZ() * ChunkMesh.SPLIT_Z + chunkMesh.getSubZ(),
						face, material);
				addChunkMeshBatch(chunkMeshBatch);
			}

			chunkMeshBatch.setMesh(mesh);
			chunkMeshBatch.update(); // One chunk in batch only
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

	private void addChunkMeshBatch(ChunkMeshBatch batch) {
		List<ChunkMeshBatch> list = chunkRenderers.get(batch.getMaterial());
		if(list == null){
			list = new ArrayList<ChunkMeshBatch>();
			chunkRenderers.put(batch.getMaterial(),list);
		}
		list.add(batch);

		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatch>> map = chunkRenderersByPosition.get(batch.getSubX(), batch.getSubY(), batch.getSubZ());
		if(map == null){
			map = new HashMap<RenderMaterial, Map<BlockFace, ChunkMeshBatch>>();
			chunkRenderersByPosition.put(batch.getSubX(), batch.getSubY(), batch.getSubZ(), map);
		}

		Map<BlockFace, ChunkMeshBatch> map2 = map.get(batch.getMaterial());
		if( map2 == null ){
			map2 = new HashMap<BlockFace, ChunkMeshBatch>();
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
	private void cleanChunkMeshBatchByBatchPosition(ChunkMesh mesh) {
		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatch>> chunkRenderersByMaterial = chunkRenderersByPosition.remove(
				mesh.getX() * ChunkMesh.SPLIT_X + mesh.getSubX(),
				mesh.getY() * ChunkMesh.SPLIT_Y + mesh.getSubY(),
				mesh.getZ() * ChunkMesh.SPLIT_Z + mesh.getSubZ());
		
		//Can be null if the thread receive a unload model of a model wich has been send previously to load be not done
		if(chunkRenderersByMaterial != null){
			
			for(Entry<RenderMaterial, Map<BlockFace, ChunkMeshBatch>> entry : chunkRenderersByMaterial.entrySet()){
				
				RenderMaterial material = entry.getKey();
				Collection<ChunkMeshBatch> batchs = entry.getValue().values();
				
				for(ChunkMeshBatch batch : batchs)
					batch.finalize();

				List<ChunkMeshBatch> list = chunkRenderers.get(material);

				list.removeAll(batchs);
				if(list.isEmpty())
					chunkRenderers.remove(material);
			}
		}
	}

	/**
	 * Gets the chunk mesh batch corresponding with the given chunk.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private ChunkMeshBatch getChunkMeshBatchByBatchPosition(ChunkMesh mesh, BlockFace face, RenderMaterial material) {
		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatch>> map = chunkRenderersByPosition.get(
				mesh.getX() * ChunkMesh.SPLIT_X + mesh.getSubX(),
				mesh.getY() * ChunkMesh.SPLIT_Y + mesh.getSubY(),
				mesh.getZ() * ChunkMesh.SPLIT_Z + mesh.getSubZ());
		if( map == null )
			return null;
		Map<BlockFace, ChunkMeshBatch> map2 = map.get(material);
		if( map2 == null )
			return null;
		return map2.get(face);
	}
	
	int ocludedChunks = 0;
	int culledChunks = 0;

	private void renderChunks() {
		int x = client.getActivePlayer().getChunk().getX();
		int y = client.getActivePlayer().getChunk().getY();
		int z = client.getActivePlayer().getChunk().getZ();
		
		ocludedChunks = 0;
		culledChunks = 0;
		
		for(Entry<RenderMaterial, List<ChunkMeshBatch>> entry : chunkRenderers.entrySet()){
			RenderMaterial material = entry.getKey();
			material.getShader().setUniform("View", client.getActiveCamera().getView());
			material.getShader().setUniform("Projection", client.getActiveCamera().getProjection());
			for (ChunkMeshBatch renderer : entry.getValue()) {

				if(renderer.getY() > y && renderer.getFace() == BlockFace.TOP){
					ocludedChunks++;
					continue;
				}
				if(renderer.getY() < y && renderer.getFace() == BlockFace.BOTTOM){
					ocludedChunks++;
					continue;
				}

				if(renderer.getX() > x && renderer.getFace() == BlockFace.SOUTH){
					ocludedChunks++;
					continue;
				}
				if(renderer.getX() < x && renderer.getFace() == BlockFace.NORTH){
					ocludedChunks++;
					continue;
				}

				if(renderer.getZ() > z && renderer.getFace() == BlockFace.WEST){
					ocludedChunks++;
					continue;
				}
				if(renderer.getZ() < z && renderer.getFace() == BlockFace.EAST){
					ocludedChunks++;
					continue;
				}

				material.getShader().setUniform("Model", renderer.getTransform());

				// It's hard to look right
				// at the world baby
				// But here's my frustrum
				// so cull me maybe?
				//if (client.getActiveCamera().getFrustum().intersects(renderer)) {
				renderer.render(material);
				/*} else {
				culledChunks++;
			}*/
			}
		}

		/*if( ocludedChunks > 0)
			System.out.println("Ocluded facechunk : " + ocludedChunks);*/
	}

	public int getChunkRenderersSize(){
		int i = 0;
		for(List<ChunkMeshBatch> list : chunkRenderers.values())
			i += list.size();
		return i;
	}

	public int getOcluded() {
		return ocludedChunks;
	}

	public int getCulled() {
		return culledChunks;
	}
}