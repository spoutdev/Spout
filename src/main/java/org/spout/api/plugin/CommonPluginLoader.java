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

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.spout.api.Engine;
import org.spout.api.UnsafeMethod;
import org.spout.api.event.server.PluginDisableEvent;
import org.spout.api.event.server.PluginEnableEvent;
import org.spout.api.exception.InvalidDescriptionFileException;
import org.spout.api.exception.InvalidPluginException;
import org.spout.api.exception.UnknownDependencyException;
import org.spout.api.exception.UnknownSoftDependencyException;
import org.spout.api.plugin.security.CommonSecurityManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class CommonPluginLoader implements PluginLoader {
	public static final String YAML_SPOUT = "properties.yml";
	public static final String YAML_OTHER = "plugin.yml";

	protected final Engine engine;
	private final Pattern[] patterns;
	private final CommonSecurityManager manager;
	private final double key;
	@SuppressWarnings("unchecked")
	private final Map<String, CommonClassLoader> loaders = new CaseInsensitiveMap();

	public CommonPluginLoader(final Engine engine, final CommonSecurityManager manager, final double key) {
		this.engine = engine;
		this.manager = manager;
		this.key = key;
		patterns = new Pattern[]{Pattern.compile("\\.jar$")};
	}

	public Pattern[] getPatterns() {
		return patterns;
	}

	@UnsafeMethod
	public synchronized void enablePlugin(Plugin plugin) {
		if (!CommonPlugin.class.isAssignableFrom(plugin.getClass())) {
			throw new IllegalArgumentException("Cannot enable plugin with this PluginLoader as it is of the wrong type!");
		}
		if (!plugin.isEnabled()) {
			CommonPlugin cp = (CommonPlugin) plugin;
			String name = cp.getDescription().getName();

			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader) cp.getClassLoader());
			}

			try {
				cp.setEnabled(true);
				cp.onEnable();
			} catch (Throwable e) {
				engine.getLogger().log(Level.SEVERE, "An error occured when enabling '" + plugin.getDescription().getFullName() + "': " + e.getMessage(), e);
			}

			engine.getEventManager().callEvent(new PluginEnableEvent(cp));
		}
	}

	@UnsafeMethod
	public synchronized void disablePlugin(Plugin paramPlugin) {
		if (!CommonPlugin.class.isAssignableFrom(paramPlugin.getClass())) {
			throw new IllegalArgumentException("Cannot disable plugin with this PluginLoader as it is of the wrong type!");
		}
		if (paramPlugin.isEnabled()) {
			CommonPlugin cp = (CommonPlugin) paramPlugin;
			String name = cp.getDescription().getName();

			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader) cp.getClassLoader());
			}

			try {
				cp.setEnabled(false);
				cp.onDisable();
			} catch (Throwable t) {
				engine.getLogger().log(Level.SEVERE, "An error occurred when disabling plugin '" + paramPlugin.getDescription().getFullName() + "' : " + t.getMessage(), t);
			}

			engine.getEventManager().callEvent(new PluginDisableEvent(cp));
		}
	}

	public synchronized Plugin loadPlugin(File paramFile) throws InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		return loadPlugin(paramFile, false);
	}

	public synchronized Plugin loadPlugin(File paramFile, boolean ignoresoftdepends) throws InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		CommonPlugin result;
		PluginDescriptionFile desc;
		CommonClassLoader loader;

		desc = getDescription(paramFile);

		File dataFolder = new File(paramFile.getParentFile(), desc.getName());

		processDependencies(desc);

		if (!ignoresoftdepends) {
			processSoftDependencies(desc);
		}

		try {
			if (engine.getPlatform() == Platform.CLIENT) {
				loader = new ClientClassLoader(this, this.getClass().getClassLoader());
			} else {
				loader = new CommonClassLoader(this, this.getClass().getClassLoader());
			}
			loader.addURL(paramFile.toURI().toURL());
			Class<?> main = Class.forName(desc.getMain(), true, loader);
			Class<? extends CommonPlugin> plugin = main.asSubclass(CommonPlugin.class);

			boolean locked = manager.lock(key);

			Constructor<? extends CommonPlugin> constructor = plugin.getConstructor();

			result = constructor.newInstance();

			result.initialize(this, engine, desc, dataFolder, paramFile, loader);

			if (!locked) {
				manager.unlock(key);
			}
		} catch (Exception e) {
			throw new InvalidPluginException(e);
		}

		loader.setPlugin(result);
		loaders.put(desc.getName(), loader);

		return result;
	}

	/**
	 * @param description Plugin description element
	 * @throws UnknownSoftDependencyException
	 */
	protected synchronized void processSoftDependencies(PluginDescriptionFile description) throws UnknownSoftDependencyException {
		List<String> softdepend = description.getSoftDepends();
		if (softdepend == null) {
			softdepend = new ArrayList<String>();
		}

		for (String depend : softdepend) {
			if (loaders == null) {
				throw new UnknownSoftDependencyException(depend);
			}
			if (!loaders.containsKey(depend)) {
				throw new UnknownSoftDependencyException(depend);
			}
		}
	}

	/**
	 * @param desc Plugin description element
	 * @throws UnknownDependencyException
	 */
	protected synchronized void processDependencies(PluginDescriptionFile desc) throws UnknownDependencyException {
		List<String> depends = desc.getDepends();
		if (depends == null) {
			depends = new ArrayList<String>();
		}

		for (String depend : depends) {
			if (loaders == null) {
				throw new UnknownDependencyException(depend);
			}
			if (!loaders.containsKey(depend)) {
				throw new UnknownDependencyException(depend);
			}
		}
	}

	/**
	 * @param file Plugin file object
	 * @return The current plugin's description element.
	 *
	 * @throws InvalidPluginException
	 * @throws InvalidDescriptionFileException
	 */
	protected synchronized PluginDescriptionFile getDescription(File file) throws InvalidPluginException, InvalidDescriptionFileException {
		if (!file.exists()) {
			throw new InvalidPluginException(file.getName() + " does not exist!");
		}

		PluginDescriptionFile description = null;
		JarFile jar = null;
		InputStream in = null;
		try {
			// Spout plugin properties file
			jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry(YAML_SPOUT);

			// Fallback plugin properties file
			if (entry == null) {
				entry = jar.getJarEntry(YAML_OTHER);
			}

			if (entry == null) {
				throw new InvalidPluginException("Jar has no properties.yml or plugin.yml!");
			}

			in = jar.getInputStream(entry);
			description = new PluginDescriptionFile(in);
		} catch (IOException e) {
			throw new InvalidPluginException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					engine.getLogger().log(Level.WARNING, "Problem closing input stream", e);
				}
			}
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					engine.getLogger().log(Level.WARNING, "Problem closing jar input stream", e);
				}
			}
		}
		return description;
	}

	protected Class<?> getClassByName(final String name, final CommonClassLoader commonLoader) {
		CommonPlugin plugin = commonLoader.getPlugin();
		Set<String> ignore = new HashSet<String>();
		ignore.add(plugin.getName());

		if (plugin.getDescription().getDepends() != null) {
			for (String dependency : plugin.getDescription().getDepends()) {
				try {
					Class<?> clazz = loaders.get(dependency).findClass(name, false);
					if (clazz != null) {
						return clazz;
					}
				} catch (ClassNotFoundException ignored) {
				}
				ignore.add(dependency);
			}
		}

		if (plugin.getDescription().getSoftDepends() != null) {
			for (String softDependency : plugin.getDescription().getSoftDepends()) {
				try {
					Class<?> clazz = loaders.get(softDependency).findClass(name, false);
					if (clazz != null) {
						return clazz;
					}
				} catch (ClassNotFoundException ignored) {
				}
				ignore.add(softDependency);
			}
		}

		for (String current : loaders.keySet()) {
			if (ignore.contains(current)) {
				continue;
			}
			CommonClassLoader loader = loaders.get(current);
			try {
				Class<?> clazz = loader.findClass(name, false);
				if (clazz != null) {
					return clazz;
				}
			} catch (ClassNotFoundException ignored) {
			}
		}
		return null;
	}
}
