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

import org.spout.api.model.Mesh;
import org.spout.api.model.ModelFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderEffect;
import org.spout.api.render.Renderer;
import org.spout.api.resource.Resource;


public class BaseMesh extends Resource implements Mesh, Iterable<ModelFace> {
	ArrayList<ModelFace> faces;
	ArrayList<RenderEffect> effects = new ArrayList<RenderEffect>();
	boolean dirty = false;

	
	public BaseMesh(){
		faces = new ArrayList<ModelFace>();
		
	}
	
	public BaseMesh(ArrayList<ModelFace> faces){
		this.faces = faces;
	}
	
	public void addRenderEffect(RenderEffect effect) {
		effects.add(effect);
	}

	public void removeRenderEffect(RenderEffect effect) {
		effects.remove(effect);
	}

	public RenderEffect[] getEffects() {
		return effects.toArray(new RenderEffect[effects.size()]);
	}

	private void preBatch(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.preBatch(batcher);
		}
	}

	private void postBatch(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.postBatch(batcher);
		}
	}

	protected void batch(Renderer batcher) {
		for (ModelFace face : faces) {
			for(Vertex vert : face){
				batcher.addTexCoord(vert.texCoord0);
				batcher.addNormal(vert.normal);
				batcher.addVertex(vert.position);
				batcher.addColor(vert.color);
			}
		}
	}

	private void preRender(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.preDraw(batcher);
		}
	}

	private void postRender(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.postDraw(batcher);
		}
	}




	@Override
	public Iterator<ModelFace> iterator() {
		return faces.iterator();
	}
}
