/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
import java.util.logging.Logger;

import org.spout.api.Engine;
import org.spout.api.lang.JavaPluginDictionary;
import org.spout.api.lang.PluginDictionary;
import org.spout.api.protocol.Protocol;
import org.spout.api.util.Named;

public abstract class Plugin implements Named {
	protected Engine engine;
	protected PluginDescriptionFile description;
	protected PluginClassLoader classLoader;
	protected PluginLoader pluginLoader;
	protected File dataFolder;
	protected File file;
	protected boolean enabled;
	protected Logger logger;
	protected PluginDictionary dictionary;

	public final void initialize(PluginLoader pluginLoader, Engine engine,
								 PluginDescriptionFile description, File dataFolder,
								 File file, PluginClassLoader classLoader) {

		this.pluginLoader = pluginLoader;
		this.engine = engine;
		this.dataFolder = dataFolder;
		this.description = description;
		this.file = file;
		this.classLoader = classLoader;
		this.logger = new PluginLogger(this);
		if (file != null) this.dictionary = new JavaPluginDictionary(this);
	}

	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable() {
	}

	/**
	 * Called when the plugins is disabled
	 */
	public void onDisable() {
	}

	/**
	 * Called when the server is reloaded
	 */
	public void onReload() {
	}

	/**
	 * Called when the plugin is initially loaded
	 */
	public void onLoad() {
	}

	/**
	 * Returns the engine object
	 * @return engine
	 */
	public final Engine getEngine() {
		return engine;
	}

	/**
	 * Returns the plugin's description
	 * @return description
	 */
	public final PluginDescriptionFile getDescription() {
		return description;
	}

	public final ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Returns the plugin's loader
	 * @return loader
	 */
	public final PluginLoader getPluginLoader() {
		return pluginLoader;
	}

	/**
	 * Returns the plugin's data folder
	 * @return folder that contains the plugin's data
	 */
	public final File getDataFolder() {
		return dataFolder;
	}

	/**
	 * Returns a File that is the plugin's jar file.
	 * @return jar file
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * Returns true if the plugins is enabled
	 * @return enabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * Returns the plugin's logger
	 * @return logger
	 */
	public final Logger getLogger() {
		return logger;
	}

	/**
	 * Returns the dictionary associated with this plugin.
	 *
	 * @return the plugins dictionary
	 */
	public final PluginDictionary getDictionary() {
		return dictionary;
	}

	@Override
	public final String getName() {
		return getDescription().getName();
	}
}
