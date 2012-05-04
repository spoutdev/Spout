package org.spout.engine.renderer.shader.variables;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import org.spout.api.math.Matrix;

public class Mat4ShaderVariable extends ShaderVariable {
	Matrix value;

	public Mat4ShaderVariable(int program, String name, Matrix value) {
		super(program, name);
		this.value = value;
	}

	public Matrix get() {
		return value;
	}

	@Override
	public void assign() {
		FloatBuffer buff = BufferUtils.createFloatBuffer(4 * 4);
		buff.put(value.toArray());
		buff.flip();

		GL20.glUniformMatrix4(location, false, buff);
	}
}
