/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.engine.renderer.vertexbuffer;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import org.spout.engine.SpoutRenderer;

public class SpoutFloatBuffer {
	private static List<Integer> BUFFER_POOL = new LinkedList<>();

	public static void initPool(int amount) {
		while (amount < BUFFER_POOL.size()) {
			BUFFER_POOL.add(GL15.glGenBuffers());
		}
	}

	public static void clearPool() {
		while (!BUFFER_POOL.isEmpty()) {
			GL15.glDeleteBuffers(BUFFER_POOL.remove(0));
			SpoutRenderer.checkGLError();
		}
	}

	public static SpoutFloatBuffer getBuffer() {
		if (BUFFER_POOL.isEmpty()) {
			return new SpoutFloatBuffer(GL15.glGenBuffers());
		} else {
			return new SpoutFloatBuffer(BUFFER_POOL.remove(0));
		}
	}

	//Buffer data
	int usage = GL15.GL_STATIC_DRAW;
	int vboId = -1;
	//VertexAttribPointer data
	int[] elements;
	int[] layout;
	int[] offset;
	int stride;
	//Flush work data
	private static int STEP = 2048;
	FloatBuffer buffer;
	int allocated = 0;
	int current = 0;
	public static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;

	private SpoutFloatBuffer(int vboId) {
		this.vboId = vboId;
	}

	public void setData(int element, int layout, FloatBuffer buffer) {
		this.elements = new int[] {element};
		this.layout = new int[] {layout};
		this.offset = new int[elements.length];

		this.offset[0] = 0;
		this.stride = elements[0] * FLOAT_SIZE;

		this.buffer = buffer;
		this.current = 0;
	}

	public void setData(int[] elements, int[] layouts, FloatBuffer buffer) {
		if (elements.length != layouts.length) {
			throw new IllegalStateException("Number of elements and layout must be same");
		}

		this.elements = elements;
		this.layout = layouts;
		this.offset = new int[elements.length];

		this.stride = 0;
		for (int i = 0; i < elements.length; i++) {
			offset[i] = stride;
			stride += elements[i] * FLOAT_SIZE;
		}

		this.buffer = buffer;
		this.current = 0;
	}

	public boolean flush(boolean force) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		SpoutRenderer.checkGLError();

		//Re/Allocate if needed
		if (buffer.limit() > allocated) {
			allocated = buffer.limit();
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, allocated * FLOAT_SIZE, usage);
			SpoutRenderer.checkGLError();
		}

		//GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 0, usage);
		//SpoutRenderer.checkGLError();
		int end;

		if (force) {
			end = buffer.capacity();
		} else {
			end = Math.min(current + STEP, buffer.capacity());
		}

		buffer.position(current);
		buffer.limit(end);

		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, current * FLOAT_SIZE, buffer);
		SpoutRenderer.checkGLError();

		current = end;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		SpoutRenderer.checkGLError();

		if (end == buffer.capacity()) {
			buffer = null;
			return true;
		}
		return false;
	}

	public void bind() {
		bind(false);
	}

	public void bind(boolean instanced) {
		if (vboId == -1) {
			throw new IllegalStateException("Cannot bind a vertex buffer without data!");
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		SpoutRenderer.checkGLError();

		for (int i = 0; i < elements.length; i++) {
			GL20.glVertexAttribPointer(layout[i], elements[i], GL11.GL_FLOAT, false, stride, offset[i]);
			SpoutRenderer.checkGLError();

			if (instanced) {
				GL33.glVertexAttribDivisor(layout[i], 1);
				SpoutRenderer.checkGLError();
			}
		}
	}

	public void unbind() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		SpoutRenderer.checkGLError();
	}

	public int[] getElements() {
		return elements;
	}

	public int[] getLayout() {
		return layout;
	}

	public void release() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		SpoutRenderer.checkGLError();

		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 0, usage);
		SpoutRenderer.checkGLError();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		SpoutRenderer.checkGLError();

		BUFFER_POOL.add(vboId);
		vboId = -1;
		allocated = 0;
	}
}
