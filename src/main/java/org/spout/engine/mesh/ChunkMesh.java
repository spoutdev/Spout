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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.engine.renderer.BatchVertex;
import org.spout.engine.world.SpoutChunkSnapshotModel;

/**
 * Represents a mesh for a chunk.
 */
public class ChunkMesh{

	/**
	 * Number of piece per chunk for each dimension
	 */
	public final static int SPLIT_X = 2;
	public final static int SPLIT_Y = 1;
	public final static int SPLIT_Z = 2;
	public final static Vector3 SPLIT = new Vector3(SPLIT_X, SPLIT_Y, SPLIT_Z);
	
	/**
	 * Number of block for a sub mesh
	 */
	public final static int SUBSIZE_X = Chunk.BLOCKS.SIZE / SPLIT_X;
	public final static int SUBSIZE_Y = Chunk.BLOCKS.SIZE / SPLIT_Y;
	public final static int SUBSIZE_Z = Chunk.BLOCKS.SIZE / SPLIT_Z;
	public final static Vector3 SUBSIZE = new Vector3(SUBSIZE_X, SUBSIZE_Y, SUBSIZE_Z);
	
	public Vector3 start;
	public Vector3 end;
	
	private HashMap<RenderMaterial, Map<BlockFace,BatchVertex>> meshs = new HashMap<RenderMaterial, Map<BlockFace,BatchVertex>>();

	private SpoutChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;
	private final int chunkX,chunkY,chunkZ;
	private final int subX,subY,subZ;
	private boolean isUnloaded = false;
	
	/**
	 * Time of the used SpoutChunkSnapshotModel generation
	 * To benchmark purpose
	 */
	private final long time;

	public ChunkMesh(SpoutChunkSnapshotModel chunkModel, int x, int y, int z) {
		this.chunkModel = chunkModel;
		
		chunkX = chunkModel.getX();
		chunkY = chunkModel.getY();
		chunkZ = chunkModel.getZ();
		
		subX = chunkX * SPLIT_X + x;
		subY = chunkY * SPLIT_Y + y;
		subZ = chunkZ * SPLIT_Z + z;
		
		time = chunkModel.getTime();
		
		start = new Vector3(SUBSIZE_X * x, SUBSIZE_Y * y, SUBSIZE_Z * z);
		end = new Vector3(SUBSIZE_X * x + SUBSIZE_X, SUBSIZE_Y * y + SUBSIZE_Y, SUBSIZE_Z * z + SUBSIZE_Z);
	}

	public static List<ChunkMesh> getChunkMeshs(SpoutChunkSnapshotModel chunkModel){
		List<ChunkMesh> list = new ArrayList<ChunkMesh>();
		Set<Vector3> subMeshs = chunkModel.getSubMeshs();
		if(subMeshs == null){
			for(int i = 0; i < SPLIT_X; i++){
				for(int j = 0; j < SPLIT_Y; j++){
					for(int k = 0; k < SPLIT_Z; k++){
						list.add(new ChunkMesh(chunkModel, i, j, k));
					}
				}
			}
		}else{
			for(Vector3 vector : subMeshs)
				list.add(new ChunkMesh(chunkModel, vector.getFloorX(), vector.getFloorY(), vector.getFloorZ()));
		}
		return list;
	}

	public int getChunkX(){
		return chunkX;
	}

	public int getChunkY(){
		return chunkY;
	}

	public int getChunkZ(){
		return chunkZ;
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

		for (int x = center.getBase().getBlockX() + start.getFloorX(); x < center.getBase().getBlockX() + end.getFloorX(); x++) {
			for (int y = center.getBase().getBlockY() + start.getFloorY(); y < center.getBase().getBlockY() + end.getFloorY(); y++) {
				for (int z = center.getBase().getBlockZ() + start.getFloorZ(); z < center.getBase().getBlockZ() + end.getFloorZ(); z++) {
					generateBlockVertices(chunkModel,x, y, z);
				}
			}
		}

		// Free memory
		chunkModel = null;
		center = null;
	}

	private Map<BlockFace, BatchVertex> getMaterialMap(RenderMaterial material){
		Map<BlockFace, BatchVertex> map = meshs.get(material);
		if(map == null){
			map = new HashMap<BlockFace, BatchVertex>();
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
		
		Map<BlockFace, BatchVertex> meshs = getMaterialMap(renderMaterial);

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
				List<MeshFace> faces = renderMaterial.render(chunkSnapshotModel, material, position, face);

				if(!faces.isEmpty()){
					BatchVertex batchVertex = meshs.get(face);
					if(batchVertex == null){
						batchVertex = new BatchVertex();
						meshs.put(face, batchVertex);
					}
					
					for (MeshFace meshFace : faces) {
						for (Vertex vert : meshFace) {
							if(vert.texCoord0 != null)
								batchVertex.addTexCoord(vert.texCoord0);
							if(vert.normal != null)
								batchVertex.addNormal(vert.normal);
							if(vert.color != null)
								batchVertex.addColor(vert.color);
							batchVertex.addVertex(vert.position);
						}
					}
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

	public BatchVertex getMesh(RenderMaterial material,BlockFace face) {
		return meshs.get(material).get(face);
	}

	public HashMap<RenderMaterial, Map<BlockFace, BatchVertex>> getMaterialsFaces() {
		return meshs;
	}

	public long getTime() {
		return time;
	}

	public int getSubX() {
		return subX;
	}

	public int getSubY() {
		return subY;
	}

	public int getSubZ() {
		return subZ;
	}
	
	private static Vector3[] subMeshMap = new Vector3[Chunk.BLOCKS.VOLUME];
	
	static {
		for (int x = 0; x < Chunk.BLOCKS.SIZE; x++) {
			for (int y = 0; x < Chunk.BLOCKS.SIZE; x++) {
				for (int z = 0; x < Chunk.BLOCKS.SIZE; x++) {
					subMeshMap[getSubMeshIndex(x, y, z)] = getChunkSubMeshRaw(x, y, z);
				}
			}
		}
	}
	
	public static Vector3 getChunkSubMesh(int x, int y, int z) {
		return subMeshMap[getSubMeshIndex(x, y, z)];
	}
	
	private static Vector3 getChunkSubMeshRaw(int x, int y, int z) {
		if (isOutsideChunk(x, y, z)) {
			throw new IllegalArgumentException("(x, y, z) must be fall inside a chunk");
		}
		return new Vector3(
                Math.floor((float) ((x & Chunk.BLOCKS.MASK) / SUBSIZE_X)),
                Math.floor((float) ((y & Chunk.BLOCKS.MASK) / SUBSIZE_Y)),
                Math.floor((float) ((z & Chunk.BLOCKS.MASK) / SUBSIZE_Z)));

	}
	
	private static int getSubMeshIndex(int x, int y, int z) {
		
		return 
				((x & Chunk.BLOCKS.MASK) << Chunk.BLOCKS.DOUBLE_BITS) |
				((y & Chunk.BLOCKS.MASK) << Chunk.BLOCKS.BITS ) |
				((z & Chunk.BLOCKS.MASK) << 0);
	}
	
	private static boolean isOutsideChunk(int x, int y, int z) {
		return x < 0 || x >= Chunk.BLOCKS.SIZE || y < 0 || y >= Chunk.BLOCKS.SIZE || z < 0 || z >= Chunk.BLOCKS.SIZE;
	}


}
