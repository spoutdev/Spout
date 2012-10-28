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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.model.OrientedMeshFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderMaterial;
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

			//shouldRender[face.ordinal()] = neighbor.isTransparent();
			if(!neighbor.isOpaque())
				shouldRender.add(face);
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
		
		faces.addAll(WorldRenderer.material.render(chunkSnapshotModel, position, shouldRender));

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

	private Vertex copy(final Vertex vertex){
		Vertex newv = new Vertex(new Vector3(vertex.position));
		if(newv.color != null)
			newv.color = new Color(vertex.color.getRGB());
		if(newv.normal != null)
			newv.normal =  new Vector3(vertex.normal);
		if(newv.texCoord0 != null)
			newv.texCoord0 =  new Vector2(vertex.texCoord0);
		if(newv.texCoord1 != null)
			newv.texCoord1 =  new Vector2(vertex.texCoord1);
		return newv;
	}

	/**
	 * Appends ModelFaces from the block face. This will likely be temporary.
	 * 
	 * @param face
	 * @param base
	 * @param faces 
	 */
	private void appendModelFaces(BlockMaterial m, BlockFace face, Vector3 base, ArrayList<MeshFace> faces) {
		Vector3 p1 = null;
		Vector3 p2 = null;
		Vector3 p3 = null;
		Vector3 p4 = null;

		/*   1--2
		 *  /| /|
		 * 5--6 |   
		 * | 0|-3    Y - Bottom < TOP
		 * |/ |/     |
		 * 4--7      O-- X - North < SOUTH
		 *          /
		 *         Z - East < WEST
		 */

		Vector3 vertex0 = base.add(0, 0, 0);
		Vector3 vertex1 = base.add(0, 1, 0);
		Vector3 vertex2 = base.add(1, 1, 0);
		Vector3 vertex3 = base.add(1, 0, 0);
		Vector3 vertex4 = base.add(0, 0, 1);
		Vector3 vertex5 = base.add(0, 1, 1);
		Vector3 vertex6 = base.add(1, 1, 1);
		Vector3 vertex7 = base.add(1, 0, 1);

		switch (face) {
		case TOP:
			p1 = vertex1;
			p2 = vertex2;
			p3 = vertex6;
			p4 = vertex5;
			break;
		case BOTTOM:
			p1 = vertex0;
			p2 = vertex4;
			p3 = vertex7;
			p4 = vertex3;
			break;
		case NORTH:
			p1 = vertex0;
			p2 = vertex1;
			p3 = vertex5;
			p4 = vertex4;
			break;
		case SOUTH:
			p1 = vertex7;
			p2 = vertex6;
			p3 = vertex2;
			p4 = vertex3;
			break;
		case WEST:
			p1 = vertex5;
			p2 = vertex6;
			p3 = vertex7;
			p4 = vertex4;
			break;
		case EAST:
			p1 = vertex0;
			p2 = vertex3;
			p3 = vertex2;
			p4 = vertex1;
			break;
		}

		Rectangle r = m.getTextureOffset();

		Vector2 uv1 = new Vector2(r.getX(), r.getY());
		Vector2 uv2 = new Vector2(r.getX(), r.getY()+r.getHeight());
		Vector2 uv3 = new Vector2(r.getX()+r.getWidth(), r.getY()+r.getHeight());
		Vector2 uv4 = new Vector2(r.getX()+r.getWidth(), r.getY());

		Color color = Color.WHITE; // Temporary testing color
		Vertex v1 = new Vertex(p1, face.getOffset(), uv1);
		v1.color = color;

		Vertex v2 = new Vertex(p2, face.getOffset(), uv2);
		v2.color = color;

		Vertex v3 = new Vertex(p3, face.getOffset(), uv3);
		v3.color = color;

		Vertex v4 = new Vertex(p4, face.getOffset(), uv4);
		v4.color = color;

		MeshFace f1 = new MeshFace(v1, v2, v3);
		MeshFace f2 = new MeshFace(v3, v4, v1);
		faces.add(f1);
		faces.add(f2);
	}

	private Color getColor(BlockMaterial m) {
		if (!m.isSolid()) {
			return new Color(0, 0, 0);
		}
		switch (m.getId()) {
		case 78:
			return new Color(255, 255, 255);
		case 24:
		case 12:
			return new Color(210, 210, 150);
		case 10:
			return new Color(200, 50, 50);
		case 9:
		case 8:
			return new Color(150, 150, 200);
		case 7:
			return new Color(50, 50, 50);
		case 4:
			return new Color(100, 100, 100);
		case 17:
		case 3:
			return new Color(110, 75, 35);
		case 18:
		case 2:
			return new Color(55, 140, 55);
		case 21:
		case 16:
		case 15:
		case 14:
		case 13:
		case 1:
		default:
			return new Color(150, 150, 150);
		}
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
