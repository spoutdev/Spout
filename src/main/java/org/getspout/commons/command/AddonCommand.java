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
package org.getspout.commons.command;

import org.getspout.commons.addon.Addon;
import org.getspout.commons.command.Command;
import org.getspout.commons.command.CommandException;
import org.getspout.commons.command.CommandExecutor;
import org.getspout.commons.command.CommandSender;

public final class AddonCommand extends Command {
	private final Addon owningAddon;
	private CommandExecutor executor;

	protected AddonCommand(String name, Addon owner) {
		super(name);
		this.executor = owner;
		this.owningAddon = owner;
		this.usageMessage = "";
	}

	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		boolean success = false;

		if (!this.owningAddon.isEnabled()) {
			return false;
		}
		try {
			success = this.executor.onCommand(sender, this, commandLabel, args);
		} catch (Throwable ex) {
			throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.owningAddon.getDescription().getFullName(), ex);
		}

		if ((!success) && (this.usageMessage.length() > 0)) {
			for (String line : this.usageMessage.replace("<command>", commandLabel).split("\n")) {
				sender.sendMessage(line);
			}
		}

		return success;
	}

	public void setExecutor(CommandExecutor executor) {
		this.executor = executor;
	}

	public CommandExecutor getExecutor() {
		return this.executor;
	}

	public Addon getAddon() {
		return this.owningAddon;
	}
}
