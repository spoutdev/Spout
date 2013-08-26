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
package org.spout.api.plugin;

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

import org.apache.commons.collections.map.CaseInsensitiveMap;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.event.server.plugin.PluginDisableEvent;
import org.spout.api.event.server.plugin.PluginEnableEvent;
import org.spout.api.exception.InvalidDescriptionFileException;
import org.spout.api.exception.InvalidPluginException;
import org.spout.api.exception.UnknownDependencyException;
import org.spout.api.exception.UnknownSoftDependencyException;
import org.spout.api.plugin.security.PluginSecurityManager;
import org.spout.api.protocol.Protocol;

public class PluginLoader {
	public static final String YAML_SPOUT = "properties.yml";
	public static final String YAML_OTHER = "plugin.yml";
	protected final Engine engine;
	private final PluginSecurityManager manager;
	private final double key;
	@SuppressWarnings ("unchecked")
	private final Map<String, PluginClassLoader> loaders = new CaseInsensitiveMap();

	public PluginLoader(final Engine engine, final PluginSecurityManager manager, final double key) {
		this.engine = engine;
		this.manager = manager;
		this.key = key;
	}

	/**
	 * Enables the plugin
	 *
	 * @param plugin to enable
	 */
	public synchronized void enablePlugin(Plugin plugin) {
		if (!plugin.isEnabled()) {

			String name = plugin.getDescription().getName();
			if (!loaders.containsKey(name)) {
				loaders.put(name, (PluginClassLoader) plugin.getClassLoader());
			}

			try {
				String version = plugin.getDescription().getVersion();
				Spout.info("Enabling " + plugin.getName() + " v" + version + "...");
				plugin.enabled = true;
				plugin.onEnable();
				Spout.info(plugin.getName() + " v" + version + " enabled.");
			} catch (Throwable e) {
				engine.getLogger().log(Level.SEVERE, "An error occured when enabling '" + plugin.getDescription().getFullName() + "': " + e.getMessage(), e);
			}

			engine.getEventManager().callEvent(new PluginEnableEvent(plugin));
		}
	}

	/**
	 * Disables the plugin
	 *
	 * @param plugin to disable
	 */
	public synchronized void disablePlugin(Plugin plugin) {
		if (plugin.isEnabled()) {

			String name = plugin.getDescription().getName();
			if (!loaders.containsKey(name)) {
				loaders.put(name, (PluginClassLoader) plugin.getClassLoader());
			}

			try {
				plugin.enabled = false;
				plugin.onDisable();
			} catch (Throwable t) {
				engine.getLogger().log(Level.SEVERE, "An error occurred when disabling plugin '" + plugin.getDescription().getFullName() + "' : " + t.getMessage(), t);
			}

			engine.getEventManager().callEvent(new PluginDisableEvent(plugin));
		}
	}

	/**
	 * Loads the file as a plugin
	 *
	 * @param file to load
	 * @return instance of the plugin
	 */
	public synchronized Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		return loadPlugin(file, false);
	}

	/**
	 * Loads the file as a plugin
	 *
	 * @param file to load
	 * @param ignoreSoftDepends ignores soft dependencies when it attempts to load the plugin
	 * @return instance of the plugin
	 */
	public synchronized Plugin loadPlugin(File file, boolean ignoreSoftDepends) throws InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		Plugin result = null;
		PluginDescriptionFile desc;
		PluginClassLoader loader;

		desc = getDescription(file);
		if (desc.isValidPlatform(engine.getPlatform())) {

			File dataFolder = new File(file.getParentFile(), desc.getName());

			processDependencies(desc);

			if (!ignoreSoftDepends) {
				processSoftDependencies(desc);
			}

			try {
				loader = new PluginClassLoader(this, this.getClass().getClassLoader(), desc);
				loader.addURL(file.toURI().toURL());
				Class<?> main = Class.forName(desc.getMain(), true, loader);
				Class<? extends Plugin> plugin = main.asSubclass(Plugin.class);

				boolean locked = manager.lock(key);

				Constructor<? extends Plugin> constructor = plugin.getConstructor();

				result = constructor.newInstance();

				result.initialize(this, engine, desc, dataFolder, file, loader);

				for (String protocolString : desc.getProtocols()) {
					Class<? extends Protocol> protocol = Class.forName(protocolString, true, loader).asSubclass(Protocol.class);
					Constructor<? extends Protocol> pConstructor = protocol.getConstructor();
					Protocol.registerProtocol(pConstructor.newInstance());
				}

				if (!locked) {
					manager.unlock(key);
				}
			} catch (Exception e) {
				throw new InvalidPluginException(e);
			} catch (UnsupportedClassVersionError e) {
				String version = e.getMessage().replaceFirst("Unsupported major.minor version ", "").split(" ")[0];
				engine.getLogger().severe("Plugin " + desc.getName() + " is built for a newer Java version than your current installation, and cannot be loaded!");
				engine.getLogger().severe("To run " + desc.getName() + ", you need Java version " + version + " or higher!");
				throw new InvalidPluginException(e);
			}

			loader.setPlugin(result);
			loaders.put(desc.getName(), loader);
		}

		return result;
	}

	/**
	 * @param description Plugin description element
	 */
	protected synchronized void processSoftDependencies(PluginDescriptionFile description) throws UnknownSoftDependencyException {
		List<String> softdepend = description.getSoftDepends();
		if (softdepend == null) {
			softdepend = new ArrayList<>();
		}

		for (String depend : softdepend) {
			if (!loaders.containsKey(depend)) {
				throw new UnknownSoftDependencyException(depend);
			}
		}
	}

	/**
	 * @param desc Plugin description element
	 */
	protected synchronized void processDependencies(PluginDescriptionFile desc) throws UnknownDependencyException {
		List<String> depends = desc.getDepends();
		if (depends == null) {
			depends = new ArrayList<>();
		}

		for (String depend : depends) {
			if (!loaders.containsKey(depend.toLowerCase())) {
				throw new UnknownDependencyException(depend);
			}
		}
	}

	/**
	 * @param file Plugin file object
	 * @return The current plugin's description element.
	 */
	protected static synchronized PluginDescriptionFile getDescription(File file) throws InvalidPluginException, InvalidDescriptionFileException {
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
					Spout.getLogger().log(Level.WARNING, "Problem closing input stream", e);
				}
			}
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					Spout.getLogger().log(Level.WARNING, "Problem closing jar input stream", e);
				}
			}
		}
		return description;
	}

	protected Class<?> getClassByName(final String name, final PluginClassLoader commonLoader) {
		Set<String> ignore = new HashSet<>();

		for (String dependency : commonLoader.getDepends()) {
			try {
				Class<?> clazz = loaders.get(dependency).findClass(name, false);
				if (clazz != null) {
					return clazz;
				}
			} catch (ClassNotFoundException ignored) {
			}
			ignore.add(dependency.toLowerCase());
		}

		for (String softDependency : commonLoader.getSoftDepends()) {
			try {
				Class<?> clazz = loaders.get(softDependency).findClass(name, false);
				if (clazz != null) {
					return clazz;
				}
			} catch (ClassNotFoundException ignored) {
			}
			ignore.add(softDependency.toLowerCase());
		}

		for (String current : loaders.keySet()) {
			if (ignore.contains(current)) {
				continue;
			}
			PluginClassLoader loader = loaders.get(current);
			if (loader == commonLoader) {
				continue;
			}
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
