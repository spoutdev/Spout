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

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.array.TFloatArrayList;

import java.awt.Color;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Renderer;
import org.spout.api.render.Shader;

public abstract class BatchVertexRenderer implements Renderer {
	public static Renderer constructNewBatch(int renderMode) {
		Client client = (Client) Spout.getEngine();
		if (client.getRenderMode() == RenderMode.GL11) {
			return new GL11BatchVertexRenderer(renderMode);
		}
		if (client.getRenderMode() == RenderMode.GL20) {
			return new GL20BatchVertexRenderer(renderMode);
		}
		if (client.getRenderMode() == RenderMode.GL30) {
			return new GL30BatchVertexRenderer(renderMode);
		}
		if (client.getRenderMode() == RenderMode.GLES20) {
			return new GLES20BatchVertexRenderer(renderMode);
		}
		throw new IllegalArgumentException("GL Mode:" + client.getRenderMode() + " Not reconized");
	}

	boolean batching = false;
	boolean flushed = false;
	int renderMode;
	//Using FloatArrayList because I need O(1) access time
	//and fast ToArray()
	TFloatArrayList vertexBuffer = new TFloatArrayList();
	TFloatArrayList colorBuffer = new TFloatArrayList();
	TFloatArrayList normalBuffer = new TFloatArrayList();
	TFloatArrayList uvBuffer = new TFloatArrayList();
	int numVertices = 0;
	boolean useColors = false;
	boolean useNormals = false;
	boolean useTextures = false;
	RenderMaterial activeMaterial = null;

