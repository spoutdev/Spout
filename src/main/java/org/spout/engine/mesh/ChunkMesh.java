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

import gnu.trove.list.array.TFloatArrayList;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.model.mesh.MeshFace;
import org.spout.api.model.mesh.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotMesh;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.engine.batcher.ChunkMeshBatchAggregator;
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
	public final static int SPLIT_Y = 2;
	public final static int SPLIT_Z = 2;
	public final static Vector3 SPLIT = new Vector3(SPLIT_X, SPLIT_Y, SPLIT_Z);

	/**
	 * Number of block for a sub mesh
	 */
	public final static int SUBSIZE_X = Chunk.BLOCKS.SIZE / SPLIT_X;
	public final static int SUBSIZE_Y = Chunk.BLOCKS.SIZE / SPLIT_Y;
	public final static int SUBSIZE_Z = Chunk.BLOCKS.SIZE / SPLIT_Z;
	public final static Vector3 SUBSIZE = new Vector3(SUBSIZE_X, SUBSIZE_Y, SUBSIZE_Z);

	public final static List<BlockFace> shouldRender = new ArrayList<BlockFace>(Arrays.asList(BlockFace.TOP,BlockFace.BOTTOM,BlockFace.NORTH,BlockFace.SOUTH,BlockFace.WEST,BlockFace.EAST));

	public final static boolean UNLOAD_ACCELERATOR = SPLIT_X == ChunkMeshBatchAggregator.SIZE_X &&
			SPLIT_Y == ChunkMeshBatchAggregator.SIZE_Y &&
			SPLIT_Z == ChunkMeshBatchAggregator.SIZE_Z;

	public Vector3 start;
	public Vector3 end;

	private HashMap<RenderMaterial, BatchVertex> meshs = new HashMap<RenderMaterial, BatchVertex>();
	private boolean verticeGenerated = false;
	private boolean lightGenerated = false;

	private SpoutChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;
	private final World world;
	private final int chunkX,chunkY,chunkZ;
	private final int subX,subY,subZ;
	private boolean isUnloaded = false;
	private boolean first = false;

	/**
	 * Time of the used SpoutChunkSnapshotModel generation
	 * To benchmark purpose
	 */
	private final long time;

	public ChunkMesh(SpoutChunkSnapshotModel chunkModel, int x, int y, int z) {
		this.chunkModel = chunkModel;
		first = chunkModel.isFirst();

		world = chunkModel.getWorld();

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

	public static Set<Vector3> getSubMeshIndexs(SpoutChunkSnapshotModel chunkModel){
		Set<Vector3> list = chunkModel.getSubMeshs();
		
		//Used to clean mesh waiting light, so we need all position
		/*if(chunkModel.isUnload() && UNLOAD_ACCELERATOR){
			// Work only if ChunkMesh split == ChunkMeshBatchAggregator group, that say a aggregator contain a entire chunk
			// The clean method unload all render/face for the aggregator that contain the mesh, so we can limit send only one mesh
			HashSet<Vector3> one = new HashSet<Vector3>();
			one.add(Vector3.ZERO);
			return one;
		}else{*/
			if(list == null){
				list = new HashSet<Vector3>();
				for(int i = 0; i < SPLIT_X; i++){
					for(int j = 0; j < SPLIT_Y; j++){
						for(int k = 0; k < SPLIT_Z; k++){
							list.add(new Vector3(i, j, k));
						}
					}
				}
			}
		//}
		return list;
	}
	
	public static List<ChunkMesh> getChunkMeshs(SpoutChunkSnapshotModel chunkModel){
		List<ChunkMesh> list = new ArrayList<ChunkMesh>();
		Set<Vector3> subMeshs = chunkModel.getSubMeshs();

		if(chunkModel.isUnload() && UNLOAD_ACCELERATOR){
			// Work only if ChunkMesh split == ChunkMeshBatchAggregator group, that say a aggregator contain a entire chunk
			// The clean method unload all render/face for the aggregator that contain the mesh, so we can limit send only one mesh
			list.add(new ChunkMesh(chunkModel, 0, 0, 0));
		}else{
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

		verticeGenerated = true;
		lightGenerated = false; //Invalid the light computation
		
		// Free memory
		chunkModel = null;
		center = null;
	}

	/**
	 * Updates the mesh light, the block vertices MUST BE done before
	 */
	public void updateLight(SpoutChunkSnapshotModel chunkModelLight) {
		if(!verticeGenerated)
			throw new IllegalStateException("Vertice must be generated before compute light");
			
		//We don't need light to unload a chunk
		/*if(chunkModelLight.isUnload()){
			isUnloaded = true;
			return;
		}*/

		//int offsetColor;
		for(BatchVertex batch : meshs.values()){

			//Reallocate the colorBuffer if needed (color -> 4 value RGBA) (vertex -> 4 value xyzw)
			//if(batch.colorBuffer.size()/4 != batch.getVertexCount())
			//	batch.colorBuffer = new TFloatArrayList(batch.getVertexCount() * 4);
			batch.colorBuffer.clear();
			
			//offsetColor = 0;
			for(int i = 0; i < batch.vertexBuffer.size();){
				Color color = generateLightOnVertices(chunkModelLight,batch.vertexBuffer.get(i++),batch.vertexBuffer.get(i++),batch.vertexBuffer.get(i++));
				i++; //Ignore w
				batch.addColor(color);
				/*batch.colorBuffer.set(offsetColor++, color.getRed());
				batch.colorBuffer.set(offsetColor++, color.getGreen());
				batch.colorBuffer.set(offsetColor++, color.getBlue());
				batch.colorBuffer.set(offsetColor++, color.getAlpha());*/
			}
		}
		
		lightGenerated = true;
	}

	/**
	 * Compute the light for one vertex
	 * @param chunkModel
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private Color generateLightOnVertices(SpoutChunkSnapshotModel chunkModel, float x, float y, float z) {
		int xi = (int)x;
		int yi = (int)y;
		int zi = (int)z;
		if(chunkModel != null){
			//TODO : Fix to get light from each shared block for the vertice
			
			//TODO : Maybe we should use two byte buffer to store light and let the shader use it as the shader want
			//(we can give the sky color and light color with a render effect in Vanilla)
			
			//TODO : Make it use each sort of light if plugin can add others lights later
			ChunkSnapshot chunk = chunkModel.getChunkFromBlock(xi, yi, zi);
			if(chunk != null){
				float light = chunk.getBlockLight(xi, yi, zi) / 16f;
				float sky = chunk.getBlockSkyLight(xi, yi, zi) / 16f;
				Color colorLight = new Color(light * 1.00f, light * 0.75f, light * 0.75f);
				Color colorSky = new Color(sky * 0.75f, sky * 0.75f, sky * 1.00f);
				return new Color(
						MathHelper.clamp(colorLight.getRed() + colorSky.getRed(), 0, 255),
						MathHelper.clamp(colorLight.getGreen() + colorSky.getGreen(), 0, 255),
						MathHelper.clamp(colorLight.getBlue() + colorSky.getBlue(), 0, 255)
						);
			}else{
				return Color.WHITE;
			}
		}else{
			return Color.WHITE;
		}
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

		if (material.isInvisible()) {
			return;
		}

		RenderMaterial renderMaterial = material.getModel().getRenderMaterial();

		if( !chunkModel.hasRenderMaterial(renderMaterial) ){
			return;
		}

		Vector3 position = new Vector3(x, y, z);

		boolean toRender[] = new boolean[shouldRender.size()];
		boolean fullyOccluded = true;
		for(int i = 0; i < shouldRender.size(); i++){
			BlockFace face = shouldRender.get(i);
			Vector3 facePos = position.add(face.getOffset());
			int x1 = facePos.getFloorX();
			int y1 = facePos.getFloorY();
			int z1 = facePos.getFloorZ();

			BlockMaterial neighbor = chunkModel.getChunkFromBlock(x1, y1, z1).getBlockMaterial(x1, y1, z1);

			if (material.isFaceRendered(face, neighbor)) {
				toRender[i] = true;
				fullyOccluded = false;
			}else{
				toRender[i] = false;
				continue;
			}

			ByteBitSet occlusion = neighbor.getOcclusion(material.getData());

			if (occlusion.get(face.getOpposite())) {
				toRender[i] = false;
				continue;
			}else{
				toRender[i] = true;
				fullyOccluded = false;
			}
		}

		if(fullyOccluded)
			return;

		SnapshotMesh snapshotMesh = new SnapshotMesh(material, chunkSnapshotModel, new Point(position, world), toRender);

		renderMaterial.preMesh(snapshotMesh);
		List<MeshFace> faces = renderMaterial.render(snapshotMesh);
		renderMaterial.postMesh(snapshotMesh);

		if(!faces.isEmpty()){
			BatchVertex batchVertex = meshs.get(renderMaterial);
			if(batchVertex == null){
				batchVertex = new BatchVertex();
				meshs.put(renderMaterial, batchVertex);
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

	public Map<RenderMaterial, BatchVertex> getMaterialsFaces() {
		return meshs;
	}

	public boolean isFirst() {
		return first;
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
			for (int y = 0; y < Chunk.BLOCKS.SIZE; y++) {
				for (int z = 0; z < Chunk.BLOCKS.SIZE; z++) {
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

	public World getWorld() {
		return world;
	}


}
