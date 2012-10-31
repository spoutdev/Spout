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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.engine.SpoutClient;
import org.spout.engine.batcher.ChunkMeshBatch;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.mesh.CubeMesh;
import org.spout.engine.world.SpoutChunkSnapshotModel;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

public class WorldRenderer {
	private final SpoutClient client;
	public static RenderMaterial material;

	public static final Comparator<RenderMaterial> RenderMaterialLayer = new Comparator<RenderMaterial>() {
		@Override
		public int compare(final RenderMaterial e1, final RenderMaterial e2) {
			if(e2.getLayer() == e1.getLayer())
				return 1;
			return e1.getLayer() - e2.getLayer();
		}
	};

	private TreeMap<RenderMaterial,List<ChunkMeshBatch>> chunkRenderers = new TreeMap<RenderMaterial,List<ChunkMeshBatch>>(RenderMaterialLayer);
	private TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatch>>> chunkRenderersByPosition = new TInt21TripleObjectHashMap<Map<RenderMaterial,Map<BlockFace,ChunkMeshBatch>>>();

	public static HashMap<String,CubeMesh> blocksMesh = new HashMap<String, CubeMesh>();
	public static CubeMesh defaultMesh = null;

	private World world; // temp

	public WorldRenderer(SpoutClient client) {
		this.client = client;
	}

	public void setup() {
		material = (RenderMaterial) Spout.getEngine().getFilesystem().getResource("material://Spout/resources/resources/materials/VanillaMaterial.smt");

		defaultMesh = (CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/cube.obj");
		blocksMesh.put("Stone",(CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/stone.obj"));
		blocksMesh.put("Grass",(CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/grass.obj"));
		blocksMesh.put("Dirt",(CubeMesh) Spout.getEngine().getFilesystem().getResource("cubemesh://Spout/resources/resources/models/dirt.obj"));

		setupWorld();

		//Enable(GL11.GL_CULL_FACE);
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
	private final long TIME_LIMIT = 2;

	public void update(){
		if( world == null ){
			setupWorld();
			if( world == null )
				return;
		}

		final long start = System.currentTimeMillis();

		//Step 2 : Add ChunkMesh to ChunkMeshBatch
		ChunkMesh chunkMesh;
		while( (chunkMesh = renderChunkMeshBatchQueue.poll()) != null){
			Vector3 batchCoords = ChunkMeshBatch.getBatchCoordinates(new Vector3(chunkMesh.getX(), chunkMesh.getY(), chunkMesh.getZ()));
			Vector3 chunkCoords = ChunkMeshBatch.getChunkCoordinates(batchCoords);

			for(RenderMaterial material : chunkMesh.getRenderMaterials()){
				ChunkMeshBatch chunkMeshBatch = getChunkMeshBatchByBatchPosition(batchCoords.getFloorX(), batchCoords.getFloorY(), batchCoords.getFloorZ(), chunkMesh.getFace(), material);

				if(chunkMeshBatch==null){
					if(chunkMesh.isUnloaded() || !chunkMesh.hasVertices(material))
						continue;
					chunkMeshBatch = new ChunkMeshBatch(world,chunkCoords.getFloorX(), chunkCoords.getFloorY(), chunkCoords.getFloorZ(), chunkMesh.getFace(), material);

					addChunkMeshBatch(chunkMeshBatch);
				}

				if(!chunkMesh.isUnloaded()){
					chunkMeshBatch.addMesh(chunkMesh);
					chunkMeshBatch.update(); // One chunk in batch only
				}else{
					chunkMeshBatch.removeMesh(chunkMesh);
					removeChunkMeshBatch(chunkMeshBatch); // One chunk in batch only
				}
			}

			if( System.currentTimeMillis() - start > TIME_LIMIT)
				break;
		}

		//Step 3 : Execute/Delete ChunkMeshBatch if full/empty
		/*for(ChunkMeshBatch batch : modifiedBatch){
			if(batch.isFull()){
				batch.update();
			}else if(batch.isEmpty()){
				removeChunkMeshBatch(batch);
			}
		}*/

		long time =  System.currentTimeMillis() - start;
		if(time > 1)
			System.out.println("WorldRender update take : " + time);
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

		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatch>> map = chunkRenderersByPosition.get(batch.getX(), batch.getY(), batch.getZ());
		if(map == null){
			map = new HashMap<RenderMaterial, Map<BlockFace, ChunkMeshBatch>>();
			chunkRenderersByPosition.put(batch.getX(), batch.getY(), batch.getZ(), map);
		}

		Map<BlockFace, ChunkMeshBatch> map2 = map.get(batch.getMaterial());
		if( map2 == null ){
			map2 = new HashMap<BlockFace, ChunkMeshBatch>();
			map.put(batch.getMaterial(), map2);
		}

		map2.put(batch.getFace(), batch);
	}

	private void removeChunkMeshBatch(ChunkMeshBatch batch) {
		List<ChunkMeshBatch> list = chunkRenderers.get(batch.getMaterial());
		list.remove(batch);
		if(list.isEmpty())
			chunkRenderers.remove(batch.getMaterial());

		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatch>> map = chunkRenderersByPosition.get(batch.getX(), batch.getY(), batch.getZ());
		Map<BlockFace, ChunkMeshBatch> map2 = map.get(batch.getMaterial());
		map2.remove(batch.getFace());
		if(map2.isEmpty()){
			map.remove(batch.getMaterial());
			if(map.isEmpty()){
				chunkRenderersByPosition.remove(batch.getX(), batch.getY(), batch.getZ());

			}
		}

		batch.finalize();
	}

	/**
	 * Gets the chunk mesh batch corresponding with the given chunk.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private ChunkMeshBatch getChunkMeshBatchByBatchPosition(int x, int y, int z, BlockFace face, RenderMaterial material) {
		Map<RenderMaterial, Map<BlockFace, ChunkMeshBatch>> map = chunkRenderersByPosition.get(x, y, z);
		if( map == null )
			return null;
		Map<BlockFace, ChunkMeshBatch> map2 = map.get(material);
		if( map2 == null )
			return null;
		return map2.get(face);
	}

	private void renderChunks() {
		final long start = System.currentTimeMillis();

		int x =client.getActivePlayer().getChunk().getX();
		int y = client.getActivePlayer().getChunk().getY();
		int z = client.getActivePlayer().getChunk().getZ();

		int culledChunks = 0;
		for(List<ChunkMeshBatch> list : chunkRenderers.values()){
			for (ChunkMeshBatch renderer : list) {

				if(renderer.getY() > y && renderer.getFace() == BlockFace.TOP){
					culledChunks++;
					continue;
				}
				if(renderer.getY() < y && renderer.getFace() == BlockFace.BOTTOM){
					culledChunks++;
					continue;
				}

				if(renderer.getX() > x && renderer.getFace() == BlockFace.SOUTH){
					culledChunks++;
					continue;
				}
				if(renderer.getX() < x && renderer.getFace() == BlockFace.NORTH){
					culledChunks++;
					continue;
				}

				if(renderer.getZ() > z && renderer.getFace() == BlockFace.WEST){
					culledChunks++;
					continue;
				}
				if(renderer.getZ() < z && renderer.getFace() == BlockFace.EAST){
					culledChunks++;
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

		/*long time =  System.currentTimeMillis() - start;
		if(time > 1)
			System.out.println("WorldRender render take : " + time);
		if( culledChunks > 0)
			System.out.println("Culled facechunk : " + culledChunks);*/
	}

	public int getChunkRenderersSize(){
		return chunkRenderers.size();
	}
}
