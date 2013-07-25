/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.command;

import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.CommandDescription;
import org.spout.api.exception.ArgumentParseException;
import org.spout.api.exception.CommandException;
import org.spout.api.input.Binding;
import org.spout.api.input.Keyboard;

import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutEngine;

public class ClientCommands extends CommonCommands {
	public ClientCommands(SpoutEngine engine) {
		super(engine);
	}

	@Override
	public SpoutClient getEngine() {
		return (SpoutClient) super.getEngine();
	}

	@CommandDescription(aliases = {"bind"}, usage = "bind <key> <command>", desc = "Binds a command to a key")
	public void bind(CommandSource source, CommandArguments args) throws CommandException {
		Keyboard key = args.popEnumValue("key", Keyboard.class);
		String command = args.popString("command");
		args.assertCompletelyParsed();
		getEngine().getInputManager().bind(new Binding(command, key));
	}

	@CommandDescription(aliases = {"say", "chat"}, usage = "[message]", desc = "Say something!")
	public void clientSay(CommandSource source, CommandArguments args) throws CommandException {
		String message = args.popRemainingStrings("message");
		getEngine().getCommandSource().sendMessage(message);
	}

	@CommandDescription(aliases = {"clear"}, usage = "[message]", desc = "Clear the client's console")
	public void consoleClear(CommandSource source, CommandArguments args) throws ArgumentParseException {
		args.assertCompletelyParsed();
		getEngine().getScreenStack().getConsole().clearConsole();
	}
}
