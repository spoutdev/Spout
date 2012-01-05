/*
 * This file is part of Spout (http://www.getspout.org/).
 *
 * The SpoutAPI is licensed under the SpoutDev license version 1.  
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
 * the MIT license and the SpoutDev license version 1 along with this program.  
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license, 
 * including the MIT license.
 */
package org.getspout.api.plugin;

import java.io.File;

//import org.getspout.api.event.Event;
import org.getspout.api.plugin.exceptions.InvalidDescriptionFileException;
import org.getspout.api.plugin.exceptions.InvalidPluginException;
import org.getspout.api.plugin.exceptions.UnknownDependencyException;

public interface PluginManager {
	
	/**
	 * Returns the the instance of a plugins when given its name
	 * 
	 * @param The name of the plugin
	 * @return instance of the plugin
	 */
	public abstract Plugin getPlugin(String plugin);

	/**
	 * Returns an array of plugins that have been loaded
	 * 
	 * @return plugins
	 */
	public abstract Plugin[] getPlugins();

	/**
	 * Loads the file as a plugin
	 * 
	 * @param paramFile
	 * @return instance of the plugin
	 * @throws InvalidPluginException
	 * @throws InvalidDescriptionFileException
	 * @throws UnknownDependencyException
	 */
	public abstract Plugin loadPlugin(File paramFile) throws InvalidPluginException, InvalidDescriptionFileException, UnknownDependencyException;

	/**
	 * Loads all plugins in a directory
	 * 
	 * @param paramFile
	 * @return array of plugins loaded
	 */
	public abstract Plugin[] loadPlugins(File paramFile);

	/**
	 * Disables all plugins
	 */
	public abstract void disablePlugins();

	/**
	 * Disables all plugins and clears the List of plugins
	 */
	public abstract void clearPlugins();

	/**
	 * Enables the plugin
	 * @param plugin
	 */
	public abstract void enablePlugin(Plugin plugin);

	/**
	 * Disables the plugin
	 * @param plugin
	 */
	public abstract void disablePlugin(Plugin plugin);
}
