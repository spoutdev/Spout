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
package org.spout.engine.renderer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.SpoutRenderer;
import org.spout.engine.renderer.vertexbuffer.ComposedFloatBuffer;

public class GL30BatchVertexRenderer extends BatchVertexRenderer {
	final int SIZE_FLOAT = 4;
	int vao;
	int vbos = -1;

	ArrayList<Integer> assigned = new ArrayList<Integer>();
	
	ComposedFloatBuffer buffer = null;
	//TIntObjectHashMap<GLFloatBuffer > vertexBuffers = new TIntObjectHashMap<GLFloatBuffer>();

	/**
	 * Batch Renderer using OpenGL 3.0 mode.
	 * @param renderMode Mode to render in
	 */
	public GL30BatchVertexRenderer(int renderMode) {
		super(renderMode);
		vao = GL30.glGenVertexArrays();
		SpoutRenderer.checkGLError();
		//Util.checkGLError();
		//GL30.glBindVertexArray(vao); // useless
		//vertexBuffers.put(0, new VertexBufferImpl("vPosition", 4, 0)); //Auto create the first time
	}

	@Override
	protected void doFlush() {
		int size = 0;
		int []layouts = new int[buffers.size()];
		int []elements = new int[buffers.size()];
		FloatBuffer[] floatBuffers = new FloatBuffer[buffers.size()];

		int i = 0;
		for(Entry<Integer, Buffer> entry : buffers.entrySet()){
			layouts[i] = entry.getKey();
			floatBuffers[i] = (FloatBuffer) entry.getValue();
			elements[i] = floatBuffers[i].limit() / numVertices;
			size += floatBuffers[i].limit();
			i++;
		}

		FloatBuffer genericBuffer = BufferUtils.createFloatBuffer(size);
		genericBuffer.clear();

		for(int vertex = 0; vertex < numVertices; vertex++){
			for(i = 0; i < layouts.length; i++){ 	
				for(int k = 0; k < elements[i]; k++){
					genericBuffer.put(floatBuffers[i].get(vertex * elements[i] + k));
				}
			}
		}

		genericBuffer.flip();

		buffer = new ComposedFloatBuffer(elements, layouts);

		GL30.glBindVertexArray(vao);
		SpoutRenderer.checkGLError();
		
		buffer.flush(genericBuffer);
		buffer.bind();
		
		for(int layout : assigned){
			GL20.glDisableVertexAttribArray(layout);
			SpoutRenderer.checkGLError();
		}
		assigned.clear();
		
		for(int layout : buffer.getLayout()){
			GL20.glEnableVertexAttribArray(layout);
			SpoutRenderer.checkGLError();
			
			assigned.add(layout);
		}
		
		GL30.glBindVertexArray(0);
		SpoutRenderer.checkGLError();
	}

	@Override
	public void preDraw() {
	}

	/**
	 * Draws this batch
	 */
	@Override
	public void doDraw(RenderMaterial material, int startVert, int endVert) {
		GL30.glBindVertexArray(vao);
		SpoutRenderer.checkGLError();

		material.assign();

		//System.out.println("glDrawArrays");
		GL11.glDrawArrays(renderMode, startVert, endVert);
		SpoutRenderer.checkGLError();

		//GL30.glBindVertexArray(0); //Run without
	}

	@Override
	public void postDraw() {
	}

	private void dispose() {
		buffer.dispose();
	}

	public void finalize() {
		dispose();
	}

	@Override
	public void doRelease() {

	}

}