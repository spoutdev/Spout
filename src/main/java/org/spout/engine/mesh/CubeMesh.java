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
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.spout.api.model.Mesh;
import org.spout.api.model.MeshFace;
import org.spout.api.model.OrientedMeshFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;
import org.spout.api.resource.Resource;
import org.spout.engine.renderer.BatchVertexRenderer;


public class CubeMesh extends Resource implements Mesh, Iterable<OrientedMeshFace> {
	ArrayList<OrientedMeshFace> faces;
	boolean dirty = false;

	Renderer renderer;
	
	
	public CubeMesh(){
		faces = new ArrayList<OrientedMeshFace>();
	}
	
	public CubeMesh(ArrayList<OrientedMeshFace> faces){
		this.faces = faces;
	}

	protected void batch(Renderer batcher) {
		for (MeshFace face : faces) {
			for(Vertex vert : face){
				if (vert.texCoord0!=null)
					batcher.addTexCoord(vert.texCoord0);
				if (vert.normal!=null)
					batcher.addNormal(vert.normal);
				if (vert.color!=null)
					batcher.addColor(vert.color);
				batcher.addVertex(vert.position);
			}
		}
	}

	public void batch(){
		if (renderer == null)
			renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		renderer.begin();
		this.batch(renderer);
		renderer.end();
	}
	
	public void render(RenderMaterial material){
		if (renderer == null)
			throw new IllegalStateException("Cannot render without batching first!");
		
		material.preRender();
		renderer.render(material);
		material.postRender();
	}
	
	@Override
	public Iterator<OrientedMeshFace> iterator() {
		return faces.iterator();
	}
}
