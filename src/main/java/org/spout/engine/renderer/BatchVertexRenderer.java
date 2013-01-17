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
		case GLES20:
			return new GLES20BatchVertexRenderer(renderMode);
		default:
			throw new IllegalArgumentException("GL Mode:" + client.getRenderMode() + " Not reconized");
		}
	}

	boolean batching = false;
	boolean flushed = false;
	final int renderMode;

	public static final int VERTEX_LAYER = 0;
	public static final int COLOR_LAYER = 1;
	public static final int NORMAL_LAYER = 2;
	public static final int TEXTURE0_LAYER = 3;
	public static final int TEXTURE1_LAYER = 4;

	final Map<Integer,Buffer> buffers = new HashMap<Integer, Buffer>();
	int numVertices = 0;

	int used = 1; // Benchmark

	public BatchVertexRenderer(int mode) {
		renderMode = mode;
	}

	public int getVertexCount(){
		return numVertices;
	}

	@Override
	public void begin() {
		if (batching) {
			throw new IllegalStateException("Already Batching!");
		}
		batching = true;
		flushed = false;
	}

	@Override
	public void end() {
		if (!batching) {
			throw new IllegalStateException("Not Batching!");
		}
		batching = false;
		flush();
	}

	public void check(){
		/*if (vertexBuffer.size() % 4 != 0) {
			throw new IllegalStateException("Vertex Size Mismatch (How did this happen?) : " + vertexBuffer.size());
		}
		if (useColors) {
			if (colorBuffer.size() % 4 != 0) {
				throw new IllegalStateException("Color Size Mismatch (How did this happen?) : " + colorBuffer.size());
			}
			if (colorBuffer.size() / 4 != numVertices) {
				throw new IllegalStateException("Color Buffer size does not match numVerticies : " + colorBuffer.size());
			}
		}
		if (useNormals) {
			if (normalBuffer.size() % 4 != 0) {
				throw new IllegalStateException("Normal Size Mismatch (How did this happen?) : " + normalBuffer.size());
			}
			if (normalBuffer.size() / 4 != numVertices) {
				throw new IllegalStateException("Normal Buffer size does not match numVerticies : " + normalBuffer.size());
			}
		}
		if (useTextures) {
			if (uvBuffer.size() % 2 != 0) {
				throw new IllegalStateException("UV size Mismatch (How did this happen?) : " + uvBuffer.size());
			}
			if (uvBuffer.size() / 2 != numVertices) {
				throw new IllegalStateException("UV Buffer size does not match numVerticies : " + uvBuffer.size());
			}
		}
		if(numVertices <= 0) throw new IllegalStateException("Must have more than 0 verticies!");*/
	}

	public final void flush() {
		check();

		//Call the overriden flush
		doFlush();

		//clean up after flush
		postFlush();
	}

	protected abstract void doFlush();

	protected void postFlush() {
		flushed = true;
		buffers.clear();
	}

	/**
	 * The act of drawing.  The Batch will check if it's possible to render
	 * as well as setup for rendering.  If it's possible to render, it will call doRender()
	 */
	protected abstract void doRender(RenderMaterial material, int startVert, int endVert);

	public final void render(RenderMaterial material, int startVert, int endVert) {
		checkRender();
		if(getVertexCount() <= 0) throw new IllegalStateException("Cannot render 0 verticies");
		doRender(material, startVert, endVert);
	}

	@Override	
	public final void render(RenderMaterial material) {
		render(material, 0, getVertexCount());
	}

	protected void checkRender() {
		if (batching) {
			throw new IllegalStateException("Cannot Render While Batching");
		}
		if (!flushed) {
			throw new IllegalStateException("Cannot Render Without Flushing the Batch");
		}
	}

	public void dumpBuffers() {
		/*System.out.println("BatchVertexRenderer Debug Ouput: Verts: " + numVertices + " Using {colors, normal, textures} {" + useColors + ", " + useNormals + ", " + useTextures + "}");
		System.out.println("colors:"+colorBuffer.size()+", normals:"+normalBuffer.size()+", vertices:"+vertexBuffer.size());
		for (int i = 0; i < numVertices; i++) {
			int index = i * 4;

			if (useColors) {
				System.out.print("Color: {" + colorBuffer.get(index) + " " + colorBuffer.get(index + 1) + " " + colorBuffer.get(index + 2) + " " + colorBuffer.get(index + 3) + "}\t");
			}
			if (useNormals) {
				System.out.print("Normal : {" + normalBuffer.get(index) + " " + normalBuffer.get(index + 1) + " " + normalBuffer.get(index + 2) + "}");
			}
			if (useTextures) {
				System.out.print("TexCoord0 : {" + uvBuffer.get((i * 2)) + " " + uvBuffer.get((i * 2) + 1) + "}");
			}
			System.out.println("Vertex : {" + vertexBuffer.get(index) + " " + vertexBuffer.get(index + 1) + " " + vertexBuffer.get(index + 2) + " " + vertexBuffer.get(index + 3) + "}");
		}*/
	}

	public void release() { 
		batching = false;
		flushed = false;
		buffers.clear();

		//TODO : Implement for each version to empty display list, vbo, etc...

		List<Renderer> list = pool.get(renderMode);
		if(list == null){
			list = new LinkedList<Renderer>();
			pool.put(renderMode, list);
		}
		list.add(this);
	}

	public void finalize() { }

	public void setBufferContainer(BufferContainer bufferContainer) {
		buffers.clear();

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

		numVertices = bufferContainer.element;
	}

	public void setBufferContainers(BufferContainer []bufferContainers) {
		buffers.clear();

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
		numVertices = 0;	
		for(BufferContainer bufferContainer : bufferContainers)
			if(bufferContainer != null)
				numVertices += bufferContainer.element;	
	}
	
	public void setGLBufferContainer(GLBufferContainer container) {
		buffers.clear();

		for(Entry<Integer, Buffer> entry : container.getBuffers().entrySet())
			buffers.put(entry.getKey(), entry.getValue());

		numVertices = container.element;
	}
}
