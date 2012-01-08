/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.server.command;

import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandException;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.server.SpoutServer;

/**
 * Commands related to server administration
 */
public class AdministrationCommands {
	private final SpoutServer server;

	public AdministrationCommands(SpoutServer server) {
		this.server = server;
	}
	
	@Command(aliases = "stop", usage = "[message]", desc = "Stop the server!", max = -1)
	public void stop(CommandContext args, CommandSource source) throws CommandException {
		String message = "Server shutting down";
		if (args.length() > 0) {
			message = args.getJoinedString(0);
		}

		server.stop(message);
	}
}
