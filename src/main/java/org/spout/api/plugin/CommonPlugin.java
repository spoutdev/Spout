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
import org.apache.commons.lang3.Validate;
import org.spout.api.Engine;
import org.spout.api.UnsafeMethod;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.lang.PluginDictionary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class CommonPlugin implements Plugin {
	private Engine engine;
	private PluginDescriptionFile description;
	private CommonClassLoader classLoader;
	private CommonPluginLoader pluginLoader;
	private File dataFolder;
	private File file;
	private boolean enabled;
	private Logger logger;
	private PluginDictionary dictionary;

	public final void initialize(CommonPluginLoader pluginLoader, Engine engine, PluginDescriptionFile description, File dataFolder, File file, CommonClassLoader classLoader) {
		this.pluginLoader = pluginLoader;
		this.engine = engine;
		this.dataFolder = dataFolder;
		this.description = description;
		this.file = file;
		this.classLoader = classLoader;

		this.logger = new PluginLogger(this);

		this.dictionary = new PluginDictionary(this);
	}

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

	public final Engine getEngine() {
		return engine;
	}

	public final PluginDescriptionFile getDescription() {
		return description;
	}

	public final ClassLoader getClassLoader() {
		return classLoader;
	}

	public final PluginLoader getPluginLoader() {
		return pluginLoader;
	}

	public final File getDataFolder() {
		return dataFolder;
	}

	public final File getFile() {
		return file;
	}

	@SuppressWarnings("resource")
	public InputStream getResource(String path) {
		Validate.notNull(path);
		JarFile jar;
		try {
			jar = new JarFile(getFile());
		} catch (IOException e) {
			return null;
		}
		JarEntry entry = jar.getJarEntry(path);
		try {
			return entry == null ? null : jar.getInputStream(entry);
		} catch (IOException e) {
			return null;
		}
	}

	public void extractResource(String path, File destination) throws IOException {
		Validate.notNull(destination);
		InputStream stream = getResource(path);
		if (stream == null) {
			throw new IOException("Unknown resource: " + path);
		}
		FileUtils.copyInputStreamToFile(stream, destination);
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final Logger getLogger() {
		return logger;
	}

	@UnsafeMethod
	public WorldGenerator getWorldGenerator(String world, String generator) {
		getLogger().severe("Unknown generator for world '" + world + "', generator: '" + generator + "'");
		return null;
	}

	public final String getName() {
		return getDescription().getName();
	}

	@Override
	@UnsafeMethod
	public final void loadLibrary(File file) {
		if (!file.exists()) {
			throw new IllegalArgumentException(new StringBuilder().append("Failed to load library: The file '").append(file.getName()).append("' does not exist.").toString());
		}

		boolean matches = false;
		for (Pattern pattern : pluginLoader.getPatterns()) {
			if (pattern.matcher(file.getName()).find()) {
				matches = true;
				break;
			}
		}

		if (!matches) {
			throw new IllegalArgumentException(new StringBuilder().append("Failed to load library: The file '").append(file.getName()).append("' is not a supported library file type.").toString());
		}

		try {
			classLoader.addURL(file.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Failed to load library: ", e);
		}
	}

	@Override
	public PluginDictionary getDictionary() {
		return dictionary;
	}
}
