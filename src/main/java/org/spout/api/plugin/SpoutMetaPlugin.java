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
import org.spout.api.generator.WorldGenerator;

public final class SpoutMetaPlugin implements Plugin {
	private PluginDescriptionFile pdf;
	private Engine engine;
	
	public SpoutMetaPlugin(Engine engine) {
		this.engine = engine;
		pdf = new PluginDescriptionFile("Spout", engine.getVersion(), "", Platform.BOTH);
	}
	
	@Override
	public void onEnable() {
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
}
