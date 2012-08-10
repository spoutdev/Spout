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
package org.spout.api.meta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.lang.PluginDictionary;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.Plugin;
import org.spout.api.plugin.PluginDescriptionFile;
import org.spout.api.plugin.PluginLoader;

public final class SpoutMetaPlugin implements Plugin {
	private PluginDescriptionFile pdf;
	private Engine engine;
	private PluginDictionary dictionary; // TODO extend this dictionary to load translation from a different location

	public SpoutMetaPlugin(Engine engine) {
		this.engine = engine;
		pdf = new PluginDescriptionFile("Spout", engine.getVersion(), "", Platform.ALL);
		dictionary = new PluginDictionary(this);
	}

	@Override
	public void onEnable() {
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this));
		Spout.getEngine().getRootCommand().addSubCommands(this, MetaCommands.class, commandRegFactory);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public String getName() {
		return "Spout";
	}

	@Override
	public void onReload() {
	}

	@Override
	public void onLoad() {
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setEnabled(boolean enabled) {
	}

	@Override
	public PluginLoader getPluginLoader() {
		return null;
	}

	@Override
	public Logger getLogger() {
		return getEngine().getLogger();
	}

	@Override
	public PluginDescriptionFile getDescription() {
		return pdf;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	public WorldGenerator getWorldGenerator(String world, String generator) {
		return null;
	}

	@Override
	public File getDataFolder() {
		return engine.getDataFolder();
	}

	@Override
	public File getFile() {
		return new File(engine.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
	}

	public InputStream getResource(String path) {
		Validate.notNull(path);
		return engine.getClass().getClassLoader().getResourceAsStream("/" + path);
	}

	public void extractResource(String path, File destination) throws IOException {
		Validate.notNull(destination);
		InputStream stream = getResource(path);
		if (stream == null) {
			throw new IOException("No resource found at path " + path);
		}
		FileUtils.copyInputStreamToFile(stream, destination);
	}

	@Override
	public void loadLibrary(File file) {
	}

	@Override
	public PluginDictionary getDictionary() {
		return dictionary;
	}
}
