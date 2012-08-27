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
package org.spout.engine.renderer;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.renderer.vertexbuffer.VertexBufferImpl;

public class GL20BatchVertexRenderer extends BatchVertexRenderer {
	final int SIZE_FLOAT = 4;
	int vbos = -1;

	TIntObjectHashMap<VertexBufferImpl > vertexBuffers = new TIntObjectHashMap<VertexBufferImpl>();
	
	
	/**
	 * Batch Renderer using OpenGL 3.0 mode.
	 * @param renderMode Mode to render in
	 */
	public GL20BatchVertexRenderer(int renderMode) {
		super(renderMode);
		
		vertexBuffers.put(0, new VertexBufferImpl("vPosition", 4, 0));
	}

	@Override
	protected void doFlush() {
		
		FloatBuffer vBuffer = BufferUtils.createFloatBuffer(vertexBuffer.size());
		vBuffer.clear();
		vBuffer.put(vertexBuffer.toArray());
		vBuffer.flip();
		
		vertexBuffers.get(0).flush(vBuffer);
		
		
		if (useColors) {
			if(vertexBuffers.get(1) == null) {
				vertexBuffers.put(1, new VertexBufferImpl("vColor", 4, 1));
			}
			
			vBuffer.clear();
			vBuffer.put(colorBuffer.toArray());
			vBuffer.flip();
			
			vertexBuffers.get(1).flush(vBuffer);
			
			
		}
		if (useNormals) {
		
			if(vertexBuffers.get(2) == null) {
				vertexBuffers.put(2, new VertexBufferImpl("vNormal", 4, 2));
			}
			
			vBuffer.clear();
			vBuffer.put(normalBuffer.toArray());
			vBuffer.flip();
			
			vertexBuffers.get(2).flush(vBuffer);
		}
		if (useTextures) {
			
			if(vertexBuffers.get(3) == null) {
				vertexBuffers.put(3, new VertexBufferImpl("vTexCoord0", 2, 3));
			}
			
			vBuffer = BufferUtils.createFloatBuffer(uvBuffer.size());
			vBuffer.clear();
			vBuffer.put(uvBuffer.toArray());
			vBuffer.flip();
			
			vertexBuffers.get(3).flush(vBuffer);
		}

		
	}

	/**
	 * Draws this batch
	 */
	@Override
	public void doRender(RenderMaterial material) {

		
		material.assign();
		
		for(VertexBufferImpl vb : vertexBuffers.valueCollection()){
			vb.bind();
			GL20.glEnableVertexAttribArray(vb.getLayout());
			GL20.glVertexAttribPointer(vb.getLayout(), vb.getElements(), GL11.GL_FLOAT, false, 0, 0);
			//activeMaterial.getShader().enableAttribute(vb.getName(), vb.getElements(), GL11.GL_FLOAT, 0, 0, vb.getLayout());			
		}
	
		GL11.glDrawArrays(renderMode, 0, numVertices);
	
		
		for(VertexBufferImpl vb : vertexBuffers.valueCollection()){			
			GL20.glDisableVertexAttribArray(vb.getLayout());		
		}
		
	}
}
