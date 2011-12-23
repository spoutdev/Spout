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
import java.util.logging.Logger;

import org.getspout.api.Game;
import org.getspout.api.UnsafeMethod;

public abstract class CommonPlugin implements Plugin {

	private PluginDescriptionFile description;
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
		return null;
	}

	public Logger getLogger() {
		return null;
	}

	public PluginDescriptionFile getDescription() {
		return description;
	}

	@UnsafeMethod
	public void initialize(CommonPluginLoader commonsPluginLoader, Game game,
			PluginDescriptionFile desc, File dataFolder, File paramFile,
			CommonClassLoader loader) {
		// TODO Auto-generated method stub
		
	}

	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

}
