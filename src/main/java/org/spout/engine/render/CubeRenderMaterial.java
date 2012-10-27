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
package org.spout.engine.render;

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
import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.model.MeshFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Shader;
import org.spout.engine.resources.ClientTexture;

public class CubeRenderMaterial implements RenderMaterial {

	Shader shader;
	Map<String, Object> materialParameters;

	boolean depthTesting;
	Matrix view;
	Matrix projection;

	public CubeRenderMaterial(Shader s, Map<String, Object> params){
		this(s, params, null, null, true);
	}

	public CubeRenderMaterial(Shader s, Map<String, Object> params, Matrix projection, Matrix view, boolean depth){
		this.shader = s;
		this.materialParameters = params;
		this.projection = projection;
		this.view = view;
		this.depthTesting = depth;
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
		if(!depthTesting)
		{
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
		Vector3 model = new Vector3(position.getFloorX() & Chunk.BLOCKS.MASK, position.getFloorY() & Chunk.BLOCKS.MASK, position.getFloorZ() & Chunk.BLOCKS.MASK);

		Vector3 vertex0 = model.add(0, 0, 0);
		Vector3 vertex1 = model.add(0, 1, 0);
		Vector3 vertex2 = model.add(1, 1, 0);
		Vector3 vertex3 = model.add(1, 0, 0);
		Vector3 vertex4 = model.add(0, 0, 1);
		Vector3 vertex5 = model.add(0, 1, 1);
		Vector3 vertex6 = model.add(1, 1, 1);
		Vector3 vertex7 = model.add(1, 0, 1);

		for(BlockFace face : faces){
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

			Rectangle r = new Rectangle(0, 0, 1, 1);//TODO : Replace by a getModel() to get TextMesh

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
			meshs.add(f1);
			meshs.add(f2);
		}

		return meshs;
	}
}
