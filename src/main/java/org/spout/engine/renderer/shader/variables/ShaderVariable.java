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
package org.spout.engine.renderer.shader.variables;

import org.lwjgl.opengl.GL20;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;

import org.spout.engine.renderer.shader.ShaderVariableNotFoundException;

public abstract class ShaderVariable {
	/**
	 * Error levels
	 * 0 - No message
	 * 1 - Warn in Console
	 * 2 - Throw Exception
	 */
	public static final int variableError = 0;
	int program;
	int location;

	@SuppressWarnings("unused")
	public ShaderVariable(int program, String name) {
		if (((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL11) {
			return;  //Shaders don't exist in OpenGL 1.1
		}
		this.program = program;
		//If we are an attribute, we aren't a uniform.  Don't continue
		if (this instanceof AttributeShaderVariable) {
			return;
		}

		this.location = GL20.glGetUniformLocation(program, name);

		//Error Checking.  In production, leave this as a warning, because OpenGL doesn't care if you try to put something
		//into a variable that doesn't exist (it ignores it).
		//
		//If we want to have a debug mode, switch the final bool to true to throw an exception if the variable doesn't exist.
		//This is the same as treating warnings as errors, and could be useful for debugging shaders.
		if (this.location == -1 && variableError == 1) {
			System.out.println("[Warning] Shader Variable: " + name + " not found! (Was it optimized out?)");
		} else if (this.location == -1 && variableError == 2) {
			throw new ShaderVariableNotFoundException(name);
		}
	}

	public abstract void assign();
}