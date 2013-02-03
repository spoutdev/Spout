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
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.SpoutRenderer;
import org.spout.engine.renderer.vertexbuffer.SpoutFloatBuffer;

public class GL20BatchVertexRenderer extends BatchVertexRenderer {
	//final int SIZE_FLOAT = 4;
	
	/**
	 * Batch Renderer using OpenGL 2.0 mode.
	 * @param renderMode Mode to render in
	 */
	public GL20BatchVertexRenderer(int renderMode) {
		super(renderMode);
	}

	@Override
	protected void initFlush(Map<Integer,Buffer> buffers){
		int size = 0;
		int []layouts = new int[buffers.size()];
		int []elements = new int[buffers.size()];
		FloatBuffer[] floatBuffers = new FloatBuffer[buffers.size()];

		int i = 0;
		for(Entry<Integer, Buffer> entry : buffers.entrySet()){
			layouts[i] = entry.getKey();
			floatBuffers[i] = (FloatBuffer) entry.getValue();
			elements[i] = floatBuffers[i].limit() / flushingNumVertices;
			size += floatBuffers[i].limit();
			i++;
		}
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		buffer.clear();

		for(int vertex = 0; vertex < flushingNumVertices; vertex++){
			for(i = 0; i < layouts.length; i++){ 	
				for(int k = 0; k < elements[i]; k++){
					buffer.put(floatBuffers[i].get(vertex * elements[i] + k));
				}
			}
		}
		
		buffer.flip();
		
		if(flushingBuffer == null)
			flushingBuffer = SpoutFloatBuffer.getBuffer();
		
		flushingBuffer.setData(elements, layouts, buffer);
	}
	
	@Override
	protected boolean doFlush(boolean force) {
		return flushingBuffer.flush(force);
	}

	@Override
	public void preDraw() {
		for(int layout : currentBuffer.getLayout()){
			GL20.glEnableVertexAttribArray(layout);
			SpoutRenderer.checkGLError();
		}
	}

	/**
	 * Draws this batch
	 */
	@Override
	public void doDraw(RenderMaterial material, int startVert, int endVert) {
		material.assign();

		currentBuffer.bind();

		GL11.glDrawArrays(renderMode, startVert, endVert);
		SpoutRenderer.checkGLError();

		currentBuffer.unbind();
	}

	@Override
	public void postDraw() {
		for(int layout : currentBuffer.getLayout()){
			GL20.glDisableVertexAttribArray(layout);
			SpoutRenderer.checkGLError();
		}
	}

	@Override
	public void doRelease(){
		if(currentBuffer != null){
			currentBuffer.release();
			currentBuffer = null;
		}
		if(flushingBuffer != null){
			flushingBuffer.release();
			flushingBuffer = null;
		}
	}

	public void finalize() {
		if(currentBuffer != null){
			currentBuffer.release();
			currentBuffer = null;
		}
		if(flushingBuffer != null){
			flushingBuffer.release();
			flushingBuffer = null;
		}
	}
}
