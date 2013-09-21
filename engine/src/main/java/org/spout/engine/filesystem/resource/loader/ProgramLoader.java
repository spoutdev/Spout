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
package org.spout.engine.filesystem.resource.loader;

import java.io.InputStream;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;
import org.spout.api.resource.ResourceLoader;

import org.spout.cereal.config.ConfigurationException;
import org.spout.cereal.config.ConfigurationNode;
import org.spout.cereal.config.yaml.YamlConfiguration;

import org.spout.engine.SpoutClient;
import org.spout.engine.filesystem.resource.SpoutShader;
import org.spout.engine.filesystem.resource.SpoutProgram;

import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Shader.ShaderType;

public class ProgramLoader extends ResourceLoader {
	public ProgramLoader() {
		super("program", "program://Spout/fallbacks/fallback.ssf");
	}

	@Override
	public SpoutProgram load(InputStream in) {
		final YamlConfiguration configuration = new YamlConfiguration(in);
		try {
			configuration.load();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		final Engine engine = Spout.getEngine();
		if (!(engine instanceof SpoutClient)) {
			throw new IllegalStateException("Shaders can only be loaded on the client.");
		}
		final SpoutClient client = (SpoutClient) engine;
		final RenderMode mode = client.getRenderMode();

		final ConfigurationNode shaderNode = configuration.getNode(mode.toString());
		if (shaderNode == null || !shaderNode.hasChildren()) {
			throw new IllegalStateException("Missing version in spout shader file: " + mode);
		}

		final SpoutShader vertex;
		String shaderFile = shaderNode.getNode("Vertex").getString();
		InputStream shaderSource = Spout.getFileSystem().getResourceStream(shaderFile);
		if (shaderSource == null) {
			throw new IllegalStateException("Shader file not found: " + shaderFile);
		}
		vertex = new SpoutShader(shaderSource);
		vertex.setSource(shaderSource);
		vertex.setType(ShaderType.VERTEX);

		final SpoutShader fragment;;
		shaderFile = shaderNode.getNode("Fragment").getString();
		shaderSource = Spout.getFileSystem().getResourceStream(shaderFile);
		if (shaderSource == null) {
			throw new IllegalStateException("Shader file not found: " + shaderFile);
		}
		fragment = new SpoutShader(shaderSource);
		fragment.setType(ShaderType.FRAGMENT);

		final SpoutProgram program = new SpoutProgram();
		program.addShader(vertex);
		program.addShader(fragment);

		return program;
	}
}
