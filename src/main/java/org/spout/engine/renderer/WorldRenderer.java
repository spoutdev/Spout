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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.math.Vector3;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.engine.SpoutClient;
import org.spout.engine.batcher.ChunkMeshBatch;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.world.SpoutChunkSnapshotModel;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

public class WorldRenderer {
	private final SpoutClient client;
	private RenderMaterial material;

	private List<ChunkMeshBatch> chunkRenderers = new ArrayList<ChunkMeshBatch>();
	private TInt21TripleObjectHashMap<ChunkMeshBatch> chunkRenderersByPosition = new TInt21TripleObjectHashMap<ChunkMeshBatch>();

	private World world; // temp

	public WorldRenderer(SpoutClient client) {
		this.client = client;
	}

	public void setup() {
		setupWorld();
		
		material = (RenderMaterial) Spout.getFilesystem().getResource("material://Spout/resources/resources/materials/BasicMaterial.smt");

		// GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void render() {
		update();
		
		material.getShader().setUniform("View", client.getActiveCamera().getView());
		material.getShader().setUniform("Projection", client.getActiveCamera().getProjection());

		renderChunks();
	}

	public void setupWorld(){
		world = client.getDefaultWorld();
		if( world != null )
			((SpoutWorld)world).enableRenderQueue();
	}
	
	private final ConcurrentLinkedQueue<ChunkMesh> renderChunkMeshBatchQueue = new ConcurrentLinkedQueue<ChunkMesh>();
	private final long TIME_LIMIT = 1000L / 60L;
	
	public void update(){
		if( world == null ){
			setupWorld();
			if( world == null )
				return;
		}
		
		final long start = System.currentTimeMillis();
		
		//Step 1 : Generate ChunkMesh with SpoutChunkSnapshotModel
		for(Region region : world.getRegions()){
			SpoutChunkSnapshotModel chunkSnapshotModel;
			while( (chunkSnapshotModel = ((SpoutRegion)region).getRenderChunkQueue().poll()) != null){
				final SpoutChunkSnapshotModel temp = chunkSnapshotModel;
				Spout.getEngine().getScheduler().scheduleAsyncTask(this, new Runnable() {
					@Override
					public void run() {
						ChunkMesh mesh = new ChunkMesh(temp);
						mesh.update();
						renderChunkMeshBatchQueue.add(mesh);
					}
				});
				
				if( System.currentTimeMillis() - start > TIME_LIMIT)
					return;
			}
		}
		
		Set<ChunkMeshBatch> modifiedBatch = new HashSet<ChunkMeshBatch>();
		
		//Step 2 : Add ChunkMesh to ChunkMeshBatch
		ChunkMesh chunkMesh;
		while( (chunkMesh = renderChunkMeshBatchQueue.poll()) != null){
			Vector3 batchCoords = ChunkMeshBatch.getBatchCoordinates(new Vector3(chunkMesh.getX(), chunkMesh.getY(), chunkMesh.getZ()));
			Vector3 chunkCoords = ChunkMeshBatch.getChunkCoordinates(batchCoords);
			ChunkMeshBatch chunkMeshBatch = getChunkMeshBatchByChunkPosition(chunkCoords.getFloorX(), chunkCoords.getFloorY(), chunkCoords.getFloorZ());
		
			if(chunkMeshBatch==null){
				if(!chunkMesh.hasVertices())
					continue;
				chunkMeshBatch = new ChunkMeshBatch(world,chunkCoords.getFloorX(), chunkCoords.getFloorY(), chunkCoords.getFloorZ());
				addChunkMeshBatch(chunkMeshBatch);
			}
		
			if(chunkMesh.hasVertices()){
				chunkMeshBatch.addMesh(chunkMesh);
				modifiedBatch.add(chunkMeshBatch);
			}else{
				chunkMeshBatch.removeMesh(chunkMesh);
				modifiedBatch.add(chunkMeshBatch);
			}
			
			if( System.currentTimeMillis() - start > TIME_LIMIT)
				break;
		}
		
		//Step 3 : Execute/Delete ChunkMeshBatch if full/empty
		for(ChunkMeshBatch batch : modifiedBatch){
			if(batch.isFull()){
				batch.update();
			}else if(batch.isEmpty()){
				removeChunkMeshBatch(batch);
			}
		}
	}

	private void addChunkMeshBatch(ChunkMeshBatch batch) {
		chunkRenderers.add(batch);
		chunkRenderersByPosition.put(batch.getX(), batch.getY(), batch.getZ(), batch);
	}

	private void removeChunkMeshBatch(ChunkMeshBatch batch) {
		chunkRenderers.remove(batch);
		chunkRenderersByPosition.remove(batch.getX(), batch.getY(), batch.getZ());
		batch.finalize();
	}
	
	/**
	 * Gets the chunk mesh batch corresponding with the given chunk.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private ChunkMeshBatch getChunkMeshBatchByChunkPosition(int x, int y, int z) {
		return chunkRenderersByPosition.get(x, y, z);
	}

	private void renderChunks() {
		for (ChunkMeshBatch renderer : chunkRenderers) {
			material.getShader().setUniform("Model", renderer.getTransform());

			// It's hard to look right
			// at the world baby
			// But here's my frustrum
			// so cull me maybe?
			//if (client.getActiveCamera().getFrustum().intersects(renderer)) {
				renderer.render(material);
			//}
		}
	}
}
