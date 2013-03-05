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

import org.spout.api.Engine;
import org.spout.api.command.Command;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandExecutor;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Binding;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.exception.CommandException;
import org.spout.api.gui.Screen;
import org.spout.api.input.Keyboard;

import org.spout.engine.SpoutClient;

/**
 * Class to create the input command structure that modifies {@link org.spout.api.entity.state.PlayerInputState}
 */
public class InputCommands {
	private final SpoutClient client;
	public InputCommands(SpoutClient client) {
		this.client = client;
	}

	@org.spout.api.command.annotated.Command(aliases = "dev_console", desc = "Toggle display of debugging info.", min = 1, max = 1)
	@Binding(keys = Keyboard.KEY_F2)
	public void devConsole(CommandContext args, CommandSource source) {
		if (!args.getString(0).equalsIgnoreCase("+")) {
			return;
		}
		final Screen consoleScreen = (Screen) client.getScreenStack().getConsole();
		client.getScheduler().enqueueRenderTask(new Runnable() {
			public void run() {
				if (client.getScreenStack().isOpen(consoleScreen)) {
					client.getScreenStack().closeScreen(consoleScreen);
				} else {
					client.getScreenStack().openScreen(consoleScreen);
				}
			}
		});
	}

	@org.spout.api.command.annotated.Command(aliases = "debug_info", desc = "Toggle display of debugging info.", min = 1, max = 1)
	@Binding(keys = Keyboard.KEY_F3)
	public void debugInfo(CommandContext args, CommandSource source) {
		if (!args.getString(0).equalsIgnoreCase("+")) {
			return;
		}
		final Screen debugScreen = (Screen) client.getScreenStack().getDebug();
		client.getScheduler().enqueueRenderTask(new Runnable() {
			public void run() {
				if (client.getScreenStack().isOpen(debugScreen)) {
					client.getScreenStack().closeScreen(debugScreen);
				} else {
					client.getScreenStack().openScreen(debugScreen);
				}
			}
		});
	}

	public static void setupInputCommands(Engine engine, Command parent) {
		for (PlayerInputState.Flags flag : PlayerInputState.Flags.values()) {
			parent.addSubCommand(engine, flag.name())
					.setArgBounds(1, 1)
					.setHelp("Adds the " + flag.name() + " flag to the calling player's input state")
					.setExecutor(new InputFlagHandler(flag));
		}
		parent.addSubCommand(engine, "dx")
				.setHelp("Adds the x distance traveled to the calling player's input state.")
				.setExecutor(new InputMouseYawHandler())
				.setArgBounds(1, 1);
		parent.addSubCommand(engine, "dy")
				.setHelp("Adds the y distance traveled to the calling player's input state.")
				.setExecutor(new InputMousePitchHandler())
				.setArgBounds(1, 1);
	}

	public static class InputFlagHandler implements CommandExecutor {
		private final PlayerInputState.Flags flag;

		public InputFlagHandler(PlayerInputState.Flags flag) {
			this.flag = flag;
		}

		@Override
		public void processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
			if (!(source instanceof Player)) {
				throw new CommandException("Source must be a player!");
			}
			Player player = (Player) source;
			if (args.getString(0).equalsIgnoreCase("+")) {
				player.processInput(player.input().withAddedFlag(flag));
			} else {
				player.processInput(player.input().withRemovedFlag(flag));
			}
		}
	}

	public static class InputMousePitchHandler implements CommandExecutor {
		@Override
		public void processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
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
			player.processInput(player.input().withAddedPitch(PlayerInputState.MOUSE_SENSITIVITY * d));
		}
	}

	public static class InputMouseYawHandler implements CommandExecutor {
		@Override
		public void processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
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
			player.processInput(player.input().withAddedYaw(PlayerInputState.MOUSE_SENSITIVITY * -d));
		}
	}
}
