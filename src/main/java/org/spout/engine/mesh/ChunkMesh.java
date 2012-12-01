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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
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
import org.spout.engine.renderer.BatchVertex;
import org.spout.engine.world.SpoutChunkSnapshotModel;

/**
 * Represents a mesh for a chunk.
 */
public class ChunkMesh{

	public final static List<BlockFace> shouldRender = new ArrayList<BlockFace>(Arrays.asList(BlockFace.TOP,BlockFace.BOTTOM,BlockFace.NORTH,BlockFace.SOUTH,BlockFace.WEST,BlockFace.EAST));

	private HashMap<RenderMaterial, BatchVertex> meshs = new HashMap<RenderMaterial, BatchVertex>();
	private boolean verticeGenerated = false;
	private boolean lightGenerated = false;

	private SpoutChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;
	private final World world;
	private final int chunkX,chunkY,chunkZ;
	private boolean isUnloaded = false;
	private boolean first = false;

	/**
	 * Time of the used SpoutChunkSnapshotModel generation
	 * To benchmark purpose
	 */
	private final long time;

	public ChunkMesh(SpoutChunkSnapshotModel chunkModel) {
		this.chunkModel = chunkModel;
		first = chunkModel.isFirst();

		world = chunkModel.getWorld();

		chunkX = chunkModel.getX();
		chunkY = chunkModel.getY();
		chunkZ = chunkModel.getZ();

		time = chunkModel.getTime();
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

		for (int x = center.getBase().getBlockX(); x < center.getBase().getBlockX() + Chunk.BLOCKS.SIZE; x++) {
			for (int y = center.getBase().getBlockY(); y < center.getBase().getBlockY() + Chunk.BLOCKS.SIZE; y++) {
				for (int z = center.getBase().getBlockZ(); z < center.getBase().getBlockZ() + Chunk.BLOCKS.SIZE; z++) {
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

		if(chunkModelLight.isUnload())
			throw new IllegalStateException("ChunkSnapshotModel with Unload state can't be used to compute light");

		for(BatchVertex batch : meshs.values()){

			batch.colorBuffer.clear(batch.getVertexCount() * 4);

			for(int i = 0; i < batch.vertexBuffer.size();){
				Color color = generateLightOnVertices(chunkModelLight,batch.vertexBuffer.get(i++),batch.vertexBuffer.get(i++),batch.vertexBuffer.get(i++));
				i++; //Ignore w
				batch.addColor(color);
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
			float light = 0;
			float skylight = 0;
			int count = 0;

			//TODO : Make it use each sort of light if plugin can add others lights later

			ChunkSnapshot chunk = chunkModel.getChunkFromBlock(xi, yi, zi);
			light += chunk.getBlockLight(xi, yi, zi);
			skylight += chunk.getBlockSkyLight(xi, yi, zi);
			count++;

			if(x == xi){
				chunk = chunkModel.getChunkFromBlock(xi - 1, yi, zi);
				light += chunk.getBlockLight(xi - 1, yi, zi);
				skylight += chunk.getBlockSkyLight(xi - 1, yi, zi);
				count++;
			}

			if(y == yi){
				chunk = chunkModel.getChunkFromBlock(xi, yi - 1, zi);
				light += chunk.getBlockLight(xi, yi - 1, zi);
				skylight += chunk.getBlockSkyLight(xi, yi - 1, zi);
				count++;
			}

			if(z == zi){
				chunk = chunkModel.getChunkFromBlock(xi, yi, zi - 1);
				light += chunk.getBlockLight(xi, yi, zi - 1);
				skylight += chunk.getBlockSkyLight(xi, yi, zi - 1);
				count++;
			}

			light /= count;
			skylight /= count;
			light /= 16;
			skylight /= 16;

			//TODO : Maybe we should use two byte buffer to store light and let the shader use it as the shader want
			//(we can give the sky color and light color with a render effect in Vanilla)

			Color colorLight = new Color(light * 1.00f, light * 0.75f, light * 0.75f);
			Color colorSky = new Color(skylight * 0.75f, skylight * 0.75f, skylight * 1.00f);
			return new Color(
					MathHelper.clamp(colorLight.getRed() + colorSky.getRed(), 0, 255),
					MathHelper.clamp(colorLight.getGreen() + colorSky.getGreen(), 0, 255),
					MathHelper.clamp(colorLight.getBlue() + colorSky.getBlue(), 0, 255)
					);
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

	public World getWorld() {
		return world;
	}

}
