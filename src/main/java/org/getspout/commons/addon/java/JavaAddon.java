/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.addon.java;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.getspout.commons.Game;
import org.getspout.commons.UnsafeMethod;
import org.getspout.commons.command.Command;
import org.getspout.commons.plugin.Plugin;
import org.getspout.commons.plugin.PluginDescriptionFile;
import org.getspout.commons.plugin.PluginLoader;

public abstract class JavaAddon implements Plugin {

	private boolean initialized = false;
	private PluginLoader loader = null;
	private Game game = null;
	private File file = null;
	private File dataFolder = null;
	private AddonClassLoader classLoader = null;
	private boolean enabled = false;
	private PluginDescriptionFile description = null;
	private boolean naggable = false;
	
	@UnsafeMethod
	public JavaAddon(){
		
	}

	public final PluginDescriptionFile getDescription() {
		return description;
	}

	
	public final void initialize(JavaAddonLoader loader, Game game, PluginDescriptionFile description, File dataFolder, File file, AddonClassLoader classLoader) {
		if (!initialized) {
			this.loader = loader;
			this.game = game;
			this.file = file;
			this.description = description;
			this.dataFolder = dataFolder;
			this.classLoader = classLoader;
			this.initialized = true;
		}
	}

	@UnsafeMethod
	public abstract void onEnable();

	@UnsafeMethod
	public abstract void onDisable();

	public final File getFile() {
		return file;
	}

	public final File getDataFolder() {
		return dataFolder;
	}

	public final Game getGame() {
		return game;
	}

	public final PluginLoader getAddonLoader() {
		return loader;
	}

	public final boolean isEnabled() {
		return enabled;
	}
	
	public final Logger getLogger(){
		return game.getLogger();
	}
	
	@UnsafeMethod
	public void onLoad() {
	}

	@UnsafeMethod
	public void setEnabled(boolean arg) {
		if (this.enabled != arg) {
			this.enabled = arg;
			if (this.enabled) {
				this.onEnable();
			} else {
				this.onDisable();
			}
		}
	}

	public final boolean isNaggable() {
		return naggable;
	}

	public final void setNaggable(boolean naggable) {
		this.naggable = naggable;
	}

	public final AddonClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public final int hashCode() {
		return (new HashCodeBuilder().append(file).append(dataFolder).append(description!=null?description.getName():"").toHashCode());
	}

}
