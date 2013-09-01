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
package org.spout.engine.renderer.shader.variables;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import org.spout.engine.SpoutRenderer;
import org.spout.math.matrix.Matrix3;

public class Mat3ShaderVariable extends ShaderVariable {
	public static final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
	Matrix3 value;

	public Mat3ShaderVariable(int program, String name, Matrix3 value) {
		super(program, name);
		this.value = value;
	}

	public Matrix3 get() {
		return value;
	}

	@Override
	public void assign() {
		buffer.position(0);
		buffer.put(value.toArray(true));
		buffer.flip();
		GL20.glUniformMatrix3(location, false, buffer);
		SpoutRenderer.checkGLError();
	}
}
