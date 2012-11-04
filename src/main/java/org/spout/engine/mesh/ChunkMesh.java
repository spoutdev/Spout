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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.engine.world.SpoutChunkSnapshotModel;

/**
 * Represents a mesh for a chunk.
 */
public class ChunkMesh{

	private HashMap<RenderMaterial, Map<BlockFace,ComposedMesh>> meshs = new HashMap<RenderMaterial, Map<BlockFace,ComposedMesh>>();

	private SpoutChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;
	private final int cx,cy,cz;
	private boolean isUnloaded = false;
	
	/**
	 * Time of the used SpoutChunkSnapshotModel generation
	 * To benchmark purpose
	 */
	private final long time;

	public ChunkMesh(SpoutChunkSnapshotModel chunkModel) {
		this.chunkModel = chunkModel;
		cx = chunkModel.getX();
		cy = chunkModel.getY();
		cz = chunkModel.getZ();
		time = chunkModel.getTime();
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

	private Map<BlockFace, ComposedMesh> getMaterialMap(RenderMaterial material){
		Map<BlockFace, ComposedMesh> map = meshs.get(material);
		if(map == null){
			map = new HashMap<BlockFace, ComposedMesh>();
			meshs.put(material, map);
		}
		return map;
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
		
		RenderMaterial renderMaterial = material.getModel().getRenderMaterial();

		if( !chunkModel.hasRenderMaterial(renderMaterial) ){
			return;
		}
		
		Map<BlockFace, ComposedMesh> meshs = getMaterialMap(renderMaterial);

		Vector3 position = new Vector3(x, y, z);
		for(BlockFace face : BlockFace.values()){
			Vector3 facePos = position.add(face.getOffset());
			int x1 = facePos.getFloorX();
			int y1 = facePos.getFloorY();
			int z1 = facePos.getFloorZ();
			BlockMaterial neighbor = chunkModel.getChunkFromBlock(x1, y1, z1).getBlockMaterial(x1, y1, z1);

			if (!material.isFaceRendered(face, neighbor)) {
				break;
			}

			ByteBitSet occlusion = neighbor.getOcclusion(material.getData());

			if (!occlusion.get(face.getOpposite())) {
				List<MeshFace> faces = renderMaterial.render(chunkSnapshotModel, position, face);

				if(!faces.isEmpty()){
					ComposedMesh mesh = meshs.get(face);
					if(mesh == null){
						mesh = new ComposedMesh();
						meshs.put(face, mesh);
					}
					mesh.getMesh().addAll(faces);
				}
			}
		}
	}

	/**
	 * Checks if the chunk mesh has any vertices.
	 * 
	 * @return
	 */
	public boolean hasVertices() {
		return !meshs.isEmpty();
	}

	@Override
	public String toString() {
		return "ChunkMesh [center=" + center + "]";
	}

	public boolean isUnloaded() {
		return isUnloaded;
	}

	public ComposedMesh getMesh(RenderMaterial material,BlockFace face) {
		return meshs.get(material).get(face);
	}

	public HashMap<RenderMaterial, Map<BlockFace, ComposedMesh>> getMaterialsFaces() {
		return meshs;
	}

	public long getTime() {
		return time;
	}

}
