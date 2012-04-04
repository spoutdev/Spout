package org.spout.server.client.renderer.shader.variables;

import org.spout.api.math.Vector3;
import org.lwjgl.opengl.GL20;

public class Vec3ShaderVariable extends ShaderVariable {
	Vector3 value;
	public Vec3ShaderVariable(int program, String name, Vector3 value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		GL20.glUniform3f(location, value.getX(), value.getY(), value.getZ());

	}

}
