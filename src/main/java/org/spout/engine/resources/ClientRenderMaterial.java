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
package org.spout.engine.resources;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.model.MeshFace;
import org.spout.api.model.TextureMesh;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Shader;
import org.spout.api.resource.Resource;

public class ClientRenderMaterial extends Resource implements RenderMaterial {
	
	Shader shader;
	Map<String, Object> materialParameters;

	boolean depthTesting;
	Matrix view;
	Matrix projection;
	int layer;

	public ClientRenderMaterial(Shader s, Map<String, Object> params){
		this(s, params, null, null, true, 0);
	}
	
	public ClientRenderMaterial(Shader s, Map<String, Object> params, int layer){
		this(s, params, null, null, true, layer);
	}

	public ClientRenderMaterial(Shader s, Map<String, Object> params, Matrix projection, Matrix view, boolean depth, int layer){
		this.shader = s;
		this.materialParameters = params;
		this.projection = projection;
		this.view = view;
		this.depthTesting = depth;
		this.layer = layer;
	}
	
	@Override
	public void assign(){
		Set<Map.Entry<String, Object>> s = materialParameters.entrySet();

		for(Map.Entry<String, Object> entry : s){
			if(entry.getValue() instanceof Integer){
				shader.setUniform(entry.getKey(), ((Integer)entry.getValue()).intValue());
			} else if( entry.getValue() instanceof Float){
				shader.setUniform(entry.getKey(), ((Float)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof Double){
				shader.setUniform(entry.getKey(), ((Double)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof ClientTexture){
				shader.setUniform(entry.getKey(), (ClientTexture)entry.getValue());
			} else if( entry.getValue() instanceof Vector2){
				shader.setUniform(entry.getKey(), (Vector2)entry.getValue());
			} else if( entry.getValue() instanceof Vector3){
				shader.setUniform(entry.getKey(), (Vector3)entry.getValue());
			} else if( entry.getValue() instanceof Vector4){
				shader.setUniform(entry.getKey(), (Vector4)entry.getValue());
			} else if( entry.getValue() instanceof Color){
				shader.setUniform(entry.getKey(), (Color)entry.getValue());
			} else if( entry.getValue() instanceof Matrix) {
				shader.setUniform(entry.getKey(), (Matrix)entry.getValue());
			}
		}
		
		shader.assign();

	}

	@Override
	public Object getValue(String name) {
		return materialParameters.get(name);
	}
	
	@Override
	public Shader getShader(){
		return shader;
	}
	
	@Override
	public void preRender() {
		if(!depthTesting){
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
		
	}
	
	@Override
	public void postRender() {
		if(!depthTesting){
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	@Override
	public List<MeshFace> render(ChunkSnapshotModel chunkSnapshotModel,
			Vector3 position, List<BlockFace> faces) {
		List<MeshFace> meshs = new ArrayList<MeshFace>();
		BlockMaterial blockMaterial = chunkSnapshotModel.getCenter().getBlockMaterial(position.getFloorX(), position.getFloorY(), position.getFloorZ());

		/*   1--2
		 *  /| /|
		 * 5--6 |   
		 * | 0|-3    Y - Bottom < TOP
		 * |/ |/     |
		 * 4--7      O-- X - North < SOUTH
		 *          /
		 *         Z - East < WEST
		 */
		Vector3 model = new Vector3(position.getFloorX() & Chunk.BLOCKS.MASK, position.getFloorY() & Chunk.BLOCKS.MASK, position.getFloorZ() & Chunk.BLOCKS.MASK);

		Vector3 vertex0 = model.add(0, 0, 0);
		Vector3 vertex1 = model.add(0, 1, 0);
		Vector3 vertex2 = model.add(1, 1, 0);
		Vector3 vertex3 = model.add(1, 0, 0);
		Vector3 vertex4 = model.add(0, 0, 1);
		Vector3 vertex5 = model.add(0, 1, 1);
		Vector3 vertex6 = model.add(1, 1, 1);
		Vector3 vertex7 = model.add(1, 0, 1);

		TextureMesh mesh = (TextureMesh) blockMaterial.getModel().getMesh();
		
		for(BlockFace face : faces){
			Vertex v1 = null, v2 = null, v3 = null, v4 = null;
			switch (face) {
			case TOP:
				v1 = new Vertex(vertex1, face.getOffset(), mesh.getUV(0,0));
				v2 = new Vertex(vertex2, face.getOffset(), mesh.getUV(0,1));
				v3 = new Vertex(vertex6, face.getOffset(), mesh.getUV(0,2));
				v4 = new Vertex(vertex5, face.getOffset(), mesh.getUV(0,3));
				break;
			case BOTTOM:
				v1 = new Vertex(vertex0, face.getOffset(), mesh.getUV(1,0));
				v2 = new Vertex(vertex4, face.getOffset(), mesh.getUV(1,1));
				v3 = new Vertex(vertex7, face.getOffset(), mesh.getUV(1,2));
				v4 = new Vertex(vertex3, face.getOffset(), mesh.getUV(1,3));
				break;
			case NORTH:
				v1 = new Vertex(vertex0, face.getOffset(), mesh.getUV(2,0));
				v2 = new Vertex(vertex1, face.getOffset(), mesh.getUV(2,1));
				v3 = new Vertex(vertex5, face.getOffset(), mesh.getUV(2,2));
				v4 = new Vertex(vertex4, face.getOffset(), mesh.getUV(2,3));
				break;
			case SOUTH:
				v1 = new Vertex(vertex7, face.getOffset(), mesh.getUV(3,0));
				v2 = new Vertex(vertex6, face.getOffset(), mesh.getUV(3,1));
				v3 = new Vertex(vertex2, face.getOffset(), mesh.getUV(3,2));
				v4 = new Vertex(vertex3, face.getOffset(), mesh.getUV(3,3));
				break;
			case EAST:
				v1 = new Vertex(vertex0, face.getOffset(), mesh.getUV(4,0));
				v2 = new Vertex(vertex3, face.getOffset(), mesh.getUV(4,1));
				v3 = new Vertex(vertex2, face.getOffset(), mesh.getUV(4,2));
				v4 = new Vertex(vertex1, face.getOffset(), mesh.getUV(4,3));
				break;
			case WEST:
				v1 = new Vertex(vertex5, face.getOffset(), mesh.getUV(5,0));
				v2 = new Vertex(vertex6, face.getOffset(), mesh.getUV(5,1));
				v3 = new Vertex(vertex7, face.getOffset(), mesh.getUV(5,2));
				v4 = new Vertex(vertex4, face.getOffset(), mesh.getUV(5,3));
				break;
			}

			Color color = Color.WHITE; // Temporary testing color
			v1.color = color;
			v2.color = color;
			v3.color = color;
			v4.color = color;

			MeshFace f1 = new MeshFace(v1, v2, v3);
			MeshFace f2 = new MeshFace(v3, v4, v1);
			meshs.add(f1);
			meshs.add(f2);
		}

		return meshs;
	}
	

	@Override
	public List<MeshFace> render(ChunkSnapshotModel chunkSnapshotModel,
			Vector3 position, BlockFace face) {
		List<MeshFace> meshs = new ArrayList<MeshFace>();
		BlockMaterial blockMaterial = chunkSnapshotModel.getCenter().getBlockMaterial(position.getFloorX(), position.getFloorY(), position.getFloorZ());

		/*   1--2
		 *  /| /|
		 * 5--6 |   
		 * | 0|-3    Y - Bottom < TOP
		 * |/ |/     |
		 * 4--7      O-- X - North < SOUTH
		 *          /
		 *         Z - East < WEST
		 */
		Vector3 model = new Vector3(position.getFloorX() & Chunk.BLOCKS.MASK, position.getFloorY() & Chunk.BLOCKS.MASK, position.getFloorZ() & Chunk.BLOCKS.MASK);

		Vector3 vertex0 = model.add(0, 0, 0);
		Vector3 vertex1 = model.add(0, 1, 0);
		Vector3 vertex2 = model.add(1, 1, 0);
		Vector3 vertex3 = model.add(1, 0, 0);
		Vector3 vertex4 = model.add(0, 0, 1);
		Vector3 vertex5 = model.add(0, 1, 1);
		Vector3 vertex6 = model.add(1, 1, 1);
		Vector3 vertex7 = model.add(1, 0, 1);

		TextureMesh mesh = (TextureMesh) blockMaterial.getModel().getMesh();

		Vertex v1 = null, v2 = null, v3 = null, v4 = null;
		switch (face) {
		case TOP:
			v1 = new Vertex(vertex1, face.getOffset(), mesh.getUV(0,0));
			v2 = new Vertex(vertex2, face.getOffset(), mesh.getUV(0,1));
			v3 = new Vertex(vertex6, face.getOffset(), mesh.getUV(0,2));
			v4 = new Vertex(vertex5, face.getOffset(), mesh.getUV(0,3));
			break;
		case BOTTOM:
			v1 = new Vertex(vertex0, face.getOffset(), mesh.getUV(1,0));
			v2 = new Vertex(vertex4, face.getOffset(), mesh.getUV(1,1));
			v3 = new Vertex(vertex7, face.getOffset(), mesh.getUV(1,2));
			v4 = new Vertex(vertex3, face.getOffset(), mesh.getUV(1,3));
			break;
		case NORTH:
			v1 = new Vertex(vertex0, face.getOffset(), mesh.getUV(2,0));
			v2 = new Vertex(vertex1, face.getOffset(), mesh.getUV(2,1));
			v3 = new Vertex(vertex5, face.getOffset(), mesh.getUV(2,2));
			v4 = new Vertex(vertex4, face.getOffset(), mesh.getUV(2,3));
			break;
		case SOUTH:
			v1 = new Vertex(vertex7, face.getOffset(), mesh.getUV(3,0));
			v2 = new Vertex(vertex6, face.getOffset(), mesh.getUV(3,1));
			v3 = new Vertex(vertex2, face.getOffset(), mesh.getUV(3,2));
			v4 = new Vertex(vertex3, face.getOffset(), mesh.getUV(3,3));
			break;
		case EAST:
			v1 = new Vertex(vertex0, face.getOffset(), mesh.getUV(4,0));
			v2 = new Vertex(vertex3, face.getOffset(), mesh.getUV(4,1));
			v3 = new Vertex(vertex2, face.getOffset(), mesh.getUV(4,2));
			v4 = new Vertex(vertex1, face.getOffset(), mesh.getUV(4,3));
			break;
		case WEST:
			v1 = new Vertex(vertex5, face.getOffset(), mesh.getUV(5,0));
			v2 = new Vertex(vertex6, face.getOffset(), mesh.getUV(5,1));
			v3 = new Vertex(vertex7, face.getOffset(), mesh.getUV(5,2));
			v4 = new Vertex(vertex4, face.getOffset(), mesh.getUV(5,3));
			break;
		case THIS:
		default:
			return meshs;
		}

		/*float lightD = ((15 - chunkSnapshotModel.getCenter().getBlockLight(position.getFloorX(), position.getFloorY(), position.getFloorZ())) * (1f / 15));
		Color light = new Color(lightD * 0.5f, lightD * 0.25f, lightD * 0.25f);
		
		float skyLightD = ((15 - chunkSnapshotModel.getCenter().getBlockSkyLight(position.getFloorX(), position.getFloorY(), position.getFloorZ())) * (1f / 15));
		Color skyLight = new Color(skyLightD * 0.25f, skyLightD * 0.25f, skyLightD * 0.5f);
		
		Color result = new Color(Math.max(light.getRed(),skyLight.getRed()),
				Math.max(light.getGreen(),skyLight.getGreen()),
				Math.max(light.getBlue(),skyLight.getBlue()));*/
		
		Color color = Color.WHITE; // Temporary testing color
		v1.color = color;
		v2.color = color;
		v3.color = color;
		v4.color = color;

		MeshFace f1 = new MeshFace(v1, v2, v3);
		MeshFace f2 = new MeshFace(v3, v4, v1);
		meshs.add(f1);
		meshs.add(f2);

		return meshs;
	}

	@Override
	public int getLayer() {
		return layer;
	}
}
