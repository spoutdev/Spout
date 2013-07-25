/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.mesh;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spout.api.model.mesh.Mesh;
import org.spout.api.model.mesh.MeshFace;
import org.spout.api.model.mesh.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotRender;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.GLBufferContainer;
import org.spout.engine.renderer.vertexformat.vertexattributes.VertexAttributes;


public class BaseMesh implements Mesh {
	GLBufferContainer container = new GLBufferContainer();
	boolean batched = false;

	BatchVertexRenderer renderer;

	public BaseMesh(ArrayList<MeshFace> faces, boolean normal, boolean color, boolean texture0, boolean indexate){
		FloatBuffer vertexBuffer, normalBuffer = null, colorBuffer = null, texture0Buffer = null;
		int i = 0, numVerticies = faces.size() * 3;
		int []verticeIndex = indexate ? new int[numVerticies * 3] : null;

		vertexBuffer = BufferUtils.createFloatBuffer(numVerticies * 4);
		vertexBuffer.clear();

		if(normal){
			normalBuffer = BufferUtils.createFloatBuffer(numVerticies * 3);
			normalBuffer.clear();
		}
		if(color){
			colorBuffer = BufferUtils.createFloatBuffer(numVerticies * 4);
			colorBuffer.clear();
		}
		if(texture0){
			texture0Buffer = BufferUtils.createFloatBuffer(numVerticies * 2);
			texture0Buffer.clear();
		}

		for(MeshFace face : faces){
			for(Vertex v : face){
				vertexBuffer.put(v.position.getX());
				vertexBuffer.put(v.position.getY());
				vertexBuffer.put(v.position.getZ());
				vertexBuffer.put(1f);

				if(normal){
					normalBuffer.put(v.normal.getX());
					normalBuffer.put(v.normal.getY());
					normalBuffer.put(v.normal.getZ());
				}

				if(color){
					colorBuffer.put(v.color.getRed() / 255f);
					colorBuffer.put(v.color.getGreen() / 255f);
					colorBuffer.put(v.color.getBlue() / 255f);
					colorBuffer.put(v.color.getAlpha() / 255f);
				}

				if(texture0){
					texture0Buffer.put(v.texCoord0.getX());
					texture0Buffer.put(v.texCoord0.getY());
				}
				
				if(indexate)
					verticeIndex[i++] = v.id;
			}
		}

		vertexBuffer.flip();

		if(normal)
			normalBuffer.flip();

		if(color)
			colorBuffer.flip();

		if(texture0)
			texture0Buffer.flip();

		container.element = numVerticies;
		container.setBuffers(VertexAttributes.Position.getLayout(), vertexBuffer);
		if(normal)
			container.setBuffers(VertexAttributes.Normal.getLayout(), normalBuffer);
		if(color)
			container.setBuffers(VertexAttributes.Color.getLayout(), colorBuffer);
		if(texture0)
			container.setBuffers(VertexAttributes.Texture0.getLayout(), texture0Buffer);
		
		container.setVerticeIndex(verticeIndex);
	}

	public GLBufferContainer getContainer(){
		return container;
	}
	
	public void batch(){
		if (renderer == null)
			renderer = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);

		if(batched)
			return;

		renderer.setGLBufferContainer(container);
		renderer.flush(true);
		batched = true;
	}

	public void preDraw(){
		if (!batched)
			throw new IllegalStateException("Cannot render without batching first!");

		renderer.preDraw();
	}
	
	public void draw(RenderMaterial material){
		if (!batched)
			throw new IllegalStateException("Cannot render without batching first!");

		SnapshotRender snapshotRender = new SnapshotRender(material);
		material.preRender(snapshotRender);
		renderer.draw(material);
		material.postRender(snapshotRender);
	}
	
	public void postDraw(){
		if (!batched)
			throw new IllegalStateException("Cannot render without batching first!");

		renderer.postDraw();
	}
	
	public void render(RenderMaterial material){
		if (!batched)
			throw new IllegalStateException("Cannot render without batching first!");

		SnapshotRender snapshotRender = new SnapshotRender(material);
		material.preRender(snapshotRender);
		renderer.render(material);
		material.postRender(snapshotRender);
	}

	public boolean isBatched() {
		return batched;
	}
}
