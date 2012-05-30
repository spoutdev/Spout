<<<<<<< HEAD
/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import org.lwjgl.opengl.GL11;

import org.spout.engine.renderer.shader.BasicShader;

public class GL11BatchVertexRenderer extends BatchVertexRenderer {
	int displayList;

	public GL11BatchVertexRenderer(int mode) {
		super(mode);
		displayList = GL11.glGenLists(1);
	}

	@Override
	protected void doFlush() {
		//TODO: Use fallback material modes for this
		if (!(activeMaterial.getShader() instanceof BasicShader)) {
			throw new IllegalStateException("Need Basic Shader in 1.1 mode");
		}

		GL11.glNewList(displayList, GL11.GL_COMPILE);
		((BasicShader) activeMaterial.getShader()).assign(true);
		GL11.glPushMatrix();
		GL11.glBegin(renderMode);
		for (int i = 0; i < numVerticies; i += 1) {
			int index = i * 4;
			if (useColors) {
				GL11.glColor3f(colorBuffer.get(index), colorBuffer.get(index + 1), colorBuffer.get(index + 2));
			}
			if (useNormals) {
				GL11.glNormal3f(normalBuffer.get(index), normalBuffer.get(index + 1), normalBuffer.get(index + 2));
			}
			if (useTextures) {
				GL11.glTexCoord2f(uvBuffer.get((i * 2)), uvBuffer.get((i * 2) + 1));
			}
			GL11.glVertex4f(vertexBuffer.get(index), vertexBuffer.get(index + 1), vertexBuffer.get(index + 2), vertexBuffer.get(index + 3));
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEndList();
	}

	@Override
	public void doRender() {

		GL11.glPushMatrix();
		GL11.glCallList(displayList);
		GL11.glPopMatrix();
	}
}
=======
package org.spout.engine.renderer;

import org.lwjgl.opengl.GL11;

import org.spout.engine.renderer.shader.BasicShader;

public class GL11BatchVertexRenderer extends BatchVertexRenderer {
	int displayList;

	public GL11BatchVertexRenderer(int mode) {
		super(mode);
		displayList = GL11.glGenLists(1);
	}

	@Override
	protected void doFlush() {
		if (!(activeShader instanceof BasicShader)) {
			throw new IllegalStateException("Need Basic Shader in 1.1 mode");
		}

		GL11.glNewList(displayList, GL11.GL_COMPILE);
		((BasicShader) activeShader).assign(true);
		GL11.glPushMatrix();
		GL11.glBegin(renderMode);
		for (int i = 0; i < numVerticies; i += 1) {
			int index = i * 4;
			if (useColors) {
				GL11.glColor3f(colorBuffer.get(index), colorBuffer.get(index + 1), colorBuffer.get(index + 2));
			}
			if (useNormals) {
				GL11.glNormal3f(normalBuffer.get(index), normalBuffer.get(index + 1), normalBuffer.get(index + 2));
			}
			if (useTextures) {
				GL11.glTexCoord2f(uvBuffer.get((i * 2)), uvBuffer.get((i * 2) + 1));
			}
			GL11.glVertex4f(vertexBuffer.get(index), vertexBuffer.get(index + 1), vertexBuffer.get(index + 2), vertexBuffer.get(index + 3));
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEndList();
	}

	@Override
	public void doRender() {

		GL11.glPushMatrix();
		GL11.glCallList(displayList);
		GL11.glPopMatrix();
	}
}
>>>>>>> parent of 751169a... Cleanup of SpoutEngine. You are welcome @RoyAwesome.
