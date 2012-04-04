package org.spout.engine.client.renderer.shader.variables;
import java.nio.FloatBuffer;

import org.spout.api.math.Matrix;
import org.lwjgl.opengl.GL20;


public class Mat3ShaderVariable extends ShaderVariable {

	Matrix value;
	public Mat3ShaderVariable(int program, String name, Matrix value) {
		super(program, name);
		this.value = value;
	}

	@Override
	public void assign() {
		FloatBuffer buff = FloatBuffer.allocate(3*3);
		buff.put(value.toArray());
		buff.flip();
		
		GL20.glUniformMatrix3(location, false, buff);

	}

}
