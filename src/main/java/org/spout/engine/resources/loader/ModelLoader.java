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

import java.io.InputStream;
import java.util.Map;

import org.spout.api.Spout;
import org.spout.api.model.Mesh;
import org.spout.api.render.RenderMaterial;
import org.spout.api.resource.BasicResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.resources.ClientModel;
import org.yaml.snakeyaml.Yaml;

public class ModelLoader extends BasicResourceLoader<ClientModel> {
	private static final String[] extensions = new String[]{ "spm" };
	private static final TypeChecker<Map<? extends String, ? extends String>> checkerMapStringObject = TypeChecker.tMap(String.class, String.class);
	
	@Override
	public String getProtocol() {
		return "model";
	}

	@Override
	public String[] getExtensions() {
		return extensions;
	}

	@Override
	public String getFallbackResourceName() {
		return "mesh://Spout/resources/fallbacks/fallback.spm";
	}

	@Override
	public ClientModel getResource(InputStream stream) {
		final Yaml yaml = new Yaml();
		final Map<? extends String, ? extends String> resourceProperties = checkerMapStringObject.check(yaml.load(stream));

		Mesh mesh = (Mesh)Spout.getFilesystem().getResource(resourceProperties.get("Mesh"));
		RenderMaterial material = (RenderMaterial)Spout.getFilesystem().getResource(resourceProperties.get("Material"));
		
		return new ClientModel(mesh, material);
		
	}

}
