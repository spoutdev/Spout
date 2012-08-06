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
package org.spout.engine.resources.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;
import org.spout.api.resource.BasicResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.filesystem.SharedFileSystem;
import org.spout.engine.renderer.shader.ClientShader;
import org.spout.engine.util.StackTrace;
import org.yaml.snakeyaml.Yaml;

public class ShaderLoader extends BasicResourceLoader<ClientShader> {
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);

	@Override
	public ClientShader getResource(InputStream stream) {
		final Client client = (Client) Spout.getEngine();

		if (client.getRenderMode() == RenderMode.GL11) {
			return ClientShader.BASIC;
		}

		// Get the paths for the Vertex and Fragment shaders
		Yaml yaml = new Yaml();

		Map<? extends String, ?> resource = checkerMapStringObject.check(yaml.load(stream));

		ClientShader shader = null;
		final Map<? extends String, ?> shaderfiles;
		if (client.getRenderMode() == RenderMode.GL30) {
			shaderfiles = checkerMapStringObject.check(resource.get("GL30"));
		} else {
			shaderfiles = checkerMapStringObject.check(resource.get("GL20"));
		}

		String fragShader = shaderfiles.get("Fragment").toString();
		String vertShader = shaderfiles.get("Vertex").toString();

		String fragSrc = readShaderSource(Spout.getFilesystem().getResourceStream(fragShader));
		String vertSrc = readShaderSource(Spout.getFilesystem().getResourceStream(vertShader));

		shader = new ClientShader(vertSrc, fragSrc, true);
		// TODO: Read Values in the shader to file

		return shader;
	}

	private String readShaderSource(InputStream stream) {
		Scanner scan = new Scanner(stream);

		StringBuilder src = new StringBuilder();

		while (scan.hasNextLine()) {
			src.append(scan.nextLine() + "\n");
		}
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return src.toString();
	}

	@Override
	public String getFallbackResourceName() {
		return "shader://Spout/resources/fallbacks/fallback.ssf";
	}

	@Override
	public String getProtocol() {
		return "shader";
	}

	@Override
	public String[] getExtensions() {
		return new String[] { "ssf" };
	}

}
