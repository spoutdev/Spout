/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.command.filter;

import org.spout.api.command.Command;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.exception.CommandException;

/**
 * Filters a command execution
 *
 * @param <T> type of command source to check for
 */
public abstract class CommandSourceFilter<T extends CommandSource> implements CommandFilter {
	private final Class<T> type;

	public CommandSourceFilter(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public void validate(Command command, CommandSource source, CommandArguments args) throws CommandException {
		if (!type.isAssignableFrom(source.getClass())) {
			throw new CommandException("You must be a " + type.getSimpleName() + " to execute this command.");
		}
	}
}
