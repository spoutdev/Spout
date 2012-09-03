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

import org.apache.commons.io.FileUtils;
import org.spout.api.Engine;
import org.spout.api.event.HandlerList;
import org.spout.api.exception.InvalidDescriptionFileException;
import org.spout.api.exception.InvalidPluginException;
import org.spout.api.exception.UnknownDependencyException;
import org.spout.api.meta.SpoutMetaPlugin;
import org.spout.api.plugin.security.CommonSecurityManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonPluginManager implements PluginManager {
	private final Engine engine;
	private final CommonSecurityManager manager;
	private final double key;
	private final SpoutMetaPlugin metaPlugin;
	private final Map<Pattern, PluginLoader> loaders = new HashMap<Pattern, PluginLoader>();
	private final Map<String, Plugin> names = new HashMap<String, Plugin>();
	private final List<Plugin> plugins = new ArrayList<Plugin>();
	private File updateDir;

	public CommonPluginManager(final Engine engine, final CommonSecurityManager manager, final double key) {
		this.engine = engine;
		this.manager = manager;
		this.key = key;
		this.metaPlugin = new SpoutMetaPlugin(engine);
	}

	public void registerPluginLoader(Class<? extends PluginLoader> loader) {
		PluginLoader instance = null;
		try {
			Constructor<? extends PluginLoader> constructor = loader.getConstructor(new Class[]{Engine.class, CommonSecurityManager.class, double.class});

			instance = constructor.newInstance(engine, manager, key);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error registering plugin loader!", e);
		}

		synchronized (this) {
			for (Pattern pattern : instance.getPatterns()) {
				loaders.put(pattern, instance);
			}
		}
	}

	@Override
	public Plugin getPlugin(String plugin) {
		return names.get(plugin);
	}

	@Override
	public Plugin[] getPlugins() {
		return plugins.toArray(new Plugin[plugins.size()]);
	}

	@Override
	public synchronized Plugin loadPlugin(File paramFile) throws InvalidPluginException, InvalidDescriptionFileException, UnknownDependencyException {
		return loadPlugin(paramFile, false);
	}

	public synchronized Plugin loadPlugin(File paramFile, boolean ignoreSoftDependencies) throws InvalidPluginException, InvalidDescriptionFileException, UnknownDependencyException {
		boolean locked = manager.lock(key);
		File update = null;

		if (updateDir != null && updateDir.isDirectory()) {
			update = new File(updateDir, paramFile.getName());
			if (update.exists() && update.isFile()) {
				try {
					FileUtils.copyFile(update, paramFile);
				} catch (IOException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Error copying file '").append(update.getPath()).append("' to its new destination at '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
				}
				update.delete();
			}
		}

		Set<Pattern> patterns = loaders.keySet();
		Plugin result = null;

		for (Pattern pattern : patterns) {
			String name = paramFile.getName();
			Matcher m = pattern.matcher(name);

			if (m.find()) {
				PluginLoader loader = loaders.get(pattern);
				result = loader.loadPlugin(paramFile, ignoreSoftDependencies);

				if (result != null) {
					break;
				}
			}
		}

		if (result != null) {
			plugins.add(result);
			names.put(result.getDescription().getName(), result);
		}

		if (locked) {
			manager.unlock(key);
		}
		return result;
	}

	@Override
	public synchronized Plugin[] loadPlugins(File paramFile) {
		if (!paramFile.isDirectory()) {
			throw new IllegalArgumentException("File parameter was not a Directory!");
		}

		if (engine.getUpdateFolder() != null) {
			updateDir = engine.getUpdateFolder();
		}

		loadMetaPlugin();

		List<Plugin> result = new ArrayList<Plugin>();
		LinkedList<File> files = new LinkedList<File>(Arrays.asList(paramFile.listFiles()));
		boolean failed = false;
		boolean lastPass = false;

		while (!failed || lastPass) {
			failed = true;
			Iterator<File> iterator = files.iterator();

			while (iterator.hasNext()) {
				File file = iterator.next();
				Plugin plugin = null;

				if (file.isDirectory()) {
					iterator.remove();
					continue;
				}

				try {
					plugin = loadPlugin(file, lastPass);
					iterator.remove();
				} catch (UnknownDependencyException e) {
					if (lastPass) {
						safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
						iterator.remove();
					}
				} catch (InvalidDescriptionFileException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
					iterator.remove();
				} catch (InvalidPluginException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
					iterator.remove();
				}

				if (plugin != null) {
					result.add(plugin);
					failed = false;
					lastPass = false;
				}
			}
			if (lastPass) {
				break;
			} else if (failed) {
				lastPass = true;
			}
		}

		return result.toArray(new Plugin[result.size()]);
	}

	@Override
	public void disablePlugins() {
		for (Plugin plugin : plugins) {
			if (plugin == metaPlugin) {
				continue;
			}
			disablePlugin(plugin);
		}
	}

	@Override
	public void clearPlugins() {
		synchronized (this) {
			disablePlugins();
			plugins.clear();
			names.clear();
		}
	}

	@Override
	public void enablePlugin(Plugin plugin) {
		if (plugin == metaPlugin) {
			return;
		}
		if (!plugin.isEnabled()) {
			boolean locked = manager.lock(key);

			try {
				plugin.getPluginLoader().enablePlugin(plugin);
			} catch (Exception e) {
				safelyLog(Level.SEVERE, "An error occurred in the Plugin Loader while enabling plugin '" + plugin.getDescription().getFullName() + "': " + e.getMessage(), e);
			}

			if (!locked) {
				manager.unlock(key);
			}
		}
	}

	@Override
	public void disablePlugin(Plugin plugin) {
		if (plugin == metaPlugin) {
			return;
		}
		if (plugin.isEnabled()) {
			boolean locked = manager.lock(key);

			try {
				plugin.getPluginLoader().disablePlugin(plugin);
				HandlerList.unregisterAll(plugin);
				engine.getServiceManager().unregisterAll(plugin);
				engine.getRootCommand().removeChildren(plugin);
			} catch (Exception e) {
				safelyLog(Level.SEVERE, "An error occurred in the Plugin Loader while disabling plugin '" + plugin.getDescription().getFullName() + "': " + e.getMessage(), e);
			}

			if (!locked) {
				manager.unlock(key);
			}
		}
	}

	private void safelyLog(Level level, String message, Throwable ex) {
		boolean relock = false;
		if (manager.isLocked()) {
			relock = true;
			manager.unlock(key);
		}
		engine.getLogger().log(level, message, ex);
		if (relock) {
			manager.lock(key);
		}
	}

	public void loadMetaPlugin() {
		plugins.add(metaPlugin);
		names.put("Spout", metaPlugin);
	}
	
	public SpoutMetaPlugin getMetaPlugin() {
		return metaPlugin;
	}
}
