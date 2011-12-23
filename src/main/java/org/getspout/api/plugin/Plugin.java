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

import java.util.logging.Logger;

public interface Plugin {
	
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
	 * @return enabled
	 */
	public boolean isEnabled();
	
	/**
	 * Changes the enabled state of the plugin
	 * This should only be called by the plugin's loader
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled);
	
	/**
	 * Returns the plugin's loader
	 * @return loader
	 */
	public PluginLoader getPluginLoader();
	
	/**
	 * Returns the plugin's logger
	 * @return logger
	 */
	public Logger getLogger();
	
	/**
	 * Returns the plugin's description
	 * @return description
	 */
	public PluginDescriptionFile getDescription();

}
