/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.filesystem.resource;

import java.io.InputStream;

import org.spout.api.Spout;

import org.spout.engine.SpoutClient;

import org.spout.renderer.GLVersioned.GLVersion;
import org.spout.renderer.gl.Shader;
import org.spout.renderer.gl.Shader.ShaderType;

public class SpoutShader {
	protected Shader shader;

	public SpoutShader(InputStream stream) {
		shader = ((SpoutClient) Spout.getEngine()).getRenderer().getGL().createShader();
		shader.setSource(stream);
	}

	public void checkCreated() {
		shader.checkCreated();
	}

	public void create() {
		shader.create();
	}

	public void destory() {
		shader.destroy();
	}

	public GLVersion getGLVersion() {
		return shader.getGLVersion();
	}

	public int getID() {
		return shader.getID();
	}

	public ShaderType getType() {
		return shader.getType();
	}

	public boolean isCreated() {
		return shader.isCreated();
	}

	public void setSource(InputStream in) {
		shader.setSource(in);
	}

	public void setType(ShaderType type){
		shader.setType(Shader.ShaderType.VERTEX);
	}
}
