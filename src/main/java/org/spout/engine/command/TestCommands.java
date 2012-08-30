/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.command;

import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;

import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.engine.SpoutEngine;

public class TestCommands {
	private final SpoutEngine engine;
	public TestCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = {"dbg"}, desc = "Debug Output")
	public void debugOutput(CommandContext args, CommandSource source) {
		World world = engine.getDefaultWorld();
		source.sendMessage("World Entity count: ", world.getAll().size());
	}


	@Command(aliases = "testmsg", desc = "Test extracting chat styles from a message and printing them")
	public void testMsg(CommandContext args, CommandSource source) throws CommandException {
		source.sendMessage(args.getJoinedString(0));
	}
}
