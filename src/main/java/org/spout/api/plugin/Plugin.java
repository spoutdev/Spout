/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import org.spout.api.Game;
import org.spout.api.Source;
import org.spout.api.generator.WorldGenerator;

public interface Plugin extends Source {
	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable();

	/**
	 * Called when the plugins is disabled
	 */
	public void onDisable();

	/**
	 * Called when the server is reloaded
	 */
	public void onReload();

	/**
	 * Called when the plugin is initially loaded
	 */
	public void onLoad();

	/**
	 * Returns true if the plugins is enabled
	 *
	 * @return enabled
	 */
	public boolean isEnabled();

	/**
	 * Changes the enabled state of the plugin This should only be called by the
	 * plugin's loader
	 *
	 * @param enabled
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Returns the plugin's loader
	 *
	 * @return loader
	 */
	public PluginLoader getPluginLoader();

	/**
	 * Returns the plugin's logger
	 *
	 * @return logger
	 */
	public Logger getLogger();

	/**
	 * Returns the plugin's description
	 *
	 * @return description
	 */
	public PluginDescriptionFile getDescription();

	/**
	 * Returns the game object
	 *
	 * @return game
	 */
	public Game getGame();

	/**
	 * Gets the suitable generator for the world and generator name.
	 *
	 * @param world name to generate
	 * @param generator name
	 * @return world generator
	 */
	public WorldGenerator getWorldGenerator(String world, String generator);

	public File getDataFolder();

	public File getFile();
}
