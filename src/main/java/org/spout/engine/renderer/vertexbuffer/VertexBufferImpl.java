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
package org.spout.engine.renderer.vertexbuffer;

import gnu.trove.list.array.TIntArrayList;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.spout.api.render.RenderMaterial;

public class VertexBufferImpl extends VertexBuffer {
	
	
	static final int NULL = 0;
	
	static TIntArrayList registeredBuffers = new TIntArrayList();
	static int genBuffer(){
		int buffer = GL15.glGenBuffers();
		registeredBuffers.add(buffer);
		
		return buffer;
	}
	
	
	int buffer;
	int verts;
	
	
	
	private class VertexAttribute {
		public int offset;
		public int location;
		
	}
	
	
	ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>();
	
	
	
	
	@Override
	public void setData(FloatBuffer data, int verticies) {
		
		buffer = VertexBufferImpl.genBuffer();
		this.verts = verticies;
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, NULL);
		
		attributes.clear();
	}

	@Override
	public void enableAttribute(int location, int offset) {
		VertexAttribute a = new VertexAttribute();
		a.location = location;
		a.offset = offset;
		attributes.add(a);
				

	}


	@Override
	public void bindBuffer() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		

		for(VertexAttribute a : attributes){
			GL20.glEnableVertexAttribArray(a.location);
			GL20.glVertexAttribPointer(a.location, 4, GL11.GL_FLOAT, false, 0, a.offset);
		}
		
		
	}


	@Override
	public void drawBuffer(RenderMaterial material) {
		material.assign();
		
		bindBuffer();
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, verts);
		
		for(VertexAttribute a : attributes){
			GL20.glDisableVertexAttribArray(a.location);
		}
		GL20.glUseProgram(0);
		
	}

}
