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

import java.io.File;
import java.util.logging.Logger;

import org.spout.api.Engine;
import org.spout.api.UnsafeMethod;
import org.spout.api.generator.WorldGenerator;

public abstract class CommonPlugin implements Plugin {
	private PluginDescriptionFile description;
	private CommonClassLoader classLoader;
	private CommonPluginLoader pluginLoader;
	private Engine game;
	private File dataFolder;
	private File file;
	private boolean enabled;
	private Logger logger;

	@UnsafeMethod
	public abstract void onEnable();

	@UnsafeMethod
	public abstract void onDisable();

	@UnsafeMethod
	public void onReload() {
	}

	@UnsafeMethod
	public void onLoad() {
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final PluginLoader getPluginLoader() {
		return pluginLoader;
	}

	public final Logger getLogger() {
		return logger;
	}

	public final PluginDescriptionFile getDescription() {
		return description;
	}

	public final void initialize(CommonPluginLoader commonsPluginLoader, Engine game, PluginDescriptionFile desc, File dataFolder, File paramFile, CommonClassLoader loader) {
		description = desc;
		classLoader = loader;
		this.game = game;
		pluginLoader = commonsPluginLoader;
		this.dataFolder = dataFolder;
		file = paramFile;
		logger = new PluginLogger(this);
	}

	public final ClassLoader getClassLoader() {
		return classLoader;
	}

	public final File getDataFolder() {
		return dataFolder;
	}

	public final File getFile() {
		return file;
	}

	public final Engine getGame() {
		return game;
	}

	@UnsafeMethod
	public WorldGenerator getWorldGenerator(String world, String generator) {
		getLogger().severe("Unknown generator for world '" + world + "', generator: '" + generator + "'");
		return null;
	}

	public String getName() {
		return getDescription().getName();
	}
}
