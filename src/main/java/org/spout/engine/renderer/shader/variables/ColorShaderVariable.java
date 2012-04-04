package org.spout.engine.renderer.shader.variables;

import java.awt.Color;

import org.lwjgl.opengl.GL20;

public class ColorShaderVariable extends ShaderVariable {
	
	Color value;

	public ColorShaderVariable(int program, String name, Color value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		GL20.glUniform4f(this.location, value.getRed() / 255.0f, value.getGreen() / 255.0f, value.getBlue() / 255.0f, value.getAlpha() / 255.0f);

	}

}
