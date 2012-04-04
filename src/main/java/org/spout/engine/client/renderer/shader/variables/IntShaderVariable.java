package org.spout.engine.client.renderer.shader.variables;

import org.lwjgl.opengl.GL20;

public class IntShaderVariable extends ShaderVariable {
	int value;
	
	public IntShaderVariable(int program, String name, int value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		GL20.glUniform1i(location, value);
	}

}
