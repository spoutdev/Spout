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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.renderer.shader.ClientShader;

public class ShaderLoader extends ResourceLoader {
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);

	public ShaderLoader() {
		super("shader", "shader://Spout/fallbacks/fallback.ssf");
	}

	@Override
	public ClientShader load(InputStream in) {
		final Client client = (Client) Spout.getEngine();

		// Get the paths for the Vertex and Fragment shaders
		Yaml yaml = new Yaml();

		Map<? extends String, ?> resource = checkerMapStringObject.check(yaml.load(in));

		ClientShader shader = null;
		final Map<? extends String, ?> shaderfiles;
		if (client.getRenderMode() == RenderMode.GL30 || client.getRenderMode() == RenderMode.GL40) {
			shaderfiles = checkerMapStringObject.check(resource.get("GL30"));
		} else {
			shaderfiles = checkerMapStringObject.check(resource.get("GL20"));
		}

		String fragShader = shaderfiles.get("Fragment").toString();
		String vertShader = shaderfiles.get("Vertex").toString();

		String fragSrc = readShaderSource(Spout.getFileSystem().getResourceStream(fragShader));
		String vertSrc = readShaderSource(Spout.getFileSystem().getResourceStream(vertShader));

		shader = new ClientShader(vertSrc, vertShader, fragSrc, fragShader, true);
		// TODO: Read Values in the shader to file

		return shader;
	}

	private String readShaderSource(InputStream stream) {
		Scanner scan = new Scanner(stream);

		StringBuilder src = new StringBuilder();

		while (scan.hasNextLine()) {
			src.append(scan.nextLine()).append("\n");
		}
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return src.toString();
	}
}
