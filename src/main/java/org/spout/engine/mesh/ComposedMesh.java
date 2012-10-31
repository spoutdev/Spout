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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.spout.api.model.MeshFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;
import org.spout.api.resource.Resource;
import org.spout.engine.renderer.BatchVertexRenderer;

public class ComposedMesh extends Resource {
	
	/**
	 * All RenderMaterial MUST have the same layer per ComposedMesh
	 */
	private Map<RenderMaterial, List<MeshFace>> facesPerMaterials;

	private Map<RenderMaterial, Renderer> renderers;

	public ComposedMesh(){
		facesPerMaterials = new HashMap<RenderMaterial, List<MeshFace>>();

		renderers = new HashMap<RenderMaterial, Renderer>();
	}

	protected void batch(Renderer batcher,RenderMaterial renderMaterial) {
		List<MeshFace> faces = facesPerMaterials.get(renderMaterial);

		if(faces != null){
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
	}

	public void batch(){
		for(RenderMaterial material : facesPerMaterials.keySet()){
			Renderer renderer = renderers.get(material);
			if(renderer == null)renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
			this.batch(renderer, material);
		}
	}

	public void render(RenderMaterial material){
		Renderer renderer = renderers.get(material);

		if(renderer == null) throw new IllegalStateException("Cannot render without batching first!");

		renderer.render(material);
	}

	public Map<RenderMaterial, Renderer> getRenderer(){
		return renderers;
	}

	public Map<RenderMaterial, List<MeshFace>> getMesh() {
		return facesPerMaterials;
	}

	public List<MeshFace> getMesh(RenderMaterial material) {
		List<MeshFace> faces = facesPerMaterials.get(material);
		if(faces == null){
			faces = new ArrayList<MeshFace>();
			facesPerMaterials.put(material, faces);
		}
		return faces;
	}

	public boolean hasVertice() {
		//Assume a material stored only if face to put in
		return !facesPerMaterials.isEmpty();
	}
}
