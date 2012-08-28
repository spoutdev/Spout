/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import java.util.List;

import org.spout.api.chat.ChatSection;
import org.spout.api.chat.completion.CompletionRequest;
import org.spout.api.chat.completion.CompletionResponse;
import org.spout.api.exception.CommandException;
import org.spout.api.exception.SpoutRuntimeException;
import org.spout.api.util.Named;

public class RootCommand extends SimpleCommand {


	public RootCommand(Named owner) {
		super(owner, "root" + owner.getName());
	}

	@Override
	public String getUsage(String name, List<ChatSection> args, int baseIndex) {
		return "Command '" + name + "' could not be found!";
	}

	public void execute(CommandSource source, String name, List<ChatSection> args, boolean fuzzyLookup) throws CommandException {
		execute(source, name, args, -1, fuzzyLookup);
	}

	@Override
	public Command closeSubCommand() {
		throw new SpoutRuntimeException("The root command has no parent.");
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	@Override
	public CommandException getMissingChildException(String usage) {
		return new CommandException(usage);
	}

	@Override
	public CompletionResponse getCompletion(CompletionRequest input) {
		return getCompletion(input, -1);
	}
}
