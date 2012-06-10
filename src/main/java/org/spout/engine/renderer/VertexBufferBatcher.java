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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.spout.engine.renderer.vertexbuffer.VertexBuffer;

public class VertexBufferBatcher extends BatchVertexRenderer {
	final static int SIZE_FLOAT = 4;
	final static int VERTEX_LOCATION = 0;
	final static int COLOR_LOCATION = 1;
	final static int NORMAL_LOCATION = 2;
	final static int TEXCOORD0_LOCATION = 3;
	
	
	
	VertexBuffer buffer;
	
	public VertexBufferBatcher(int mode, VertexBuffer buffer) {
		super(mode);
		this.buffer = buffer;
	}

	@Override
	protected void doFlush() {
		//Calculate the size
		int size = numVerticies * 4 * SIZE_FLOAT;
		
		if (useColors) {
			size += numVerticies * 4 * SIZE_FLOAT;			
		}
		if (useNormals) {
			size += numVerticies * 4 * SIZE_FLOAT;			
		}
		if (useTextures) {
			size += numVerticies * 2 * SIZE_FLOAT;			
		}
		FloatBuffer verts = BufferUtils.createFloatBuffer(size);
		verts.put(vertexBuffer.toArray());
		int offset = 0;
		buffer.enableAttribute(VERTEX_LOCATION, offset);
		if(useColors){
			verts.put(colorBuffer.toArray());
			offset += this.numVerticies * SIZE_FLOAT * 4;
			buffer.enableAttribute(COLOR_LOCATION, offset);
		}
		if(useNormals){
			verts.put(normalBuffer.toArray());
			offset += this.numVerticies * SIZE_FLOAT * 4;
			buffer.enableAttribute(NORMAL_LOCATION, offset);
		}
		if(useTextures){
			verts.put(uvBuffer.toArray());
			offset += this.numVerticies * SIZE_FLOAT * 2;
			buffer.enableAttribute(TEXCOORD0_LOCATION, offset);
		}
		
		verts.flip();
		
		this.buffer.setData(verts, numVerticies);
		
		
		
		
	}

	@Override
	protected void doRender() {
		this.buffer.drawBuffer(this.activeMaterial);
		
	}

}
