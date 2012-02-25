/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.command;

import org.spout.api.exception.CommandException;

/**
 *
 * @author zml2008
 */
public interface RawCommandExecutor {
	/**
	 * Executes a command based on the provided arguments.
	 *
	 * The base index is equal to the number of arguments that have already been
	 * processed by super commands.
	 *
	 * @param source the {@link CommandSource} that sent this command.
	 * @param args the command arguments
	 * @param baseIndex the arguments that have already been processed by
	 * @param fuzzyLookup Whether to use levenschtein distance while looking up
	 *            commands.
	 * @throws CommandException when an issue occurred with the command
	 */
	public void execute(CommandSource source, String[] args, int baseIndex, boolean fuzzyLookup) throws CommandException;
}
