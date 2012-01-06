/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event.player;

import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.player.Player;

/**
 * Called when a player speaks in chat.
 */
public class PlayerChatEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private String message;
	private String format = "<%1$s> %2$s";
	
	public PlayerChatEvent(Player p, String message) {
		super(p);
		this.message = message;
	}
	
	/**
	 * Gets the message that the player sent.
	 *
	 * @return The message of the player.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Overrides the sent message.
	 *
	 * @param message The message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the format that will be broadcasted.
	 * @return The message format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the message's format to {@code format}
	 * Verification is performed to make sure that the string has at least two string
	 * formatting positions.
	 * @param format The format to set.
	 * @return Whether the format was valid.
	 */
	public boolean setFormat(String format) {
		try {
			String.format(format, player.getName(), message);
		} catch (Throwable t) {
			return false;
		}
		this.format = format;
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
