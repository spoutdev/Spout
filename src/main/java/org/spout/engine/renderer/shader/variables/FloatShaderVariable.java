package org.spout.engine.renderer.shader.variables;

import org.lwjgl.opengl.GL20;

public class FloatShaderVariable extends ShaderVariable {
	float value;
	
	public FloatShaderVariable(int program, String name, float value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		GL20.glUniform1f(location, value);

	}

}
