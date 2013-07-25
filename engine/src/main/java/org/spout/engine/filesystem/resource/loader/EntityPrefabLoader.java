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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.component.Component;
import org.spout.api.component.entity.EntityComponent;
import org.spout.api.plugin.PluginClassLoader;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.filesystem.resource.ClientEntityPrefab;

public class EntityPrefabLoader extends ResourceLoader {
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);
	private static final TypeChecker<List<? extends String>> checkerListString = TypeChecker.tList(String.class);

	public EntityPrefabLoader() {
		super("entity", "entity://Spout/fallbacks/entity.sep");
	}

	@SuppressWarnings ("unchecked")
	@Override
	public ClientEntityPrefab load(InputStream in) {
		Engine engine = Spout.getEngine();
		if (!(engine instanceof Client)) {
			throw new IllegalStateException("Prefabs can only be loaded on the client.");
		}

		final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(in));

		if (!(resourceProperties.containsKey("Name")) || !(resourceProperties.containsKey("Components")) || !(resourceProperties.containsKey("Data"))) {
			throw new IllegalStateException("A property is missing (Name, Components or Data)");
		}

		final Object name = resourceProperties.get("Name");
		if (!(name instanceof String)) {
			throw new IllegalStateException("Tried to load an entity prefab but wasn't given a name");
		}

		final List<? extends String> componentsPath = checkerListString.check(resourceProperties.get("Components"));
		final List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();
		for (String path : componentsPath) {
			Class<?> componentClass;
			try {
				try {
					componentClass = PluginClassLoader.findPluginClass(path);
				} catch (ClassNotFoundException e) {
					componentClass = Class.forName(path);
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("A component is missing: " + path);
			}
			if (EntityComponent.class.isAssignableFrom(componentClass)) {
				components.add((Class<? extends EntityComponent>) componentClass);
			} else {
				throw new IllegalStateException("This is not an entity component.");
			}
		}

		final Map<? extends String, ?> datasOld = checkerMapStringObject.check(resourceProperties.get("Data"));
		final Map<String, Object> datas = new HashMap<String, Object>();
		datas.putAll(datasOld);

		return new ClientEntityPrefab((Client) engine, (String) name, components, datas);
	}
}
