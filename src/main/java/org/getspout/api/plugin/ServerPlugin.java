/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
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

import org.getspout.api.addon.java.JavaAddon;
import org.getspout.api.command.Command;
import org.getspout.api.command.CommandSource;

public final class ServerPlugin extends JavaAddon {

	public ServerPlugin(String name, String version, String main) {
		//TODO fix
		//initialize(null, Spoutcraft.getClient(), new AddonDescriptionFile(name, version, main), new File(Spoutcraft.getClient().getAddonFolder(), name), null, null);
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	public boolean equals(Object other) {
		if (other instanceof JavaAddon) {
			return ((JavaAddon) other).getDescription().getName().equals(getDescription().getName());
		}
		return false;
	}

	public boolean processCommand(CommandSource source, Command command, Enum<?> commandEnum, String[] args, int baseIndex) {
		return false;
	}
}
