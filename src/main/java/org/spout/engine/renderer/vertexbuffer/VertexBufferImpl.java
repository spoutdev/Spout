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
import org.lwjgl.opengl.GL15;

public class VertexBufferImpl {	
	int usage = GL15.GL_STATIC_DRAW;
	
	String elementName;
	int type;
	
	int vboId = -1;
	
	int size;
	
	int elements;
	int layout;
	
	public VertexBufferImpl(String name, int elements, int layout){
		elementName = name;
		this.elements = elements;
		this.layout = layout;
	}
	
	public void flush(FloatBuffer buffer){
		if(vboId == -1) vboId = GL15.glGenBuffers();
		
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, usage);				
		
	}
	
	public void bind(){
		if(vboId == -1) throw new IllegalStateException("Cannot bind a vertex buffer without data!");
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
				
	}
	 
	
	
	public String getName(){
		return elementName;		
	}
	
	public int getElements() {
		return elements;
	}
	
	public int getLayout() {
		return layout;
	}
	
	public void dispose() {
		if( vboId != -1 ) GL15.glDeleteBuffers(vboId);
	}
	
	public void finalize() {
		dispose();
	}
	
}