	public BatchVertexRenderer(int mode) {
		renderMode = mode;
	}

	
	public int getVertexCount(){
		return numVertices;
	}
	
	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#begin()
		 */
	@Override
	public void begin(RenderMaterial material) {
		if (batching) {
			throw new IllegalStateException("Already Batching!");
		}
		batching = true;
		flushed = false;
		vertexBuffer.clear();
		colorBuffer.clear();
		normalBuffer.clear();
		uvBuffer.clear();

		numVertices = 0;
		
		this.activeMaterial = material;
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#end()
		 */
	@Override
	public void end() {
		if (!batching) {
			throw new IllegalStateException("Not Batching!");
		}
		batching = false;
		flush();
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#flush()
		 */
	public final void flush() {
		if (vertexBuffer.size() % 4 != 0) {
			throw new IllegalStateException("Vertex Size Mismatch (How did this happen?)");
		}
		if (useColors) {
			if (colorBuffer.size() % 4 != 0) {
				throw new IllegalStateException("Color Size Mismatch (How did this happen?)");
			}
			if (colorBuffer.size() / 4 != numVertices) {
				throw new IllegalStateException("Color Buffer size does not match numVerticies");
			}
		}
		if (useNormals) {
			if (normalBuffer.size() % 4 != 0) {
				throw new IllegalStateException("Normal Size Mismatch (How did this happen?)");
			}
			if (normalBuffer.size() / 4 != numVertices) {
				throw new IllegalStateException("Normal Buffer size does not match numVerticies");
			}
		}
		if (useTextures) {
			if (uvBuffer.size() % 2 != 0) {
				throw new IllegalStateException("UV size Mismatch (How did this happen?)");
			}
			if (uvBuffer.size() / 2 != numVertices) {
				throw new IllegalStateException("UV Buffer size does not match numVerticies");
			}
		}
		if(numVertices <= 0) return;
		//Call the overriden flush
		doFlush();

		//clean up after flush
		postFlush();
	}

	protected abstract void doFlush();

	protected void postFlush() {
		flushed = true;
		vertexBuffer.clear();
		colorBuffer.clear();
		normalBuffer.clear();
		uvBuffer.clear();
	}

	/**
	 * The act of drawing.  The Batch will check if it's possible to render
	 * as well as setup for rendering.  If it's possible to render, it will call doRender()
	 */
	protected abstract void doRender();

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#render()
		 */
	@Override
	public final void render() {
		checkRender();
		if(numVertices <= 0) return;
		doRender();
	}

	protected void checkRender() {
		if (batching) {
			throw new IllegalStateException("Cannot Render While Batching");
		}
		if (!flushed) {
			throw new IllegalStateException("Cannon Render Without Flushing the Batch");
		}
		
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addVertex(float, float, float, float)
		 */
	@Override
	public void addVertex(float x, float y, float z, float w) {
		vertexBuffer.add(x);
		vertexBuffer.add(y);
		vertexBuffer.add(z);
		vertexBuffer.add(w);

		numVertices++;
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addVertex(float, float, float)
		 */
	@Override
	public void addVertex(float x, float y, float z) {
		addVertex(x, y, z, 1.0f);
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addVertex(float, float)
		 */
	@Override
	public void addVertex(float x, float y) {
		addVertex(x, y, 0.0f, 1.0f);
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addVertex(org.spout.api.math.Vector3)
		 */
	@Override
	public void addVertex(Vector3 vertex) {
		addVertex(vertex.getX(), vertex.getY(), vertex.getZ());
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#AddVertex(org.spout.api.math.Vector2)
		 */
	@Override
	public void addVertex(Vector2 vertex) {
		addVertex(vertex.getX(), vertex.getY());
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#AddVertex(org.spout.api.math.Vector4)
		 */
	@Override
	public void addVertex(Vector4 vertex) {
		addVertex(vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getZ());
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addColor(float, float, float)
		 */
	@Override
	public void addColor(float r, float g, float b) {
		addColor(r, g, b, 1.0f);
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addColor(float, float, float, float)
		 */
	@Override
	public void addColor(float r, float g, float b, float a) {
		if (!useColors) {
			this.enableColors();
		}
		colorBuffer.add(r);
		colorBuffer.add(g);
		colorBuffer.add(b);
		colorBuffer.add(a);
	}

	public void addColor(int r, int g, int b, int a) {
		addColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addColor(org.spout.api.util.Color)
		 */
	@Override
	public void addColor(Color color) {
		addColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addNormal(float, float, float, float)
		 */
	@Override
	public void addNormal(float x, float y, float z, float w) {
		if (!useNormals) {
			this.enableNormals();
		}
		normalBuffer.add(x);
		normalBuffer.add(y);
		normalBuffer.add(z);
		normalBuffer.add(w);
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addNormal(float, float, float)
		 */
	@Override
	public void addNormal(float x, float y, float z) {
		addNormal(x, y, z, 1.0f);
	}

	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addNormal(org.spout.api.math.Vector3)
	 */
	@Override
	public void addNormal(Vector3 vertex) {
		addNormal(vertex.getX(), vertex.getY(), vertex.getZ());
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addNormal(org.spout.api.math.Vector4)
		 */
	@Override
	public void addNormal(Vector4 vertex) {
		addNormal(vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getZ());
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addTexCoord(float, float)
		 */
	@Override
	public void addTexCoord(float u, float v) {
		if (!this.useTextures) {
			this.enableTextures();
		}
		uvBuffer.add(u);
		uvBuffer.add(v);
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#addTexCoord(org.spout.api.math.Vector2)
		 */
	@Override
	public void addTexCoord(Vector2 uv) {
		addTexCoord(uv.getX(), uv.getY());
	}


	@Override
	public Shader getShader() {
		return activeMaterial.getShader();
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#enableColors()
		 */
	@Override
	public void enableColors() {
		useColors = true;
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#enableNormals()
		 */
	@Override
	public void enableNormals() {
		useNormals = true;
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.Renderer#enableTextures()
		 */
	@Override
	public void enableTextures() {
		useTextures = true;
	}

	public void dumpBuffers() {
		System.out.println("BatchVertexRenderer Debug Ouput: Verts: " + numVertices + " Using {colors, normal, textures} {" + useColors + ", " + useNormals + ", " + useTextures + "}");
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
		}
	}
}
