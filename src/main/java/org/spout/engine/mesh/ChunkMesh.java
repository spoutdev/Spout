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
package org.spout.engine.mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.world.SpoutChunkSnapshotModel;

/**
 * Represents a mesh for a chunk.
 */
public class ChunkMesh{
	
	private TreeMap<Integer, ComposedMesh> meshs = new TreeMap<Integer, ComposedMesh>();
	
	private SpoutChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;
	private final int cx,cy,cz;
	private boolean isUnloaded = false;
	private final BlockFace face;

	/**
	 * Private constructor.
	 */
	private ChunkMesh(SpoutChunkSnapshotModel chunkModel, BlockFace face) {
		this.chunkModel = chunkModel;
		this.face = face;
		cx = chunkModel.getX();
		cy = chunkModel.getY();
		cz = chunkModel.getZ();
	}
	
	public static List<ChunkMesh> getChunkMeshs(SpoutChunkSnapshotModel chunkModel){
		List<ChunkMesh> meshs = new ArrayList<ChunkMesh>();
		if(chunkModel.isUnload()){ // Useless to make ChunkMesh for each face
			meshs.add(new ChunkMesh(chunkModel, BlockFace.THIS));
		}else{
			for(BlockFace face : BlockFace.values()){
				meshs.add(new ChunkMesh(chunkModel, face));
			}
		}
		return meshs;
	}
	
	public int getX(){
		return cx;
	}

	public int getY(){
		return cy;
	}

	public int getZ(){
		return cz;
	}

	/**
	 * Updates the mesh.
	 */
	public void update() {
		if(chunkModel.isUnload()){
			isUnloaded = true;
			return;
		}

		center = chunkModel.getCenter();

		for (int x = center.getBase().getBlockX(); x < center.getBase().getBlockX() + Chunk.BLOCKS.SIZE; x++) {
			for (int y = center.getBase().getBlockY(); y < center.getBase().getBlockY() + Chunk.BLOCKS.SIZE; y++) {
				for (int z = center.getBase().getBlockZ(); z < center.getBase().getBlockZ() + Chunk.BLOCKS.SIZE; z++) {
					generateBlockVertices(chunkModel,x, y, z);
				}
			}
		}

		// Free memory
		chunkModel = null;
		center = null;
	}

	private ComposedMesh getComposedMesh(int layer, boolean create){
		ComposedMesh mesh = meshs.get(layer);
		if(mesh == null && create){
			mesh = new ComposedMesh();
			meshs.put(layer, mesh);
		}
		return mesh;
	}
	
	/**
	 * Generates the vertices of the given block and adds them to the ChunkMesh.
	 * @param chunkSnapshotModel 
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void generateBlockVertices(SpoutChunkSnapshotModel chunkSnapshotModel, int x, int y, int z) {
		BlockMaterial material = center.getBlockMaterial(x, y, z);

		if (material.isTransparent()) {
			return;
		}

		//TODO : Waiting BlockMaterial have model & material : material.getModel().getRenderMaterial();
		//TODO : Remove fallback 
		RenderMaterial renderMaterial;
		try{
			renderMaterial = material.getModel().getRenderMaterial();
		}catch (NullPointerException e) {
			// Use fallback
			renderMaterial = WorldRenderer.material;
		}

		Vector3 position = new Vector3(x, y, z);
		Vector3 facePos = position.add(face.getOffset());
		int x1 = facePos.getFloorX();
		int y1 = facePos.getFloorY();
		int z1 = facePos.getFloorZ();
		BlockMaterial neighbor = chunkModel.getChunkFromBlock(x1, y1, z1).getBlockMaterial(x1, y1, z1);

		if (!material.isFaceRendered(face, neighbor)) {
			return;
		}

		ByteBitSet occlusion = neighbor.getOcclusion(material.getData());

		if (!occlusion.get(face.getOpposite())) {
			int layer = renderMaterial.getLayer();

			List<MeshFace> faces = renderMaterial.render(chunkSnapshotModel, position, face);
			
			if(!faces.isEmpty())
				getComposedMesh(layer,true).getMesh(renderMaterial).addAll(faces);
		}
	}

	/**
	 * Checks if the chunk mesh has any vertices.
	 * 
	 * @return
	 */
	public boolean hasVertices(int layer) {
		ComposedMesh mesh = getComposedMesh(layer,false);
		if(mesh == null) return false;
		return mesh.hasVertice();
	}

	@Override
	public String toString() {
		return "ChunkMesh [center=" + center + "]";
	}

	public boolean isUnloaded() {
		return isUnloaded;
	}

	public ComposedMesh getLayer(int layer) {
		return meshs.get(layer);
	}

	public BlockFace getFace() {
		return face;
	}

	public Set<Integer> getLayers() {
		return meshs.keySet();
	}
}
