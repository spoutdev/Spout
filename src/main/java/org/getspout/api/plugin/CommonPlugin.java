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
import java.util.logging.Logger;

import org.getspout.api.Game;
import org.getspout.api.UnsafeMethod;

public abstract class CommonPlugin implements Plugin {

	private PluginDescriptionFile description;
	private CommonClassLoader classLoader;
	private CommonPluginLoader pluginLoader;
	private Game game;
	private File dataFolder;
	private File paramFile;
	private boolean enabled;

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

	public boolean isEnabled() {
		return enabled;
	}

	@UnsafeMethod
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public PluginLoader getPluginLoader() {
		return pluginLoader;
	}

	public Logger getLogger() {
		return game.getLogger();
	}

	public PluginDescriptionFile getDescription() {
		return description;
	}

	@UnsafeMethod
	public void initialize(CommonPluginLoader commonsPluginLoader, Game game,
			PluginDescriptionFile desc, File dataFolder, File paramFile,
			CommonClassLoader loader) {
		this.description = desc;
		this.classLoader = loader;
		this.game = game;
		this.pluginLoader = commonsPluginLoader;
		this.dataFolder = dataFolder;
		this.paramFile = paramFile;
		// TODO Auto-generated method stub
		
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

}
