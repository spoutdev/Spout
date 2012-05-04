package org.spout.engine.renderer.shader.variables;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

import org.spout.api.math.Matrix;

public class Mat2ShaderVariable extends ShaderVariable {
	Matrix value;

	public Mat2ShaderVariable(int program, String name, Matrix value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		FloatBuffer buff = FloatBuffer.allocate(2);
		buff.put(value.toArray());
		buff.flip();

		GL20.glUniformMatrix2(location, false, buff);
	}
}
