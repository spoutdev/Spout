package org.spout.engine.renderer.shader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderHelper {
	public static int compileShader(String source, int type) {

		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, source);
		GL20.glCompileShader(shader);
		int status = GL20.glGetShader(shader, GL20.GL_COMPILE_STATUS);

		if (status != GL11.GL_TRUE) {
			String error = GL20.glGetShaderInfoLog(shader, 255);
			throw new ShaderCompileException("Compile Error in " + ((type == GL20.GL_FRAGMENT_SHADER) ? "Fragment Shader" : "VertexShader") + ": " + error);
		}
		return shader;
	}

	public static String readShaderSource(String file) throws FileNotFoundException {

		FileInputStream in = new FileInputStream(file);
		Scanner scan = new Scanner(in);

		StringBuilder src = new StringBuilder();

		while (scan.hasNextLine()) {
			src.append(scan.nextLine() + "\n");
		}

		return src.toString();
	}
}
