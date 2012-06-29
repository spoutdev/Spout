/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
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
		
		scan.close();

		return src.toString();
	}

}
