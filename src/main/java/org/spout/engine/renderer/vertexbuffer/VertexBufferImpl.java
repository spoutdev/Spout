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

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.spout.api.render.RenderMaterial;

public class VertexBufferImpl extends VertexBuffer {	
	static final int NULL = 0;
		
	int buffer = -1;
	int verts;
	
	private class VertexAttribute {
		public String name;
		public int offset;
		public int location;
		
	}
	
	ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>();
		
	@Override
	public void setData(FloatBuffer data, int verticies) {
		if(buffer == -1){
			buffer = GL15.glGenBuffers();
			
		}
		else
		{
			GL15.glDeleteBuffers(buffer);
			buffer = GL15.glGenBuffers();			
		}
		this.verts = verticies;
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, NULL);
				
	}
	
	public void setData(float[] data, int verticies){
		
		FloatBuffer buff = BufferUtils.createFloatBuffer(data.length);
		buff.put(data);
		buff.flip();
		
		setData(buff, verticies);		
	}

	@Override
	public void enableAttribute(String name, int location, int offset) {
		VertexAttribute a = new VertexAttribute();
		a.name = name;
		a.location = location;
		a.offset = offset;
		attributes.add(a);
	}


	@Override
	void bindBuffer() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
	}


	@Override
	public void drawBuffer(RenderMaterial material) {
		bindBuffer();
		for(VertexAttribute a : attributes){
			material.getShader().enableAttribute(a.name, 4, GL11.GL_FLOAT, 0, a.offset);
		}
		material.assign();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, verts);
		
		for(VertexAttribute a : attributes){
			GL20.glDisableVertexAttribArray(a.location);
		}
		GL20.glUseProgram(0);		
	}

}
