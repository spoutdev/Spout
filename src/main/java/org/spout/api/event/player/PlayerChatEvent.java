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
package org.spout.api.event.player;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatTemplate;
import org.spout.api.chat.Placeholder;
import org.spout.api.entity.Player;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;

/**
 * Called when a player speaks in chat.
 * Implements {@link Cancellable}. Canceling this event will prevent the message from being sent to other players.
 */
public class PlayerChatEvent extends PlayerEvent implements Cancellable {
	public static final Placeholder NAME = new Placeholder("name"), MESSAGE = new Placeholder("message");
	private static final HandlerList handlers = new HandlerList();
	private ChatArguments message;
	private ChatTemplate format = new ChatTemplate(new ChatArguments("<", NAME, "> ", MESSAGE));

	public PlayerChatEvent(Player p, Object... message) {
		super(p);
		this.message = new ChatArguments(message);
	}

	/**
	 * Gets the message that the player will send.
	 * @return The message of the player.
	 */
	public ChatArguments getMessage() {
		return message;
	}

	/**
	 * Overrides the sent message.
	 * @param message The message to set
	 */
	public void setMessage(ChatArguments message) {
		this.message = message;
	}

	/**
	 * Gets the message format that will parse out the message text for broadcasting.
	 * @return The message format
	 */
	public ChatTemplate getFormat() {
		return format;
	}

	/**
	 * Sets the message's format to {@code format}. <br/>
	 * Verification is performed to make sure that the ChatArguments has both the
	 * {@link #NAME name} and {@link #MESSAGE message} placeholders <br/>
	 * <p/>
	 * If verification of the format fails the format will not change.
	 * @param format The format to set.
	 * @return true if the format was valid, otherwise false.
	 */
	public boolean setFormat(ChatArguments format) {
		if (!(format.hasPlaceholder(NAME) && format.hasPlaceholder(MESSAGE))) {
			return false;
		}
		this.format = new ChatTemplate(format);
		return true;
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
