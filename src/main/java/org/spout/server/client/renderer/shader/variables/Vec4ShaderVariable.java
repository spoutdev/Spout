package org.spout.server.client.renderer.shader.variables;

import org.spout.api.math.Vector4;
import org.lwjgl.opengl.GL20;
public class Vec4ShaderVariable extends ShaderVariable {
	Vector4 value;
	public Vec4ShaderVariable(int program, String name, Vector4 value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		GL20.glUniform4f(location, value.getX(), value.getY(), value.getZ(), value.getW());
	}

}
