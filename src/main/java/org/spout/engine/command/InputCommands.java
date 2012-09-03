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

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.command.Command;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandExecutor;
import org.spout.api.command.CommandSource;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.exception.CommandException;
import org.spout.api.plugin.Platform;

/**
 * Class to create the input command structure that modifies {@link org.spout.api.entity.state.PlayerInputState}
 */
public class InputCommands {
	public static void setupInputCommands(Engine engine, Command parent) {
		for (PlayerInputState.Flags flag : PlayerInputState.Flags.values()) {
			parent.addSubCommand(engine, "+" + flag.name())
					.setArgBounds(0, 0)
					.setHelp("Adds the " + flag.name() + " flag to the calling player's input state")
					.setExecutor(Platform.ALL, new InputFlagHandler(flag, true));
			parent.addSubCommand(engine, "-" + flag.name())
					.setArgBounds(0, 0)
					.setHelp("Removes the " + flag.name() + " flag from the calling player's input state")
					.setExecutor(Platform.ALL, new InputFlagHandler(flag, false));
		}
		parent.addSubCommand(engine, "+dx")
				.setHelp("Adds the x distance traveled to the calling player's input state.")
				.setExecutor(Platform.ALL, new InputMouseHandler(true));
		parent.addSubCommand(engine, "+dy")
				.setHelp("Adds the y distance traveled to the calling player's input state.")
				.setExecutor(Platform.ALL, new InputMouseHandler(false));
	}

	public static class InputFlagHandler implements CommandExecutor {
		private final PlayerInputState.Flags flag;
		private final boolean add;

		public InputFlagHandler(PlayerInputState.Flags flag, boolean add) {
			this.flag = flag;
			this.add  = add;
		}

		@Override
		public void processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
			if (Spout.getPlatform().equals(Platform.CLIENT)) {
				if (!(source instanceof Player)) {
					throw new CommandException("Source must be a player!");
				}
				Player player = (Player) source;
				if (add) {
					player.processInput(player.input().withAddedFlag(flag));
				} else {
					player.processInput(player.input().withRemovedFlag(flag));
				}
			} else if (Spout.getPlatform().equals(Platform.SERVER)) {
				// TODO server handling
			}
		}
	}

	public static class InputMouseHandler implements CommandExecutor {
		private final boolean x;

		public InputMouseHandler(boolean x) {
			this.x = x;
		}

		@Override
		public void processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
			if (Spout.getPlatform().equals(Platform.CLIENT)) {
				if (!(source instanceof Player)) {
					throw new CommandException("Source must be a player!");
				}
				int d;
				try {
					d = args.getInteger(0);
				} catch (NumberFormatException numberFormatException) {
					throw new IllegalArgumentException("Cannot add a non-number to mouse distances.");
				}
				Player player = (Player) source;
				if (x) {
					player.processInput(player.input().withAddedYaw(PlayerInputState.MOUSE_SENSITIVITY * d));
				} else {
					player.processInput(player.input().withAddedPitch(PlayerInputState.MOUSE_SENSITIVITY * d));
				}
			} else if (Spout.getPlatform().equals(Platform.SERVER)) {
				// TODO server handling
			}
		}
	}
}
