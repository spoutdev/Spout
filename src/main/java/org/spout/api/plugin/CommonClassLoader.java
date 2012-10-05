/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommonClassLoader extends URLClassLoader {
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private final CommonPluginLoader loader;
	private CommonPlugin plugin;
	private static HashMap<String, CommonPlugin> pluginsForClassNames = new HashMap<String, CommonPlugin>(500);
	private static Set<CommonClassLoader> loaders = new HashSet<CommonClassLoader>();

	public CommonClassLoader(final CommonPluginLoader loader, final ClassLoader parent) {
		super(new URL[0], parent);
		this.loader = loader;
		loaders.add(this);
	}

	@Override
	protected void addURL(URL url) {
		super.addURL(url);
	}

	protected void setPlugin(CommonPlugin plugin) {
		this.plugin = plugin;
		pluginsForClassNames.put(plugin.getClass().getName(), plugin);
	}

	protected CommonPlugin getPlugin() {
		return plugin;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return findClass(name, true);
	}

	protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		Class<?> result = classes.get(name);

		if (result == null) {
			try {
				result = super.findClass(name);
			} catch (Exception ignored) {
			}

			if (result == null && checkGlobal) {
				result = loader.getClassByName(name, this);
			}

			if (result != null) {
				classes.put(name, result);
				pluginsForClassNames.put(name, plugin);
			} else {
				throw new ClassNotFoundException(name);
			}
		}

		return result;
	}

	/**
	 * Returns a set of cached classes' names
	 *
	 * @return set of class names
	 */
	public Set<String> getClassNames() {
		return Collections.unmodifiableSet(classes.keySet());
	}

	/**
	 * Returns a set of cached classes
	 *
	 * @return set of classes
	 */
	public Collection<Class<?>> getClasses() {
		return Collections.unmodifiableCollection(classes.values());
	}
	
	public static CommonPlugin getPlugin(String className) {
		return pluginsForClassNames.get(className);
	}
	
	public static Class<?> findPluginClass(String name) throws ClassNotFoundException {
		for (CommonClassLoader loader : loaders) {
			Class<?> clazz = loader.findClass(name);
			if (clazz != null) {
				return clazz;
			}
		}
		throw new ClassNotFoundException("Class " + name + " was unable to be found");
	}
}
