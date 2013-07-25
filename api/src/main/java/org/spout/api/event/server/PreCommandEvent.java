/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.api.event.server;

import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.event.Cancellable;
import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * This event is called before a command goes through the full command handling process.</br> This can be used for command blocking, or detecting anything the player is typing as a command that may
 * not be registered yet.</br>
 */
public class PreCommandEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private String command;
	private CommandArguments args;
	private CommandSource source;

	public PreCommandEvent(CommandSource source, String command, String... args) {
		this.source = source;
		this.command = command;
		this.args = new CommandArguments(command, args);
	}

	public CommandSource getCommandSource() {
		return source;
	}

	/**
	 * Gets the first component of the command.
	 *
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
		this.args = new CommandArguments(this.command, args.get());
	}

	/**
	 * Gets the full command message issued to the server.
	 *
	 * @return message sent.
	 */
	public CommandArguments getArguments() {
		return args;
	}

	/**
	 * Sets the command message that was sent to the server.
	 */
	public void setArguments(CommandArguments args) {
		this.args = args;
	}

	/**
	 * Sets the command message that was sent to the server.
	 */
	public void setArguments(String... args) {
		setArguments(new CommandArguments(command, args));
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
