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
import org.lwjgl.opengl.GL11;

import org.spout.api.render.RenderMaterial;
//TODO update this to work better
public class GL11BatchVertexRenderer extends BatchVertexRenderer {
	int displayList;

	public GL11BatchVertexRenderer(int mode) {
		super(mode);
		displayList = GL11.glGenLists(1);
	}

	@Override
	protected void doFlush() {
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		FloatBuffer vBuffer = BufferUtils.createFloatBuffer(vertexBuffer.size());

		vBuffer.clear();
		vBuffer.put(vertexBuffer.toArray());
		vBuffer.flip();

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(4, 0, vBuffer);

		/*if (useColors) {
			vBuffer.clear();
			vBuffer.put(colorBuffer.toArray());
			vBuffer.flip();
			
			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
			GL11.glColorPointer(4, 0, vBuffer);
		}*/

		if (useNormals) {
			vBuffer.clear();
			vBuffer.put(normalBuffer.toArray());
			vBuffer.flip();

			GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
			GL11.glNormalPointer(0, vBuffer);
		}
		
		if (useTextures) {
			FloatBuffer temp = BufferUtils.createFloatBuffer(uvBuffer.size());
			temp.put(uvBuffer.toArray());
			temp.flip();

			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL11.glTexCoordPointer(2, 0, temp);
		}

		GL11.glDrawArrays(renderMode, 0, numVertices);
		
		GL11.glEndList();
	}

	@Override
	public void doRender(RenderMaterial material, int startVert, int endVert) {
		material.assign();
		GL11.glCallList(displayList);
	}
	
	public void finalize() {
		 GL11.glDeleteLists(displayList, 1);
	}
}
