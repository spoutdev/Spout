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

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.world.SpoutChunkSnapshotModel;

import com.google.common.collect.Lists;

/**
 * Represents a mesh for a chunk.
 */
public class ChunkMesh extends ComposedMesh {
	/**
	 * Faces that you can render.
	 */
	private static final List<BlockFace> renderableFaces = Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.TOP, BlockFace.BOTTOM);

	private SpoutChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;
	private final int cx,cy,cz;
	private boolean isUnloaded = false;

	/**
	 * Private constructor.
	 */
	public ChunkMesh(SpoutChunkSnapshotModel chunkModel) {
		this.chunkModel = chunkModel;
		cx = chunkModel.getX();
		cy = chunkModel.getY();
		cz = chunkModel.getZ();
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
		
		//boolean[] shouldRender = new boolean[6];
		List<BlockFace> shouldRender = new ArrayList<BlockFace>();
		Vector3 position = new Vector3(x, y, z);
		for (BlockFace face : renderableFaces) {
			Vector3 facePos = position.add(face.getOffset());
			int x1 = facePos.getFloorX();
			int y1 = facePos.getFloorY();
			int z1 = facePos.getFloorZ();
			BlockMaterial neighbor = chunkModel.getChunkFromBlock(x1, y1, z1).getBlockMaterial(x1, y1, z1);
			
			if (!material.isFaceRendered(face, neighbor)) {
				continue;
			}

			ByteBitSet occlusion = neighbor.getOcclusion(material.getData());

			if (!occlusion.get(face.getOpposite())) {
				shouldRender.add(face);
			}
		}
		
		if (shouldRender.size() <= 0) {
			return;
		}

		//TODO : Waiting BlockMaterial have model & material : material.getModel().getRenderMaterial();
		//TODO : Remove fallback 

		ArrayList<MeshFace> faces;
		//if(renderMaterial.isOpaque()){
		faces = opaqueFacesPerMaterials.get(WorldRenderer.material);
		if(faces == null){
			faces = new ArrayList<MeshFace>();
			opaqueFacesPerMaterials.put(WorldRenderer.material, faces);
		}
		/*}else{
		faces = tranparentFacesPerMaterials.get(renderMaterial);
		if(faces == null){
			faces = new ArrayList<MeshFace>();
			tranparentFacesPerMaterials.put(renderMaterial, faces);
		}
		}*/
		try{
			faces.addAll(material.getModel().getRenderMaterial().render(chunkSnapshotModel, position, shouldRender));
		}catch (NullPointerException e) {
			// Use fallback
			faces.addAll(WorldRenderer.material.render(chunkSnapshotModel, position, shouldRender));
		}

		/*Vector3 model = new Vector3(x & Chunk.BLOCKS.MASK, y & Chunk.BLOCKS.MASK, z & Chunk.BLOCKS.MASK);

		CubeMesh cubeMesh = WorldRenderer.blocksMesh.get(material.getDisplayName());

		if (cubeMesh == null) {
			cubeMesh = WorldRenderer.defaultMesh;
		}
		
		for (OrientedMeshFace face : cubeMesh) {
			if(face.canRender(shouldRender)){
				Iterator<Vertex> it = face.iterator();
				Vertex v1 = copy(it.next());
				Vertex v2 = copy(it.next());
				Vertex v3 = copy(it.next());
				v1.position = v1.position.add(model);
				v2.position = v2.position.add(model);
				v3.position = v3.position.add(model);
				v1.color = Color.white;
				v2.color = Color.white;
				v3.color = Color.white;
				faces.add(new MeshFace(v1, v2, v3));
			}
		}*/

		/*for (BlockFace face : renderableFaces) {
			if (shouldRender[face.ordinal()]) {
				// System.out.println(material + " " + face + " " + position);
				// Create a face -- temporary until we get some real models
				appendModelFaces(material, face, model, faces);
			}
		}*/
	}

	/**
	 * Checks if the chunk mesh has any vertices.
	 * 
	 * @return
	 */
	public boolean hasVertices() {
		for(ArrayList<MeshFace> mesh : opaqueFacesPerMaterials.values()){
			if(!mesh.isEmpty())
				return true;
		}
		return false;
	}

	/**
	 * Counts the number of faces in the mesh.
	 * 
	 * @return
	 */
	public int countFaces() {
		//TODO : Update this
		//return faces.size();
		return 1;
	}

	@Override
	public String toString() {
		return "ChunkMesh [center=" + center + "]";
	}

	public boolean isUnloaded() {
		return isUnloaded;
	}
}
