package org.spout.engine.renderer.shader.variables;

import org.lwjgl.opengl.GL20;

public class AttributeShaderVariable extends ShaderVariable {
	int size;
	int type;
	long offset;

	public AttributeShaderVariable(int program, String name, int size, int type, int stride, long offset) {
		super(program, name);
		this.location = GL20.glGetAttribLocation(program, name);

		this.size = size;
		this.type = type;
		this.offset = offset;
	}

	@Override
	public void assign() {
		GL20.glEnableVertexAttribArray(location);
		GL20.glVertexAttribPointer(location, size, type, false, 0, offset);
	}
}
