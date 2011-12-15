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
package org.getspout.commons.plugin;

import java.util.logging.Logger;

import org.getspout.commons.command.CommandExecutor;

public interface Plugin extends CommandExecutor {

	public PluginDescriptionFile getDescription();

	public void onEnable();

	public void onDisable();

	public void onLoad();

	public PluginLoader getAddonLoader();

	public boolean isEnabled();

	public void setEnabled(boolean arg);

	public boolean isNaggable();

	public void setNaggable(boolean b);
	
	public Logger getLogger();

	public enum Mode {
		SINGLE_PLAYER,
		MULTIPLAYER,
		BOTH;
	}

}
