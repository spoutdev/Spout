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
package org.spout.engine.renderer.shader;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import org.spout.engine.renderer.shader.variables.Mat4ShaderVariable;
import org.spout.math.matrix.Matrix4f;

public class BasicShader extends ClientShader {
	FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

	public BasicShader() {

	}

	@Override
	public void assign(boolean compatabilityMode) {
		if (!variables.containsKey("Projection")) {
			throw new IllegalStateException("Basic Shader must have a projection matrix assigned");
		}
		if (!variables.containsKey("View")) {
			throw new IllegalStateException("Basic Shader must have a view matrix assigned");
		}

		if (compatabilityMode) {
			if (textures.size() > 0) {
				textures.values().iterator().next().getTexture().bind();
			} else {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			matrixBuffer.clear();
			matrixBuffer.put(getProjectionMatrix().toArray());
			matrixBuffer.flip();

			GL11.glLoadMatrix(matrixBuffer);

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			matrixBuffer.clear();
			matrixBuffer.put(getModelViewMatrix().mul(getViewMatrix()).toArray());
			matrixBuffer.flip();

			GL11.glLoadMatrix(matrixBuffer);
		} else {
			super.assign();
		}
	}

	public void setModelViewMatrix(Matrix4f mat) {
		setUniform("Model", mat);
	}

	public Matrix4f getModelViewMatrix() {
		return ((Mat4ShaderVariable) variables.get("Model")).get();
	}

	public void setViewMatrix(Matrix4f mat) {
		setUniform("View", mat);
	}

	public Matrix4f getViewMatrix() {
		return ((Mat4ShaderVariable) variables.get("View")).get();
	}

	public Matrix4f getProjectionMatrix() {
		return ((Mat4ShaderVariable) variables.get("Projection")).get();
	}

	public void setProjectionMatrix(Matrix4f mat) {
		setUniform("Projection", mat);
	}
}
