/*
 * This file is part of SpoutAPI (http://getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.plugin;

import java.io.File;
import java.util.regex.Pattern;

import org.getspout.api.plugin.exceptions.InvalidDescriptionFileException;
import org.getspout.api.plugin.exceptions.InvalidPluginException;
import org.getspout.api.plugin.exceptions.UnknownDependencyException;

public interface PluginLoader {
	
	public abstract Pattern[] getPatterns();

	/**
	 * Enables the plugin
	 * 
	 * @param paramPlugin
	 */
	public abstract void enablePlugin(Plugin paramPlugin);

	/**
	 * Disables the plugin
	 * 
	 * @param paramPlugin
	 */
	public abstract void disablePlugin(Plugin paramPlugin);
	
	/**
	 * Loads the file as a plugin
	 * 
	 * @param paramFile
	 * @return instance of the plugin
	 * @throws InvalidPluginException
	 * @throws InvalidPluginException
	 * @throws UnknownDependencyException
	 * @throws InvalidDescriptionFileException
	 */
	public abstract Plugin loadPlugin(File paramFile) throws InvalidPluginException, InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException;
	
	/**
	 * Loads the file as a plugin
	 * 
	 * @param paramFile
	 * @param paramBoolean ignores soft dependencies when it attempts to load the plugin
	 * @return instance of the plugin
	 * @throws InvalidPluginException
	 * @throws InvalidPluginException
	 * @throws UnknownDependencyException
	 * @throws InvalidDescriptionFileException
	 */
	public abstract Plugin loadPlugin(File paramFile, boolean paramBoolean) throws InvalidPluginException, InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException;
	
}
