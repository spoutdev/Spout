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

import gnu.trove.list.array.TFloatArrayList;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.render.BufferContainer;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;
import org.spout.engine.renderer.vertexbuffer.SpoutFloatBuffer;

public abstract class BatchVertexRenderer implements Renderer {

	public static HashMap<Integer,List<Renderer>> pool = new HashMap<Integer,List<Renderer>>();

	public static void initPool(int renderMode, int number){
		List<Renderer> list = pool.get(renderMode);

		if(list == null){
			list = new LinkedList<Renderer>();
			pool.put(renderMode, list);
		}

		Client client = (Client) Spout.getEngine();

		switch (client.getRenderMode()) {
			case GL11:
				for(int i = 0; i < number; i++)
					list.add(new GL11BatchVertexRenderer(renderMode));
				return;
			case GL20:
				for(int i = 0; i < number; i++)
					list.add(new GL20BatchVertexRenderer(renderMode));
				return;
			case GL30:
				for(int i = 0; i < number; i++)
					list.add(new GL30BatchVertexRenderer(renderMode));
				return;
			case GL40:
				for(int i = 0; i < number; i++)
					list.add(new GL30BatchVertexRenderer(renderMode));
				return;
			case GLES20:
				for(int i = 0; i < number; i++)
					list.add(new GLES20BatchVertexRenderer(renderMode));
				return;
			default:
				throw new IllegalArgumentException("GL Mode:" + client.getRenderMode() + " Not reconized");
		}
	}

	public static Renderer constructNewBatch(int renderMode) {
		List<Renderer> list = pool.get(renderMode);
		if(list != null && !list.isEmpty()){
			Renderer batch = list.remove(0);
			((BatchVertexRenderer)batch).used++;
			return batch;
		}

		Client client = (Client) Spout.getEngine();

		switch (client.getRenderMode()) {
			case GL11:
				return new GL11BatchVertexRenderer(renderMode);
			case GL20:
				return new GL20BatchVertexRenderer(renderMode);
			case GL30:
				return new GL30BatchVertexRenderer(renderMode);
			case GL40:
				return new GL30BatchVertexRenderer(renderMode);
			case GLES20:
				return new GLES20BatchVertexRenderer(renderMode);
			default:
				throw new IllegalArgumentException("GL Mode:" + client.getRenderMode() + " Not reconized");
		}
	}

	final int renderMode;

	SpoutFloatBuffer currentBuffer = null;
	SpoutFloatBuffer flushingBuffer = null;
	int currentNumVertices = 0;
	int flushingNumVertices = 0;
	
	int used = 1; // Benchmark

	public BatchVertexRenderer(int mode) {
		renderMode = mode;
	}

	public int getVertexCount(){
		return currentNumVertices;
	}

	protected abstract boolean doFlush(boolean force);

	public final boolean flush(boolean force) {
		if(doFlush(force)){
			if(currentBuffer != null)
				currentBuffer.release();
			currentBuffer = flushingBuffer;
			flushingBuffer = null;
			
			currentNumVertices = flushingNumVertices;
			flushingNumVertices = 0;
			
			return true;
		}
		return false;
	}
	
	/**
	 * The act of drawing.  The Batch will check if it's possible to render
	 * as well as setup for rendering.  If it's possible to render, it will call doRender()
	 */
	protected abstract void doDraw(RenderMaterial material, int startVert, int endVert);
	
	@Override
	public void draw(RenderMaterial material){
		draw(material, 0, getVertexCount());
	}

	@Override
	public void draw(RenderMaterial material, int startVert, int endVert){
		if(getVertexCount() <= 0) throw new IllegalStateException("Cannot render 0 verticies");
		doDraw(material, startVert, endVert);
	}

	@Override	
	public final void render(RenderMaterial material) {
		render(material, 0, getVertexCount());
	}

	public final void render(RenderMaterial material, int startVert, int endVert) {
		if(getVertexCount() <= 0) throw new IllegalStateException("Cannot render 0 verticies");
		preDraw();
		doDraw(material, startVert, endVert);
		postDraw();
	}

	public abstract void doRelease();
	
	public void release() {
		//TODO : Implement for each version to empty display list, vbo, etc...

		List<Renderer> list = pool.get(renderMode);
		if(list == null){
			list = new LinkedList<Renderer>();
			pool.put(renderMode, list);
		}
		list.add(this);
	}

	public void finalize() { }

	protected abstract void initFlush(Map<Integer,Buffer> buffers);
	
	public void setBufferContainer(BufferContainer bufferContainer) {
		Map<Integer,Buffer> buffers = new HashMap<Integer, Buffer>();

		for(Entry<Integer, Object> entry : bufferContainer.getBuffers().entrySet()){
			int layout = entry.getKey();
			Object buffer = entry.getValue();

			if(buffer instanceof TFloatArrayList){

				if(((TFloatArrayList) buffer).isEmpty())
					throw new IllegalStateException("Buffer can't be empty");

				FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(((TFloatArrayList) buffer).size());
				floatBuffer.clear();
				floatBuffer.put(((TFloatArrayList)buffer).toArray());
				floatBuffer.flip();

				buffers.put(layout, floatBuffer);

			}else{
				throw new IllegalStateException("Buffer different of TFloatArrayList not yet supported");
			}
		}

		flushingNumVertices = bufferContainer.element;
		
		initFlush(buffers);
	}

	public void setBufferContainers(BufferContainer []bufferContainers) {
		Map<Integer,Buffer> buffers = new HashMap<Integer, Buffer>();

		int first = 0;
		
		while(first < bufferContainers.length && bufferContainers[first] == null)
			first++;
		
		if(first == bufferContainers.length)
			throw new IllegalStateException("BufferContainers array can't be fully empty");
		
		//For each layout
		for(Entry<Integer, Object> entry : bufferContainers[first].getBuffers().entrySet()){

			int layout = entry.getKey();
			int count = 0;

			if(entry.getValue() instanceof TFloatArrayList){

				//First pass, count element
				for(BufferContainer bufferContainer : bufferContainers){
					if(bufferContainer == null) continue;
					Object buffer = bufferContainer.getBuffers().get(layout);
					count += ((TFloatArrayList)buffer).size();
				}

				//Allocate with result (to avoid constant reallocation during copy)
				FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(count);
				floatBuffer.clear();

				//Second pass, add element
				for(BufferContainer bufferContainer : bufferContainers){
					if(bufferContainer == null) continue;
					Object buffer = bufferContainer.getBuffers().get(layout);
					floatBuffer.put(((TFloatArrayList)buffer).toArray());
				}

				floatBuffer.flip();

				buffers.put(layout, floatBuffer);

			}else{
				throw new IllegalStateException("Buffer different of TFloatArrayList not yet supported");
			}
		}

		//Count vertices
		flushingNumVertices = 0;	
		for(BufferContainer bufferContainer : bufferContainers)
			if(bufferContainer != null)
				flushingNumVertices += bufferContainer.element;
		
		initFlush(buffers);
	}
	
	public void setGLBufferContainer(GLBufferContainer container) {
		Map<Integer,Buffer> buffers = new HashMap<Integer, Buffer>();

		for(Entry<Integer, Buffer> entry : container.getBuffers().entrySet())
			buffers.put(entry.getKey(), entry.getValue());

		flushingNumVertices = container.element;
		
		initFlush(buffers);
	}
}
