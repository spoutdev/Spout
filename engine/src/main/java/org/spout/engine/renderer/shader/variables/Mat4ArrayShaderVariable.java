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
import org.spout.math.matrix.Matrix4;

public class Mat4ArrayShaderVariable extends ShaderVariable {
	Matrix4[] value;

	public Mat4ArrayShaderVariable(int program, String name, Matrix4[] value) {
		super(program, name);
		this.value = value;
	}

	public Matrix4[] get() {
		return value;
	}

	@Override
	public void assign() {
		FloatBuffer buff = BufferUtils.createFloatBuffer(16 * value.length);

		for (int i = 0; i < value.length; i++) {
			buff.put(value[i].toArray());
		}
		buff.flip();

		GL20.glUniformMatrix4(location, false, buff);
		SpoutRenderer.checkGLError();
	}
}
