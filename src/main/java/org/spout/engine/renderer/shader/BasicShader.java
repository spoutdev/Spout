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
package org.spout.engine.renderer.shader;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import org.spout.api.Spout;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Matrix;
import org.spout.api.math.Quaternion;

import org.spout.engine.SpoutClient;
import org.spout.engine.renderer.shader.variables.Mat4ShaderVariable;

public class BasicShader extends ClientShader {
	FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);


	public BasicShader() {
	 
	}

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
			}
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			matrixBuffer.clear();
			matrixBuffer.put(getProjectionMatrix().toArray());
			matrixBuffer.flip();

			GL11.glLoadMatrix(matrixBuffer);

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			matrixBuffer.clear();
			matrixBuffer.put(getViewMatrix().toArray());
			matrixBuffer.flip();

			GL11.glLoadMatrix(matrixBuffer);
		} else {
			super.assign();
		}
	}

	public void setViewMatrix(Matrix mat) {
		setUniform("View", mat);
	}

	public Matrix getViewMatrix() {
		return ((Mat4ShaderVariable) variables.get("View")).get();
	}

	public Matrix getProjectionMatrix() {
		return ((Mat4ShaderVariable) variables.get("Projection")).get();
	}

	public void setProjectionMatrix(Matrix mat) {
		setUniform("Projection", mat);
	}
}
