package org.spout.engine.renderer.shader.variables;

import org.lwjgl.opengl.GL20;

import org.spout.api.math.Vector2;

public class Vec2ShaderVariable extends ShaderVariable {
	Vector2 value;

	public Vec2ShaderVariable(int program, String name, Vector2 value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		GL20.glUniform2f(location, value.getX(), value.getY());
	}
}
